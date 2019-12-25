package edu.ucsb.cs.cs184.wetube;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.ucsb.cs.cs184.wetube.R;


public class SearchRoomActivity extends AppCompatActivity {

        //EditText for input search keywords
        private EditText searchInput;
        private EditText password;
        private Button searchButton;
        private static DatabaseReference my_db_ref;
        private  Vector<CreateRoomActivity.Room> roomList;
        private Context cur;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent curr = new Intent(this, HomeActivity.class);
        startActivity(curr);
    }


    @Override
        protected void onCreate(Bundle savedInstanceState) {

            //calling parent class to recall the app's last state
            super.onCreate(savedInstanceState);
            cur = this;

            //method to fill the activity that is launched with  the activity_searchch.xml layout file
            setContentView(R.layout.activity_room_search);

            //initailising the objects with their respective view in activity_search.xmlml file
            searchInput = (EditText)findViewById(R.id.search_room_name);
            password = findViewById(R.id.search_room_pw);
            searchButton = findViewById(R.id.button_search);
            roomList = new Vector<>();

            my_db_ref = FirebaseDatabase.getInstance().getReference("room");
            my_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            if(userSnapshot.exists()) {
                                CreateRoomActivity.Room room = userSnapshot.getValue(CreateRoomActivity.Room.class);
                                roomList.add(room);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = searchInput.getText().toString();
                            String pw = password.getText().toString();
                            boolean found = false;
                            for (CreateRoomActivity.Room rm : roomList){
                                if(rm.roomName.equals(name)){
                                    if (rm.pri){
                                        if (!pw.equals(rm.password)){
                                            Log.v("password",pw);
                                            Log.v("room",rm.password);
                                            Toast.makeText(SearchRoomActivity.this, "Woops! Please recheck your password!", Toast.LENGTH_LONG).show();
                                            break;
                                        }

                                    }
                                    String roomk = rm.roomKey;
                                    String ownerId = rm.ownerId;
                                    Intent intToMain;
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if(currentUser != null && ownerId.equals(currentUser.getUid())){
                                        intToMain = new Intent(cur, RoomActivity.class);
                                    }else {
                                        intToMain = new Intent(cur, ViewerRoomActivity.class);
                                    }
                                    intToMain.putExtra("rk", roomk);
                                    found = true;
                                    startActivity(intToMain);
                                    break;
                                }
                            }
                            if (!found){Toast.makeText(SearchRoomActivity.this, "Woops! No such room!", Toast.LENGTH_LONG).show();}

                        }
                    });
                }
            });

        }

    }
