package edu.ucsb.cs.cs184.wetube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import edu.ucsb.cs.cs184.wetube.R;

//The activity which plays has YouTubePlayerView inside layout must extend YouTubeBaseActivity
//implement OnInitializedListener to get the state of the player whether it has succeed or failed
//to load
public class PlayerActivity extends YouTubeBaseActivity implements OnInitializedListener, FirebaseAuth.AuthStateListener {

    private YouTubePlayerView playerView;


    String roomKey;
    Button createRoom;
    String ownerId;
    String url;

    private static DatabaseReference my_db_ref;
    FirebaseUser currentUser;
//        if(currentUser != null && ownerId.equals(currentUser.getUid())){

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
    }
    //Overriding onCreate method(first method to be called) to create the activity 
    //and initialise all the variable to their respective views in layout file and
    //adding listeners to required views
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //method to fill the activity that is launched with  the activity_player.xml layout file
        setContentView(R.layout.activity_player);

        //getting youtube player view object
        playerView = (YouTubePlayerView)findViewById(R.id.player_view);

        createRoom = findViewById(R.id.createroom);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ownerId = currentUser.getUid();


        //initialize method of YouTubePlayerView used to play videos and control video playback
        //arguments are a valid API key that is enabled to use the YouTube Data API v3 service
        //and YouTubePlayer.OnInitializedListener object or the callbacks that will be invoked 
        //when the initialization succeeds or fails
        //as in this case the activity implements OnInitializedListener
        playerView.initialize(YoutubeConnector.KEY, this);

        //initialising various descriptive data in the UI and player
        TextView video_title = (TextView)findViewById(R.id.player_title);
        TextView video_desc = (TextView)findViewById(R.id.player_description);
        TextView video_id = (TextView)findViewById(R.id.player_id);

        //setting text of each View form UI
        //setText method used to change the text shown in the view
        //getIntent method returns the object of current Intent 
        //of which getStringExtra returns the string which was passed while calling the intent
        //by using the name that was associated during call
        video_title.setText(getIntent().getStringExtra("VIDEO_TITLE"));
        video_id.setText("Video ID : "+(getIntent().getStringExtra("VIDEO_ID")));
        video_desc.setText(getIntent().getStringExtra("VIDEO_DESC"));

        url = (getIntent().getStringExtra("VIDEO_ID"));


//        my_db_ref = FirebaseDatabase.getInstance().getReference("room");
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRoomActivity.Room newRoom = new CreateRoomActivity.Room();

                String link = getIntent().getStringExtra("VIDEO_ID");
//
//                newRoom.url = link;
//                newRoom.position = 0;
////                newRoom.roomId = 1;
//                newRoom.ownerId = ownerId;

//                roomKey = my_db_ref.push().getKey();
//                newRoom.roomKey = roomKey;
//                my_db_ref = FirebaseDatabase.getInstance().getReference("room");
//                my_db_ref.child(roomKey).setValue(newRoom);
                Intent room = new Intent(PlayerActivity.this,CreateRoomActivity.class);
                room.putExtra("url",url);
                startActivity(room);
                Log.v("fb","Link is: " + link);

            }
        });
    }

    //method called if the YouTubePlayerView fails to initialize
    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    //method called if the YouTubePlayerView succeeds to load the video
    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean restored) {
        
        //initialise the video player only if it is not restored or is not yet set
        if(!restored){

            //cueVideo takes video ID as argument and initialise the player with that video
            //this method just prepares the player to play the video
            //but does not download any of the video stream until play() is called
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
        }
    }


}