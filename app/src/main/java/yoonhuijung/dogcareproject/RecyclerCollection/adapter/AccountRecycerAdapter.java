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

import yoonhuijung.dogcareproject.RecyclerCollection.item.Updateitem;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class AccountRecycerAdapter extends RecyclerView.Adapter<AccountRecycerAdapter.ItemViewHolder> {

   ArrayList<Updateitem> mItems; //아이템 어레이리스트
    private Context context;
    public AccountRecycerAdapter(Context context,ArrayList<Updateitem> mItems) {
        this.mItems = mItems;
        this.context = context;
    }


    //새로운 뷰홀더 생성
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_carrer_item, parent,false);
        return new ItemViewHolder(view);
    }

    //View 의 내용을 해당 포지션의 데이터로 바꾼다.
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Updateitem updateitem = mItems.get(position);
        Glide.with(context).load(updateitem.getImage_acc()).into(holder.postImage);
        holder.postTitle.setText(updateitem.getTitle());
        holder.postDescription.setText(updateitem.getContents());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTitle;
        TextView postDescription;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = (ImageView)itemView.findViewById(R.id.postimage);
            postTitle = (TextView)itemView.findViewById(R.id.postTitle);
            postDescription = (TextView)itemView.findViewById(R.id.postDescription);
        }
    }
}
