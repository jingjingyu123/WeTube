package edu.ucsb.cs.cs184.wetube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ucsb.cs.cs184.wetube.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class HomeActivity extends AppCompatActivity implements MyAdapter.OnRoomListner, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener  {
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static DatabaseReference my_db_ref;
    Vector<CreateRoomActivity.Room> roomList;
    Vector<CreateRoomActivity.Room> visibleRoomList;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    Context cur = this;



//    FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
//        Toast.makeText(getContext(), "clicked label: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:{
                AuthUI.getInstance().signOut(cur).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeActivity.this, "See ya ~ !", Toast.LENGTH_LONG).show();
                        // Close activity
                        finish();
                            }
                        });
                break;
        }
            case 1:
                Intent curr = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(curr);
                break;
            case 2:
                Intent intToMain;
                intToMain = new Intent(this, SearchRoomActivity.class);
                startActivity(intToMain);
            case 3:
                Intent se = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(se);
                break;

        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
//        Toast.makeText(getContext(), "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:{
                AuthUI.getInstance().signOut(cur).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeActivity.this, "See ya ~ !", Toast.LENGTH_LONG).show();
                        // Close activity
                        finish();
                    }
                });
                break;
            }
            case 1:
                Intent curr = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(curr);
                break;
            case 2:
                Intent sr;
                sr = new Intent(this, SearchRoomActivity.class);
                startActivity(sr);
                break;
            case 3:
                Intent se = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(se);
                break;
        }
        rfabHelper.toggleContent();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.listOfRoom);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        roomList = new Vector<>();
        visibleRoomList = new Vector<>();

        Log.v("fb","home activity start");

        rfaLayout = findViewById(R.id.activity_main_rfal);
        rfaBtn = findViewById(R.id.activity_main_rfab);

        mFirebaseAuth = FirebaseAuth.getInstance();

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(cur);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Log Out")
                .setResId(R.drawable.logout)
                .setIconNormalColor(0xff283593)
                .setIconPressedColor(0xff1a237e)
                .setLabelColor(0xff283593)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Refresh Room List")
                .setResId(R.drawable.fresh)
                .setIconNormalColor(0xff4e342e)
                .setIconPressedColor(0xff3e2723)
//                .setLabelColor(Color.WHITE)
                .setLabelSizeSp(14)
//                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(context, 4)))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Search Room")
                .setResId(R.drawable.search)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Search YouTube & Create New Room")
                .setResId(R.drawable.create)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setLabelColor(0xffd84315)
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(dip2px(cur, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(dip2px(cur, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                cur,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
//                    Toast.makeText(HomeActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(HomeActivity.this,"Please Login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        };


        my_db_ref = FirebaseDatabase.getInstance().getReference("room");
        final MyAdapter.OnRoomListner or = this;
        my_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        if(userSnapshot.exists()) {
                            CreateRoomActivity.Room room = userSnapshot.getValue(CreateRoomActivity.Room.class);
//                            Log.v("roomkey",room.roomKey);
                            roomList.add(room);
                            if(room.pri == false){visibleRoomList.add(room);}
                            MyAdapter input = new MyAdapter(cur, visibleRoomList, or);
                                Log.v("fbb","after new adapter");
                                recyclerView.setAdapter(input);
                            }else{
                                Log.v("fb","doesnt exist");
                            }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.v("fb","Size is: "+Integer.toString(roomList.size()));


    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onRoomClick(int position) {
        String roomk = visibleRoomList.get(position).roomKey;
        String ownerId = visibleRoomList.get(position).ownerId;
        Intent intToMain;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.v("owner", currentUser.getUid());
        Log.v("current", ownerId);


        if(currentUser != null && ownerId.equals(currentUser.getUid())){
            intToMain = new Intent(this, RoomActivity.class);

        }else {
            intToMain = new Intent(this, ViewerRoomActivity.class);
        }

        Log.v("pos",Integer.toString(roomList.get(position).roomKey.length()));
        if(!roomk.isEmpty()){
            Log.v("keyHome", roomList.get(position).roomKey);
            intToMain.putExtra("rk", roomk);
        }else{
            Log.v("keyHome", "Nothing");
        }
        startActivity(intToMain);
    }

}
