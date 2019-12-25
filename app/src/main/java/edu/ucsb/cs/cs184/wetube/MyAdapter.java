package edu.ucsb.cs.cs184.wetube;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import static android.app.PendingIntent.getActivity;

import edu.ucsb.cs.cs184.wetube.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<CreateRoomActivity.Room> roomListReady;
    Context homeContext;
    private OnRoomListner monroomlistner;
    String key;
//    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Log.v("adap","item is clicked");
//            Intent intToMain = new Intent(homeContext, ViewerRoomActivity.class);
//
//            intToMain.putExtra("roomkey", key);
//            homeContext.startActivity(intToMain);
//        }
//    };

    public MyAdapter(){

    }

    public MyAdapter(Context context, List<CreateRoomActivity.Room> rooms, OnRoomListner onRoomListner ){
        this.roomListReady = rooms;
        this.homeContext = context;
        this.monroomlistner = onRoomListner;
        Log.v("fbb","adapter created");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        //view.setOnClickListener(mOnClickListener);
        return new MyViewHolder(view,monroomlistner);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String url = "https://i.ytimg.com/vi/"+roomListReady.get(position).url+"/maxresdefault.jpg";

         holder.roomname.setText(roomListReady.get(position).roomName);
//        holder.roomid.setText(Integer.toString(roomListReady.get(position).roomId));
        Picasso.get()
                .load(url)
                .resize(480,270)
                .centerCrop()
                .into(holder.coverholder);;

    }

    @Override
    public int getItemCount() {
        return roomListReady.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView roomname,roomid;
        ImageView coverholder;
        OnRoomListner onRoomListner;
        public MyViewHolder(View itemView, OnRoomListner onRoomListner ){
            super(itemView);
            roomname = itemView.findViewById(R.id.roomname);
//            roomid = itemView.findViewById(R.id.roomid);
            coverholder = itemView.findViewById(R.id.cover);

            this.onRoomListner = onRoomListner;
            itemView.setOnClickListener(this );

        }

        public void bind(final CreateRoomActivity.Room item, final AdapterView.OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("adap","item is clicked");

                    Intent intToMain = new Intent(homeContext,ViewerRoomActivity.class);

                    homeContext.startActivity(intToMain);
                }
            });
        }


        @Override
        public void onClick(View view) {
            onRoomListner.onRoomClick(getAdapterPosition());
        }
    }

    public interface OnRoomListner{
        void onRoomClick(int position);
    }

}

