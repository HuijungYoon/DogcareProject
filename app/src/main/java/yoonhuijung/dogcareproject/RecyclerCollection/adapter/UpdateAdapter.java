package yoonhuijung.dogcareproject.RecyclerCollection.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import yoonhuijung.dogcareproject.RecyclerCollection.item.Updateitem;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.PostViewHolder>   {

    private Context context;
    private ArrayList<Updateitem> updateitems;

    public UpdateAdapter(Context context, ArrayList<Updateitem> updateitems) {
        this.context = context;
        this.updateitems = updateitems;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_carrer_item, parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Updateitem updateitem = updateitems.get(position);
        //holder.postImage.setImageResource(updateitem.getImage_acc());
        Glide.with(context).load(updateitem.getImage_acc()).into(holder.postImage);
        holder.postTitle.setText(updateitem.getTitle());
        holder.postDescription.setText(updateitem.getContents());

    }

    @Override
    public int getItemCount() {
        return updateitems.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView postImage;
        TextView postTitle;
        TextView postDescription;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = (ImageView)itemView.findViewById(R.id.postimage);
            postTitle = (TextView)itemView.findViewById(R.id.postTitle);
            postDescription = (TextView)itemView.findViewById(R.id.postDescription);
        }
    }




}
