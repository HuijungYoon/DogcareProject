package kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.solbridge.student.hyoon.dogcareproject.ApiConfig;
import kr.ac.solbridge.student.hyoon.dogcareproject.DogSitterActivity;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.accrecycle.AccountRecycerAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.Updateitem;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DogSitter_Register_List extends AppCompatActivity {
@BindView(R.id.dogsitter_register_recyclerview)
RecyclerView dogsitter_register_recyclerview;
private LinearLayoutManager mLayoutManger;
private DogSitterAdapter mAdapter;
private ArrayList<getDogsitter_Register> arraygetDogSitterList;
private ApiConfig mapiconfig;
private ArrayList<dogsitter_register_item> mitems = new ArrayList<>();//아이템 배열목록 초기화
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_sitter__register__list);
        ButterKnife.bind(this);
        //레트로핏초기화
        initializeretrofit();
        //http통신
        getData_Dogsitter_Register_Info_From_Server();
        //배열 초기화
        arraygetDogSitterList =  new ArrayList<>();

    }
    //서버에서 자신이 등록한 반려견 산책 신청 정보 불러오기
    private void getData_Dogsitter_Register_Info_From_Server()
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_email = sp.getString("email","");
        Call<List<getDogsitter_Register>> call = mapiconfig.getdogsitterRegister(sp_email);
        call.enqueue(new Callback<List<getDogsitter_Register>>() {
            @Override
            public void onResponse(Call<List<getDogsitter_Register>> call, Response<List<getDogsitter_Register>> response) {
                List<getDogsitter_Register> getDogsitterRegistersList = response.body();
                for(int i=0; i<getDogsitterRegistersList.size(); i++)
                {
                    arraygetDogSitterList.add(new getDogsitter_Register(getDogsitterRegistersList.get(i).getImage(),getDogsitterRegistersList.get(i).getName(),getDogsitterRegistersList.get(i).getPrice()));
                }
                Toast.makeText(DogSitter_Register_List.this, "리스트.", Toast.LENGTH_SHORT).show();
                setRecyclerView(); //리사이클러뷰
            }

            @Override
            public void onFailure(Call<List<getDogsitter_Register>> call, Throwable t) {

            }
        });

    }
    //레트로핏초기화및 apiconfig 인터페이스 초기화
    private void initializeretrofit()
    {
        //레트로핏 빌드하기
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);
    }

    //리사이클러뷰 초기화
    private void  setRecyclerView()
    {
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        for(int i=0; i<arraygetDogSitterList.size(); i++)
        {
            mitems.add(new dogsitter_register_item(arraygetDogSitterList.get(i).getImage(),arraygetDogSitterList.get(i).getName(),arraygetDogSitterList.get(i).getPrice()));
        }
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        dogsitter_register_recyclerview.setLayoutManager(mLayoutManger);
        dogsitter_register_recyclerview.setItemAnimator(new DefaultItemAnimator());
        dogsitter_register_recyclerview.addItemDecoration(dividerItemDecoration);
        mAdapter = new DogSitterAdapter(getApplicationContext(),mitems);
        dogsitter_register_recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
