package edu.ucsb.cs.cs184.wetube;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ucsb.cs.cs184.wetube.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class RoomActivity extends YouTubeBaseActivity implements FirebaseAuth.AuthStateListener {

    // Room
    Boolean isOwner = false;
    private void enterRoom(){}

    // Youtube player
    YouTubePlayerView movieView;
    Button start;
    Button dismiss;
    String movieURL;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private static DatabaseReference my_db_ref;
    // Chat
    Button send;
    Query sChatQuery;
    RecyclerView mRecyclerView;
    EditText mMessageEdit;
    FirebaseRecyclerAdapter adapter;
    String key;
    YouTubePlayer player;
    Thread thread;
    Boolean roomExits;
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent curr = new Intent(this, HomeActivity.class);
        startActivity(curr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        roomExits = true;
        Intent i = getIntent();
        key = i.getStringExtra("rk");
        Log.v("key","key is: "+key);
        // Youtube player
        dismiss = (Button)findViewById(R.id.dismiss);
        start = (Button)findViewById(R.id.playMovie);
        movieView = (YouTubePlayerView)findViewById(R.id.youtubeview);
        // Chat
        send = (Button)findViewById(R.id.sendButton);

        sChatQuery = FirebaseDatabase.getInstance().getReference().child("room").child(key).child("chats").limitToLast(50);
        mRecyclerView = (RecyclerView)findViewById(R.id.messagesList);
        mMessageEdit = (EditText)findViewById(R.id.messageEdit);

        // Initialize Chat RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });

        // Initialize Chat RecyclerView Adapter
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(sChatQuery, Chat.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(options) {
            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                holder.bind(model);
            }
        };

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(positionStart);
            }
        });
        // Set adapter
        mRecyclerView.setAdapter(adapter);

        movieView = (YouTubePlayerView)findViewById(R.id.youtubeview);
        my_db_ref = FirebaseDatabase.getInstance().getReference("room");
        my_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        Log.v("keys",userSnapshot.getKey());
                        if(userSnapshot.getKey().equals(key)) {
                            Log.v("fbfind","find it");
                            CreateRoomActivity.Room room = userSnapshot.getValue(CreateRoomActivity.Room.class);
                            movieURL = room.url;
                            Log.v("url",movieURL);

                        }else{
                            Log.v("fbfind","didnt find it "+key);

                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Initialize Youtube player
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.v("fb","Done init");
                String link;
//                youTubePlayer.loadVideo("WVP3fUzQHcg");
//                my_db_ref.child("room").child(key);
//                youTubePlayer.loadVideo("WVP3fUzQHcg",15000);
                player = youTubePlayer;
                player.loadVideo(movieURL);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.v("fb","fail to init");
            }
        };

        // Set buttons
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("fb","initializing youtube player");
                movieView.initialize(YouTubeConfig.getApiKey(), onInitializedListener);

            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                my_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                Log.v("keys",userSnapshot.getKey());
                                if(userSnapshot.getKey().equals(key)) {
                                    userSnapshot.getRef().removeValue();

                                }else{
                                    Log.v("fbfind","didnt find it "+key);

                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                DatabaseReference chat = FirebaseDatabase.getInstance().getReference("chats");
//                chat.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        if(dataSnapshot.exists()){
//                            dataSnapshot.getRef().removeValue();
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                roomExits = false;

                Log.v("fb","cancel is clicked");
                Intent dismissRoom = new Intent(RoomActivity.this,HomeActivity.class);
                startActivity(dismissRoom);

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String name = "User " + uid.substring(0, 6);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("room")
                        .child(key)
                        .child("chats")
                        .push()
                        .setValue(new  Chat(name, mMessageEdit.getText().toString(), uid)
                        );
                mMessageEdit.setText("");
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        FirebaseAuth.getInstance().removeAuthStateListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final CreateRoomActivity.Room newroom = new CreateRoomActivity.Room();

        adapter.startListening();
        FirebaseAuth.getInstance().addAuthStateListener(this);

        my_db_ref = FirebaseDatabase.getInstance().getReference("room");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(roomExits){
                    if(player != null) {
                        my_db_ref.child(key).child("position").setValue(player.getCurrentTimeMillis());
                    }

                    try{
                        Thread.sleep((1000));
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
}





