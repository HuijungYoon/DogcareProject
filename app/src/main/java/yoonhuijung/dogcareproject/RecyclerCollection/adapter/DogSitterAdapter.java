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
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import yoonhuijung.dogcareproject.RecyclerCollection.item.dogsitter_register_item;

public class DogSitterAdapter extends RecyclerView.Adapter<DogSitterAdapter.DogSitterViewHolder> {

    private Context context;
    private ArrayList<dogsitter_register_item> dogsitterRegisterItems;


    public DogSitterAdapter(Context context, ArrayList<dogsitter_register_item> dogsitterRegisterItems) {
        this.context = context;
        this.dogsitterRegisterItems = dogsitterRegisterItems;
    }



    @NonNull
    @Override
    public DogSitterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_dogsitter_register_item, viewGroup,false);
        return new DogSitterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogSitterViewHolder dogSitterViewHolder, int position) {
        dogsitter_register_item dogsitter_register_item = dogsitterRegisterItems.get(position);
        Uri uri = Uri.parse(dogsitter_register_item.getImage_dogsitter());
        Glide.with(context).load(uri).into(dogSitterViewHolder.dogsitter_img);
        dogSitterViewHolder.dogsitter_name.setText(dogsitter_register_item.getName_dogsitter());
        dogSitterViewHolder.dogsitter_price.setText(dogsitter_register_item.getPrice_dogsitter());

    }

    @Override
    public int getItemCount() {
        return dogsitterRegisterItems.size();
    }


    public class DogSitterViewHolder extends RecyclerView.ViewHolder{
        ImageView dogsitter_img;
        TextView dogsitter_name;
        TextView dogsitter_price;

        public DogSitterViewHolder(@NonNull View itemView) {
            super(itemView);
            dogsitter_img = (ImageView)itemView.findViewById(R.id.dogsitter_profile_img);
            dogsitter_name = (TextView)itemView.findViewById(R.id.nametxtview);
            dogsitter_price = (TextView)itemView.findViewById(R.id.pricetextview);
        }
    }

}
