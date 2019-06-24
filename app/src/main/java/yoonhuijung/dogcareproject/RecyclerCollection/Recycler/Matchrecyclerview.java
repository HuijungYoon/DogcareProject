package yoonhuijung.dogcareproject.RecyclerCollection.Recycler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yoonhuijung.dogcareproject.RecyclerCollection.adapter.MatchrecyclerviewAdapter;
import yoonhuijung.dogcareproject.RecyclerCollection.item.Matchresult_item;
import yoonhuijung.dogcareproject.dogsitterRecyclerview.LatitudeLongtitudeDogSitterRegister;
import yoonhuijung.dogcareproject.dogsitterRecyclerview.OnlyLatitudeLongtitude;
import yoonhuijung.dogcareproject.Interface.ApiConfig;
import yoonhuijung.dogcareproject.Interface.ClickListener;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import yoonhuijung.dogcareproject.geoapi.Search_Address;
import yoonhuijung.dogcareproject.infodogsitter.InfoForDogSitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Matchrecyclerview extends AppCompatActivity {
    private ArrayList<LatitudeLongtitudeDogSitterRegister> arraygetDogSitterList;
    private ArrayList<OnlyLatitudeLongtitude> onlyLatitudeLongtitudeArrayList;
    private ApiConfig mapiconfig;
    private LinearLayoutManager mLayoutManger;
    private MatchrecyclerviewAdapter mAdapter;
    private ArrayList<Matchresult_item> mitems = new ArrayList<>();//아이템 배열목록 초기화
    @BindView(R.id.showdetailrecyclerview)
    RecyclerView showdetail_recyclerview;
    InfoForDogSitter infoForDogSitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchrecyclerview);
        ButterKnife.bind(this);
        //레트로핏초기화
        initializeretrofit();
        //http통신
        getData_Dogsitter_Register_Info_From_Server();
        //배열 초기화
        arraygetDogSitterList =  new ArrayList<>();
        onlyLatitudeLongtitudeArrayList= new ArrayList<>();
    }

    //서버에서 자신이 등록한 반려견 산책 신청 정보 위도와 경도 불러오기
    private void getData_Dogsitter_Register_Info_From_Server()
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Call<List<LatitudeLongtitudeDogSitterRegister>> call = mapiconfig.getdogsitterLatitudeLongtitude();
        call.enqueue(new Callback<List<LatitudeLongtitudeDogSitterRegister>>() {
            @Override
            public void onResponse(Call<List<LatitudeLongtitudeDogSitterRegister>> call, Response<List<LatitudeLongtitudeDogSitterRegister>> response) {
                List<LatitudeLongtitudeDogSitterRegister> latitudeLongtitudeDogSitterRegisters = response.body();
                Search_Address search_address = new Search_Address();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String mycurrentlatitude;
                double mycurrentlatitude_dobule;
                String mycurrentlongtitude;
                double mycurrentlongtitude_doble;
                double latitude_dogsitter_dobule;
                double lontitude_dogsitter_dobule;
                Search_Address search_addres = new Search_Address();
                if(search_addres.mycurrentbtnclicked == true)
                {

                    mycurrentlatitude =  sp.getString("내위치위도","");
                    mycurrentlatitude_dobule = Double.parseDouble(mycurrentlatitude);
                    mycurrentlongtitude =  sp.getString("내위치경도","");
                    mycurrentlongtitude_doble = Double.parseDouble(mycurrentlongtitude);
                }
                else
                {
                    mycurrentlatitude =  sp.getString("위도직접입력","");
                    mycurrentlatitude_dobule = Double.parseDouble(mycurrentlatitude);
                    mycurrentlongtitude =  sp.getString("경도직접입력","");
                    mycurrentlongtitude_doble = Double.parseDouble(mycurrentlongtitude);
                }
                float[] result = new float[1];
                float[] limitdistance = new float[1];
                limitdistance[0] = 10000.0f;
                for(int i=0; i<latitudeLongtitudeDogSitterRegisters.size(); i++)
                {
                    onlyLatitudeLongtitudeArrayList.add(new OnlyLatitudeLongtitude(latitudeLongtitudeDogSitterRegisters.get(i).getLatitude(),latitudeLongtitudeDogSitterRegisters.get(i).getLongtitude()));
                    latitude_dogsitter_dobule = Double.parseDouble(latitudeLongtitudeDogSitterRegisters.get(i).getLatitude());
                    lontitude_dogsitter_dobule = Double.parseDouble(latitudeLongtitudeDogSitterRegisters.get(i).getLongtitude());
                   // caculatedistance(mycurrentlatitude_dobule,mycurrentlongtitude_doble,latitude_dogsitter_dobule,lontitude_dogsitter_dobule);
                    result =  caculatedistance(mycurrentlatitude_dobule,mycurrentlongtitude_doble,latitude_dogsitter_dobule,lontitude_dogsitter_dobule);
                    if(result[0] < limitdistance[0])
                    {
                        arraygetDogSitterList.add(new LatitudeLongtitudeDogSitterRegister(latitudeLongtitudeDogSitterRegisters.get(i).getImage(),latitudeLongtitudeDogSitterRegisters.get(i).getName(),latitudeLongtitudeDogSitterRegisters.get(i).getPrice(),latitudeLongtitudeDogSitterRegisters.get(i).getLatitude()
                                ,latitudeLongtitudeDogSitterRegisters.get(i).getLongtitude()));
                    }
                }
                //Toast.makeText(Matchrecyclerview.this, "리스트.", Toast.LENGTH_SHORT).show();
                setRecyclerView(); //리사이클러뷰
            }

            @Override
            public void onFailure(Call<List<LatitudeLongtitudeDogSitterRegister>> call, Throwable t) {

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


    //서버에서 불러온 강아지돌보미 신책현황에서 주소값인 위도와 경도를 자신의 위치나 자신이 원하는 주소의 위도와 경도에 대입해
    //거리를 구하는 함수이다.
    public float[] caculatedistance(double mylati, double mylongti, double sitterlati,double sitterlongti)
    {
        float[] dist = new float[1];
        Location.distanceBetween(mylati,mylongti,sitterlati,sitterlongti,dist);
        float[] limitdistance = new float[1];
        limitdistance[0] = 100000.0f;
        if(dist[0]<limitdistance[0])
        {
            Log.e("10키로이내","10키로이내입니다.");
        }
        return dist;
    }

    //리사이클러뷰 초기화
    private void  setRecyclerView()

    {
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);

        for(int i=0; i<arraygetDogSitterList.size(); i++)
        {
            mitems.add(new Matchresult_item(arraygetDogSitterList.get(i).getImage(),arraygetDogSitterList.get(i).getName(),arraygetDogSitterList.get(i).getPrice()));
        }
        if(arraygetDogSitterList.size() == 0)
        {
            Toast.makeText(this, "근방에 애완견산책돌보미가 없습니다.", Toast.LENGTH_SHORT).show();
        }
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        showdetail_recyclerview.setLayoutManager(mLayoutManger);
        showdetail_recyclerview.setItemAnimator(new DefaultItemAnimator());
        showdetail_recyclerview.addItemDecoration(dividerItemDecoration);
        mAdapter = new MatchrecyclerviewAdapter(getApplicationContext(),mitems, new ClickListener() {
        @Override
        public void OnPositionClicked(int position) {

                Intent intent = new Intent(getApplicationContext(),InfoForDogSitter.class);
                intent.putExtra("더그시터이미지",mitems.get(position).getImage_dogsitter());
                intent.putExtra("더그시터이름",mitems.get(position).getName_dogsitter());
                intent.putExtra("더그시터가격",mitems.get(position).getPrice_dogsitter());
                startActivity(intent);


        }

        @Override
        public void OnLongClicked(int position) {

        }
    });
        showdetail_recyclerview.setAdapter(mAdapter);
    }


//    //인텐트ActivityForResult
//    public void IntentforResult()
//    {
//        Intent intent = new Intent(getApplication(),InfoForDogSitter.class);
//        startActivityForResult(intent,1300);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
