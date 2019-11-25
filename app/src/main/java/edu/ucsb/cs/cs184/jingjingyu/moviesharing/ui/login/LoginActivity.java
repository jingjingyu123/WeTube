package edu.ucsb.cs.cs184.jingjingyu.moviesharing.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.ucsb.cs.cs184.jingjingyu.moviesharing.R;
import edu.ucsb.cs.cs184.jingjingyu.moviesharing.ui.login.LoginViewModel;
import edu.ucsb.cs.cs184.jingjingyu.moviesharing.ui.login.LoginViewModelFactory;
import edu.ucsb.cs.cs184.jingjingyu.moviesharing.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //authentication
        mAuth=FirebaseAuth.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        //sign up and log in
        final Button signupButton = findViewById(R.id.signUp);
        final Button loginButton = findViewById(R.id.logIn);

        final ProgressBar loadingProgressBar = findViewById(R.id.loading);



        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                signupButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });


        /*
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

         */

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        //tap the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                //read info about username and password
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                //check database information
                mAuth.signInWithEmailAndPassword(usernameEditText.getText().toString(),passwordEditText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //if database has information
                                if(task.isSuccessful()){
                                    Log.d("login","success");
                                    FirebaseUser user=mAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                                    //start the next activity
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            //if database does not have information
                                }else{
                                    Log.w("login","failure");
                                    Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
        });

        //tap the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                //add info to auth firebase
                mAuth.createUserWithEmailAndPassword(usernameEditText.getText().toString(),passwordEditText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //if create successful in database
                                if(task.isSuccessful()){
                                    Log.d("signup","success");
                                    //FirebaseUser user=mAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "welcome", Toast.LENGTH_LONG).show();
                                    //start the next activity
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    //if database does not have information
                                }else{
                                    Log.w("login","failure");
                                    Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_LONG).show();

                                }
                            }
                        });;
            }
        });
    }

    /*
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
     */
}
