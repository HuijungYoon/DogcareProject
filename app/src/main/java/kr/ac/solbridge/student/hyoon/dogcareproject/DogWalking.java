package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.matchingrecyclerview.Matchrecyclerview;

public class DogWalking extends AppCompatActivity {

    private TextView calendar_tv;
    private TextView location_tv;
    @BindView(R.id.next_btn)
    Button next_btn;


    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_walking);
        ButterKnife.bind(this);
        Search_Address search_address = new Search_Address();
        calendar_tv = (TextView)findViewById(R.id.calendar_tv);
        location_tv = (TextView)findViewById(R.id.location_tv);

        Clickevent();
        Intent intent = getIntent();

//        //날짜 년,월,일 인텐트값 받아와서 각변수들에 넣기
//        String data = intent.getStringExtra("year");
//        String datab = intent.getStringExtra("month");
//        String datac = intent.getStringExtra("day");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String calendar_date = sp.getString("date","");
        String location_data = sp.getString("주소","");
        String currentlocation_data  = sp.getString("주소","");
        Log.e("date",sp.getString("date",""));

        int k;

        k = sp.getInt("비교값",-1);
        //인텐트값을 받아오기전
        if(calendar_date ==null)
        {
            calendar_tv.setText("날짜를 선택해주세요.");
        }
        //받아온후
        else {
            //쉐어드프리퍼런스에 저장된 캘린더에서 선택한 날짜를 담는 변수
            calendar_tv.setText(calendar_date);

        }


        /*현재위치검색했을때와 주소검색을 통해 찾은 결과를 textview에 뿌려주는조건문 */
        if(location_data ==null) {

            location_tv.setText("위치를 입력하세요");

        }
        else if(k == 1)
        {
            location_tv.setText(currentlocation_data);
            SharedPreferences.Editor editor = sp.edit();
            k = 0;
            editor.putInt("비교값",k);
            editor.commit();
        }

        else if (location_data!=null)
        {
            location_tv.setText(location_data);
        }

    }
    private void Clickevent()
    {
        calendar_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), calendar.class);
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });
        location_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),Search_Address.class);
                startActivity(intent);
            }
        });
    }
    @OnClick(R.id.next_btn)
    void nextmatch()
    {
        Intent intent = new Intent(getApplicationContext(), Matchrecyclerview.class);
        startActivity(intent);
    }

    //뒤로가기버튼
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
