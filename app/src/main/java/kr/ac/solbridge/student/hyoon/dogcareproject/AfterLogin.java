package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.Chatting_Client_Side;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.ReceiverThread;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.SenderThread;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.getchatlist;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static kr.ac.solbridge.student.hyoon.dogcareproject.chatting.Chatting_Client_Side.IPADRRESS;

public class AfterLogin extends AppCompatActivity implements View.OnClickListener,ReceiverThreadmain.OnReceiveListener {

    private ImageButton mydog_btn;
    private ImageButton profile_btn;
    private ImageButton Dogsitter_btn;
    private DiscreteScrollView ItemPicker;
    private ApiConfig mapiconfig;
    private Toolbar mtoolbar;
    private SenderThread mThread1;
    private Button receive_btn;
    private Realm mRealm;
    private Socket mSocket = null;
    private String NAME;
    private NotificationManager mNM;
    private Notification mNoti;
    private ChattingDatabaseRealm chattingDatabaseRealm;
    private static String sender;
    private static String msg;
    private Context contex;
    private RelativeLayout title_bar;
    public static ArrayList<getchatlist> chattingListItems;
    Chatting_Client_Side chatting_client_side;
    private ArrayList<String> mchatlist;
    HashMap<String,ArrayList<String>> chatdata = new HashMap<String, ArrayList<String>>();
    ArrayList value = new ArrayList();
    private SimpleDateFormat sdfNow;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Chatting_Client_Side chatting_client_side = new Chatting_Client_Side();
        mtoolbar = (Toolbar) findViewById(R.id.tool_bar);
        setContentView(R.layout.activity_after_login);
        chattingListItems = new ArrayList<>();
        //수신함버튼
        title_bar = (RelativeLayout)findViewById(R.id.title_bar);
        receive_btn = (Button)title_bar.findViewById(R.id.btnreceive);
        receive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ChattingList.class);
                startActivityForResult(intent,3000);
                overridePendingTransition(0, 0);
            }
        });


        //렘초기화
        mRealm = Realm.getDefaultInstance();
        //캘린더 버튼
        mydog_btn = (ImageButton) findViewById(R.id.mydog_btn);
        profile_btn = (ImageButton) findViewById(R.id.profile_btn);
        Dogsitter_btn = (ImageButton) findViewById(R.id.dog_walker_btn);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_name = sp.getString("name","");
        NAME = sp_name;
        Btn_click_event();
        setSupportActionBar(mtoolbar);
        //자신의계정관리
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Account.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //더그시터 버튼
        Dogsitter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), DogSitterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //레트로핏초기화
        initializeRetrofit();
        //이미지와이름불러오기
        Getinfo();
        //TCP/IP연결
        ConnectToServer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    //TCP/IP연결
    public void ConnectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mSocket = new Socket(chatting_client_side.IPADRRESS, 7800);
                    ReceiverThreadmain thread2 = new ReceiverThreadmain(mSocket);
                    thread2.setOnReceiveListener(AfterLogin.this);
                    mThread1 = new SenderThread(mSocket,NAME);
                    mThread1.start();
                    thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //자신의반려견 산책신청란
    public void Btn_click_event() {
        mydog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), DogWalking.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    //레트로핏 초기화
    public void initializeRetrofit() {
        //레트로핏 빌드하기
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);

    }

    //프로필및 이름 불러오는 http통신문
    public void Getinfo() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_email = sp.getString("email", "");

        //프로필및 이름 불러오는 http통신문
        Call<List<GetdataAcc>> call = mapiconfig.getdata(sp_email);

        call.enqueue(new Callback<List<GetdataAcc>>() {
            @Override
            public void onResponse(Call<List<GetdataAcc>> call, Response<List<GetdataAcc>> response) {
                List<GetdataAcc> getdataAccList = response.body();
                String email = getdataAccList.get(0).getEmail();
                String name = getdataAccList.get(0).getName();
                String profile_img = getdataAccList.get(0).getProfile_img();
                String serveraddress = "http://34.235.147.50/";
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("imageurl", serveraddress + profile_img);
                editor.putString("name", name);
                editor.commit();
            }

            @Override
            public void onFailure(Call<List<GetdataAcc>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onClick(View v) {

    }

    @WorkerThread
    @Override
    public void onReceive(String message)  {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] split = message.split(">");
        sender = split[0];
        msg = split[1];


        SharedPreferences.Editor editor =sp.edit();
        String nickname = split[2];
        String SenderThatisent = sp.getString("더그시터이름모음","");

        //chattingListItems.add(new ChattingListItem(sender,msg));
        editor.putString("렘수신자",sender);
        editor.putString("렘메세지",msg);
        editor.commit();
        //알림관련
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(),AfterLogin.class),PendingIntent.FLAG_CANCEL_CURRENT);
        mNoti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(sender)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_pets_black_24dp)
                .setTicker(sender +"께서 메세지를 보내셨습니다.")
                .setAutoCancel(true)
                .setContentIntent(mPendingIntent)
                .build();
        mNM.notify(7777,mNoti);

    }

    public void saveChattingdata() {

        //제이슨배열에 수신자를 키값으로한 쉐어드프리퍼런스를담는다.
        //만약에 샌더의 이름이 저장되있으면 저장된 어레이리스트에 메세지를 넣어주고
        //없다면 어레이리스트를 만들어서 그곳에 데이터를 넣어준다.
        SharedPreferences spchat = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonSender = spchat.getString(sender,null);
        ArrayList<String> arrayList = new ArrayList<>();
        //만약에 쉐어드프리퍼런스에 sender라는 키값이있다면
        if(jsonSender == null)
        {
            //기존에 있던 어레이리스트에 담아준다.
            arrayList.add(msg);
        }
        else
        {
            //어레이리스트를 하나생성해서 거기에 넣어준다.


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case 3000:
                   // chattingListItems.add(data.getStringExtra("아이템"));
                    break;
            }
        }
    }


}






