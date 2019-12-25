package edu.ucsb.cs.cs184.wetube;

import androidx.appcompat.app.AppCompatActivity;
import edu.ucsb.cs.cs184.wetube.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Map;

public class CreateRoomActivity extends AppCompatActivity {
    EditText name;
    EditText pw;
    Button cancel, create;
    String roomKey = "";
    String url;
    Switch pri;
    Boolean isPri;
    private static FirebaseDatabase db;
    private static DatabaseReference my_db_ref;
    Context cur = this;

    //JJY
    //Map<String, Room> rooms;

    public static class Room implements Serializable {
        public String url;
        public String ownerId;
        public long position;
        public String roomKey;
        public boolean pri;
        public String roomName;
        public String password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        name = findViewById(R.id.room_name);
        pw = findViewById(R.id.room_password);
        cancel = findViewById(R.id.cancel);
        create = findViewById(R.id.create);
        pri=findViewById(R.id.is_private);
        isPri=false;

        pw.setVisibility(View.INVISIBLE);

        pri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPri = isChecked; // private if checked
                if (isChecked){
                    pw.setVisibility(View.VISIBLE);
                }else {
                    pw.setVisibility(View.INVISIBLE);
                    pw.setText("");
                }
            }
        });

        Intent i = getIntent();
        url = i.getStringExtra("url");

//        FirebaseApp mydb = FirebaseApp.initializeApp(this);
//        db = FirebaseDatabase.getInstance(mydb);
        my_db_ref = FirebaseDatabase.getInstance().getReference("room");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("fb","cancel is clicked");
                Intent createRoom = new Intent(CreateRoomActivity.this,HomeActivity.class);

                startActivity(createRoom);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Room newRoom = new Room();

                String roomName = (name.getText()).toString();
                String rmpw = pw.getText().toString();
                if(roomName.length()>20||roomName.length()<=0){
                    Toast.makeText(cur, "Invalid Link", Toast.LENGTH_SHORT).show();
                }else {
                    roomName = (name.getText()).toString();
                    //roomName = convertLink(roomName);

//                    newRoom.roomId = 1;
                    //JJY read values
                    newRoom.url = url;
                    newRoom.pri = isPri;
                    newRoom.roomName=roomName;
                    newRoom.position=0;
                    newRoom.password=rmpw;

                    //JJY put room in database
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    newRoom.ownerId = currentUser.getUid();

                    roomKey = my_db_ref.push().getKey();
                    newRoom.roomKey = roomKey;
                    my_db_ref = FirebaseDatabase.getInstance().getReference("room");
                    my_db_ref.child(roomKey).setValue(newRoom);
                    Intent rm = new Intent(CreateRoomActivity.this,RoomActivity.class);
                    rm.putExtra("rk",roomKey);
                    startActivity(rm);
                    //Log.v("fb","Link is: " + link);
                }

            }
        });
    }

    String getkey(){
        return roomKey;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("roomKey", roomKey);
        // etc.
    }



}
