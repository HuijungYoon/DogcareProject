package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview.DogSitter_Register_List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DogSitterActivity extends AppCompatActivity {

    @BindView(R.id.dogsitter_profile_img)
    ImageView dogsitter_profile_img;
    @BindView(R.id.dogsitter_profile_name_tv)
    TextView dogsitter_profile_name_tv;
    @BindView(R.id.dogsitter_profile_date_tv)
    TextView dogsitter_profile_date_tv;
    @BindView(R.id.dogsitter_profile_address_tv)
    TextView dogsitter_profile_address_tv;
    @BindView(R.id.dogsitter_complete_btn)
    Button dogsitter_complete_btn;
    private String calendar_date;
    @BindView(R.id.dogsitter_price_edit)
    EditText dogsitter_price_edit;
    @BindView(R.id.dogsitter_register_show_btn)
    Button dogsitter_register_show_btn;
    private ApiConfig mapiconfig; //레트로핏 인터페이스


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),AfterLogin.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_sitter);
        ButterKnife.bind(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String profile_url = sp.getString("imageurl","");
        Uri uri =  Uri.parse(profile_url);
        String updateimagefile = sp.getString("업데이트사진","");
        Glide.with(getApplicationContext()).load(uri).into(dogsitter_profile_img); //프포필이미지
        String profile_name = sp.getString("name","");
        dogsitter_profile_name_tv.setText(profile_name); //이름
        calendar_date = sp.getString("dateforsitter","");
        String currentlocation_data  = sp.getString("내위치더그시터","");
        String location_data = sp.getString("주소더그시터","");
        //작성했을때;
        initializwrite();
        int k;
        k = sp.getInt("비교값더그시터",-1);
        /*현재위치검색했을때와 주소검색을 통해 찾은 결과를 textview에 뿌려주는조건문 */
        if(location_data ==null) {

            dogsitter_profile_address_tv.setText("위치를 입력하세요");

        }
        else if(k == 1)
        {
            dogsitter_profile_address_tv.setText(currentlocation_data);
            SharedPreferences.Editor editor = sp.edit();
            k = 0;
            editor.putInt("비교값더그시터",k);
            editor.commit();
        }

        else if (location_data!=null)
        {
            dogsitter_profile_address_tv.setText(location_data);
        }
        String price = dogsitter_price_edit.getText().toString();

        buildRetrofit();
    }


    //레트로핏 초기화및빌드
    public void buildRetrofit()
    {
        //레트로핏 빌드하기
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);
    }





    public void initializwrite()
    {

        //날짜선택
        if(calendar_date == null)
        {
            dogsitter_profile_date_tv.setText("날짜를 선택해주세요");
        }
        else
        {
            dogsitter_profile_date_tv.setText(calendar_date);
        }
        //주소선택
        if(dogsitter_profile_address_tv == null)
        {
            dogsitter_profile_address_tv.setText("주소를 선택해주세요");
        }



    }

    //작성완료버튼
    @OnClick(R.id.dogsitter_complete_btn)
    void CompleteDocument()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = sp.getString("email","");
        String name = sp.getString("name","");
        String image = sp.getString("imageurl","");
        String priceedit = dogsitter_price_edit.getText().toString();
        String latitude;
        String longtitude;
        SearchAddressforDogSitter searchAddressforDogSitter = new SearchAddressforDogSitter();

        if(searchAddressforDogSitter.mycurrentbtnboolean == true)
        {
            latitude = sp.getString("내위치위도더그시터","");
            longtitude = sp.getString("내위치경도더그시터","");
        }
        else
        {
            latitude = sp.getString("위도직접입력더그시터","");
            longtitude = sp.getString("경도직접입력더그시터","");
        }

        if(priceedit.getBytes().length<=0)
        {
            Toast.makeText(DogSitterActivity.this, "가격을 반드시 입력해주셔야합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            int price = Integer.parseInt(dogsitter_price_edit.getText().toString());
            Call<GetdataAcc> call = mapiconfig.sitter_register_info(email, image, name, price,latitude,longtitude);
            call.enqueue(new Callback<GetdataAcc>() {
                @Override
                public void onResponse(Call<GetdataAcc> call, Response<GetdataAcc> response) {
                    GetdataAcc getdataAcc = response.body();
                    if (getdataAcc.getResult().equals("YES")) {
                        Toast.makeText(DogSitterActivity.this, "추가완료.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DogSitter_Register_List.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(DogSitterActivity.this, "추가실패.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GetdataAcc> call, Throwable t) {

                }
            });
        }

    }
    //날짜 edittext선택했을때
    @OnClick(R.id.dogsitter_profile_date_tv)
    void Datepickup()
    {
        Intent intent = new Intent(getApplication(), CallendarforDogSitter.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
    //주소tv를 선택했을때
    @OnClick(R.id.dogsitter_profile_address_tv)
    void AddressSearch()
    {
        Intent intent = new Intent(getApplication(),SearchAddressforDogSitter.class);
        startActivity(intent);
    }
    //신청현황목록보기버튼
    @OnClick(R.id.dogsitter_register_show_btn)
    void showRegisterList()
    {
        Intent intent = new Intent(getApplication(),DogSitter_Register_List.class);
        startActivity(intent);
    }



}
