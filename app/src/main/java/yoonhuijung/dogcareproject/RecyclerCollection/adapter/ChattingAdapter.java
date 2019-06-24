package yoonhuijung.dogcareproject.RecyclerCollection.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import yoonhuijung.dogcareproject.Interface.ClickListener;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import yoonhuijung.dogcareproject.RecyclerCollection.item.Chat_item;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ChattingViewHolder> {
    private Context context;
    private ArrayList<Chat_item> chatItemArrayList;
    private final ClickListener listener;

    public ChattingAdapter(Context context, ArrayList<Chat_item> chatItemArrayList,ClickListener listener) {
        this.context = context;
        this.chatItemArrayList = chatItemArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_chat, viewGroup,false);

        return new ChattingViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingViewHolder chattingViewHolder, int position) {
        Chat_item chat_item = chatItemArrayList.get(position);

       //내꺼일떄
        if(chat_item.isMe)
        {
            chattingViewHolder.chatmessageme.setText(chat_item.getMessagechatting());
            chattingViewHolder.linearLayoutme.setVisibility(View.VISIBLE);
            chattingViewHolder.linearLayoutyou.setVisibility(View.GONE);
        }
        //남이전송한거일때
        else{
            chattingViewHolder.chatmessage.setText(chat_item.getMessagechatting());
            chattingViewHolder.Nickname.setText(chat_item.getNickname());
            chattingViewHolder.linearLayoutme.setVisibility(View.GONE);
            chattingViewHolder.linearLayoutyou.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return chatItemArrayList.size();
    }

    public class ChattingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView chatmessage;
        TextView chatmessageme;
        TextView Nickname;
        LinearLayout linearLayoutme;
        LinearLayout linearLayoutyou;
        private WeakReference<ClickListener> listenerRef;

        public ChattingViewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            chatmessage= (TextView)itemView.findViewById(R.id.item_chat_txt);
            chatmessageme = (TextView)itemView.findViewById(R.id.item_chat_txt_me);
            Nickname = (TextView)itemView.findViewById(R.id.nickname_you);
            linearLayoutme = (LinearLayout)itemView.findViewById(R.id.layout_me);
            linearLayoutyou = (LinearLayout)itemView.findViewById(R.id.layout_you);

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == Nickname.getId())
            {
                Toast.makeText(context, "ITEM PRESSED = " +String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Row PRESSED = " +String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            listenerRef.get().OnPositionClicked(getAdapterPosition());
        }
    }
}
