package kr.ac.solbridge.student.hyoon.dogcareproject.matchingrecyclerview;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import kr.ac.solbridge.student.hyoon.dogcareproject.ClickListener;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview.dogsitter_register_item;

public class MatchrecyclerviewAdapter extends RecyclerView.Adapter<MatchrecyclerviewAdapter.MatchrecyclerviewHolder>
{
    private Context context;
    private ArrayList<Matchresult_item> showdetailarrlist;
    private final ClickListener listener;

    public MatchrecyclerviewAdapter(Context context, ArrayList<Matchresult_item> showdetailarrlist,ClickListener listener) {
        this.context = context;
        this.showdetailarrlist = showdetailarrlist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchrecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_matchresult_item, viewGroup,false);
        return new MatchrecyclerviewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchrecyclerviewHolder matchrecyclerviewHolder, int position) {
        Matchresult_item matchresultItem = showdetailarrlist.get(position);
        Uri uri = Uri.parse(matchresultItem.getImage_dogsitter());
        Glide.with(context).load(uri).into(matchrecyclerviewHolder.dogsitter_img);
        matchrecyclerviewHolder.dogsitter_name.setText(matchresultItem.getName_dogsitter());
        matchrecyclerviewHolder.dogsitter_price.setText(matchresultItem.getPrice_dogsitter());
    }

    @Override
    public int getItemCount() {
        return showdetailarrlist.size();
    }

    public class MatchrecyclerviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView dogsitter_img;
        TextView dogsitter_name;
        TextView dogsitter_price;
        Button showdetail_btn;
        private WeakReference<ClickListener> listenerRef;


        public MatchrecyclerviewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);

            dogsitter_img = (ImageView)itemView.findViewById(R.id.dogsitter_profile_img);
            dogsitter_name = (TextView)itemView.findViewById(R.id.nametxtview);
            dogsitter_price = (TextView)itemView.findViewById(R.id.pricetextview);
            showdetail_btn = (Button)itemView.findViewById(R.id.info_btn);


            showdetail_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == showdetail_btn.getId())
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
