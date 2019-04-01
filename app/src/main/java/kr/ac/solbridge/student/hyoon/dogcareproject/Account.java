package kr.ac.solbridge.student.hyoon.dogcareproject;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Build;

import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.solbridge.student.hyoon.dogcareproject.accrecycle.AccountRecycerAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.Updateitem;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Account extends AppCompatActivity {
    ImageView profile_image;
    LinearLayout imagelinearlayout;
    TextView profile_name;
    private ImageView updateplus_btn;
    private TextView acc_name_txt;
    private ArrayList<Updateitem> mitems = new ArrayList<>();//아이템 배열목록 초기화
    @BindView(R.id.acc_recyclerview)
    RecyclerView mrecylerview;
    LinearLayoutManager mLayoutManger;
    AccountRecycerAdapter mAdapter;
    ArrayList<getCarrerdata> arrayCarrerList;
    private ApiConfig mapiconfig;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplication(), AfterLogin.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        getCarrerdata getCarrerdata;
        mrecylerview = (RecyclerView)findViewById(R.id.acc_recyclerview);
        updateplus_btn = (ImageView)findViewById(R.id.updateplus_btn);
        acc_name_txt = (TextView)findViewById(R.id.acc_name_txt);
        profile_image= (ImageView)findViewById(R.id.profile_image);
        imagelinearlayout = (LinearLayout)findViewById(R.id.natural);
        profile_name = (TextView)findViewById(R.id.name_txt);

        //레트로핏 빌드하기
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);

        //배열 초기화
        arrayCarrerList =  new ArrayList<>();

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_email = sp.getString("email","");
        String sp_name = sp.getString("name","");
        //profile  url 받아오기
        String profile_url = sp.getString("imageurl","");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("업데이트사진",profile_url);
        editor.commit();
        Uri uri =  Uri.parse(profile_url);
        Glide.with(getApplicationContext()).load(uri).into(profile_image);
        acc_name_txt.setText(sp_name);

        //경력사항 불러오는 http통신문
        Call<List<GetdataAcc>> calls = mapiconfig.getcarrerdata(sp_email);

        calls.enqueue(new Callback<List<GetdataAcc>>() {
            @Override
            public void onResponse(Call<List<GetdataAcc>> call, Response<List<GetdataAcc>> response) {
                List<GetdataAcc> getcarrerdataliset = response.body();

                for(int i =0; i<getcarrerdataliset.size(); i++)
                {
                    arrayCarrerList.add(new getCarrerdata(getcarrerdataliset.get(i).getCarrer_title(),getcarrerdataliset.get(i).getCarrer_contents()));
                }
                setRecyclerView();
            }
            @Override
            public void onFailure(Call<List<GetdataAcc>> call, Throwable t) {

            }
        });

        Glide.with(this).load(R.drawable.sea).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    imagelinearlayout.setBackground(resource);
                }
            }
        });


        updateplus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),updateAccount.class);
                startActivityForResult(intent,3000);
            }
        });

    }
    private void  setRecyclerView()
    {
        int img_carrer_int = R.drawable.takewalk;
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        for(int i=0; i<arrayCarrerList.size(); i++)
        {
            mitems.add(new Updateitem(img_carrer_int,arrayCarrerList.get(i).getCarrer_title(),arrayCarrerList.get(i).getCarrer_contents()));
        }
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String jsonitem = gson.toJson(mitems);
        editor.putString("수정본",jsonitem);
        editor.commit();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        mrecylerview.setLayoutManager(mLayoutManger);
        mrecylerview.setItemAnimator(new DefaultItemAnimator());
        mrecylerview.addItemDecoration(dividerItemDecoration);
        mAdapter = new AccountRecycerAdapter(getApplicationContext(),mitems);
        mAdapter.notifyItemInserted(1);
        mAdapter.notifyDataSetChanged();
        mrecylerview.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            switch (requestCode){
                case 3000:
                    String resultname = data.getStringExtra("업데이트이름");
                    String resultimage = data.getStringExtra("업데이트사진");
                    int a = data.getIntExtra("1",-1);
                    String b = data.getStringExtra("2");
                    String c = data.getStringExtra("3");
                    int img_carrer_int = R.drawable.takewalk;
                    mLayoutManger = new LinearLayoutManager(this);
                    mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
                    mitems.add(0,new Updateitem(img_carrer_int,b,c));
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
                    mrecylerview.setLayoutManager(mLayoutManger);
                    mrecylerview.setItemAnimator(new DefaultItemAnimator());
                    mrecylerview.addItemDecoration(dividerItemDecoration);
                    mAdapter = new AccountRecycerAdapter(getApplicationContext(),mitems);
                    mAdapter.notifyItemInserted(1);
                    mAdapter.notifyDataSetChanged();
                    mrecylerview.setAdapter(mAdapter);
                    acc_name_txt.setText(resultname);
                    profile_image.setImageURI(Uri.parse(resultimage));
                    break;
            }

        }

    }
}
