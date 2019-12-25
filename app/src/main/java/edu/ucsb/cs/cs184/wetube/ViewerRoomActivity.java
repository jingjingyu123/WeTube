package edu.ucsb.cs.cs184.wetube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ucsb.cs.cs184.wetube.R;

import android.content.Context;
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


public class ViewerRoomActivity extends YouTubeBaseActivity implements FirebaseAuth.AuthStateListener {


    YouTubePlayer.OnInitializedListener onInitializedListener;
    String roomkey;
    YouTubePlayerView movieView;
    String movieURL;
    Context cur = this;
    Button send;
    Boolean roomExits;
    Thread thread;
    Query sChatQuery;
    RecyclerView mRecyclerView;
    EditText mMessageEdit;
    private static DatabaseReference my_db_ref;
    YouTubePlayer player;
    long pos;
    FirebaseRecyclerAdapter adapter;
    boolean isIni;


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
        setContentView(R.layout.activity_viewer_room);
        movieView = (YouTubePlayerView)findViewById(R.id.youtube);
        Intent i = getIntent();
        roomExits = true;
        roomkey = i.getStringExtra("rk");
        Log.v("key","key is: "+roomkey);
        //chat
        send = (Button)findViewById(R.id.sendButton);
        sChatQuery = FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("chats").limitToLast(50);
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

        my_db_ref = FirebaseDatabase.getInstance().getReference("room");

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.v("fb","Done init");

//                youTubePlayer.loadVideo("WVP3fUzQHcg");
                player = youTubePlayer;
                player.loadVideo(movieURL, (int) pos);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.v("fb","fail to init");
            }

        };
        if(pos!=0)
            movieView.initialize(YouTubeConfig.getApiKey(), onInitializedListener);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(roomExits){
                    my_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                                    Log.v("keys",userSnapshot.getKey());
                                    if(userSnapshot.getKey().equals(roomkey)) {
                                        Log.v("fbfind","find it");
                                        CreateRoomActivity.Room room = userSnapshot.getValue(CreateRoomActivity.Room.class);
                                        pos = room.position;
                                        movieURL = room.url;
                                        if(pos != 0 && isIni == false) {
                                            movieView.initialize(YouTubeConfig.getApiKey(), onInitializedListener);
                                            isIni = true;
                                        }
                                        Log.v("url",movieURL);

                                    }else{
                                        Log.v("fbfind","didnt find it "+roomkey);

                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if(isIni == true){
                    player.seekToMillis((int) pos);}
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


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String name = "User " + uid.substring(0, 6);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("room")
                        .child(roomkey)
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
        roomExits = false;
        FirebaseAuth.getInstance().removeAuthStateListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
