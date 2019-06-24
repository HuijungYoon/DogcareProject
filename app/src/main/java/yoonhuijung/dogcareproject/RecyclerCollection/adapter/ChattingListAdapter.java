package yoonhuijung.dogcareproject.RecyclerCollection.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import yoonhuijung.dogcareproject.Interface.ClickListener;
import yoonhuijung.dogcareproject.get.Getchatlists;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class ChattingListAdapter extends RecyclerView.Adapter<ChattingListAdapter.ChattingviewHolder> {

    private Context context;
    private ArrayList<Getchatlists> chattingListItems;
    private final ClickListener listener;

    public ChattingListAdapter(Context context, ArrayList<Getchatlists> chattingListItems, ClickListener listener) {
        this.context = context;
        this.chattingListItems = chattingListItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChattingviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_listchat, viewGroup,false);
        return new ChattingviewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingviewHolder chattingviewHolder, int position) {
        Getchatlists chattingListItem = chattingListItems.get(position);
        Uri uri = Uri.parse(chattingListItem.getSender_image());
        chattingviewHolder.sender_txt.setText(chattingListItem.getSender_chat());
        chattingviewHolder.msg_txt.setText(chattingListItem.getMsg_chat());
        Glide.with(context).load(uri).into(chattingviewHolder.chat_profile_img);
    }

    @Override
    public int getItemCount() {
        return chattingListItems.size();
    }

    public class ChattingviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView sender_txt;
        TextView msg_txt;
        ImageView chat_profile_img;
        private WeakReference<ClickListener> listenerRef;
        public ChattingviewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            sender_txt = (TextView)itemView.findViewById(R.id.list_sender);
            msg_txt = (TextView)itemView.findViewById(R.id.list_msg);
            chat_profile_img = (ImageView)itemView.findViewById(R.id.chat_list_profile_img);
            sender_txt.setOnClickListener(this);
            msg_txt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == msg_txt.getId() || v.getId() == sender_txt.getId())
            {
                Toast.makeText(context, "ITEM PRESSED = " +String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Row PRESSED = " +String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            listenerRef.get().OnPositionClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
