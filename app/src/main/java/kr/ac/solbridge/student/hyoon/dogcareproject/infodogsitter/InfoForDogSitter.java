package kr.ac.solbridge.student.hyoon.dogcareproject.infodogsitter;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.ApiConfig;
import kr.ac.solbridge.student.hyoon.dogcareproject.GetdataAcc;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.accrecycle.AccountRecycerAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.Chatting_Client_Side;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.getchatdata;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.Updateitem;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoForDogSitter extends AppCompatActivity {

    //Chatting_Client_Side
    @BindView(R.id.dogsitter_profile_image)
    ImageView dogsitter_profile_image;
    @BindView(R.id.dogsitter_name_txt)
    TextView dogsitter_name_txt;
    @BindView(R.id.contact_btn_to_dogsitter)
    Button contact_btn_to_dogsitter;
    @BindView(R.id.natural)
    LinearLayout natural;
    @BindView(R.id.dogsitter_recyclerview)
    RecyclerView dogsitter_recyclerview;
    @BindView(R.id.pay_money_btn)
    Button pay_money_btn;
    private ApiConfig mapiconfig;
    LinearLayoutManager mLayoutManger;
    AccountRecycerAdapter mAdapter;
    ArrayList<getCarrerdata> arrayCarrerList;
    private ArrayList<Updateitem> mitems = new ArrayList<>();//아이템 배열목록 초기화
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_for_dog_sitter);
        ButterKnife.bind(this);
        getCarrerdata getCarrerdata;
        //배열 초기화
        arrayCarrerList =  new ArrayList<>();
        getIntentValueMethod();
        //레트로핏초기화
        initializeRetrofit();
        //서버에서 데이터경력받아오기
        getCarrerdataFromSever();

        //쉐어드프리퍼런스
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
    }
    //결제확인창
    @OnClick(R.id.pay_money_btn)
    void setPay_money_btn()
    {
        info_Custom_Dialog_DogSitter info_custom_dialog_dogSitter = new info_Custom_Dialog_DogSitter(this);
        info_custom_dialog_dogSitter.show();
    }
    //더그시터에게 연락하기
    @OnClick(R.id.contact_btn_to_dogsitter)
    void ContactToDogSitter()
    {
        Intent intent = new Intent(getApplicationContext(), Chatting_Client_Side.class);
        startActivity(intent);

    }

    public void getIntentValueMethod()
    {
        //인텐트값일거온다
        Intent intent = getIntent();
        //업데이트이미지 스트링 변수에 에 더그시터이미지를 담는다
        String updateimage = intent.getStringExtra("더그시터이미지");
        //업데이트이름 스트링 변수에 에 더그시터이름를 담는다
        String name = intent.getStringExtra("더그시터이름");
        //업데이트가격 스트링 변수에 담아준다.
        String price = intent.getStringExtra("더그시터가격");

        //스트링 updateimage를 Uri로 변환해준다
        Uri uri = Uri.parse(updateimage);
        //각 xml 에 담아준다.
        Glide.with(this).load(R.drawable.sea).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    natural.setBackground(resource);
                }
            }
        });
        Glide.with(this).load(uri).into(dogsitter_profile_image);
        dogsitter_name_txt.setText(name);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("더그시터이름모음",name);
        editor.putString("더그시터이미지",updateimage);
        editor.putString("더그시터가격",price);
        editor.commit();
    }
    //레트로핏 초기화
    public void initializeRetrofit()
    {
        //레트로핏 빌드하기
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);

    }
//리싸이클러뷰
    private void  setRecyclerView()
    {
        int img_carrer_int = R.drawable.takewalk;
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        for(int i=0; i<arrayCarrerList.size(); i++)
        {
            mitems.add(new Updateitem(img_carrer_int,arrayCarrerList.get(i).getCarrer_title(),arrayCarrerList.get(i).getCarrer_contents()));
        }

        dogsitter_recyclerview.setLayoutManager(mLayoutManger);
        dogsitter_recyclerview.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AccountRecycerAdapter(getApplicationContext(),mitems);
        dogsitter_recyclerview.setAdapter(mAdapter);
    }

    //경력사항 불러오는 httpd통신문
    private void getCarrerdataFromSever()
    {
        //인텐트값일거온다
        Intent intent = getIntent();
        String name = intent.getStringExtra("더그시터이름");

        Call<List<getCarrerdata>> call = mapiconfig.getCarrerdataPersonally(name);
        call.enqueue(new Callback<List<getCarrerdata>>() {
            @Override
            public void onResponse(Call<List<getCarrerdata>> call, Response<List<getCarrerdata>> response) {
                List<getCarrerdata> getCarrerdata = response.body();
                for(int i=0 ; i<getCarrerdata.size(); i++)
                {
                    arrayCarrerList.add(new getCarrerdata(getCarrerdata.get(i).getCarrer_title(),getCarrerdata.get(i).getCarrer_contents()));
                }
                setRecyclerView();
            }

            @Override
            public void onFailure(Call<List<getCarrerdata>> call, Throwable t) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //뒤로가기버튼
        finish();
    }
}
