package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.Chat_item;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.Chatting_Client_Side;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.SenderThread;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.getchatlist;
import kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview.dogsitter_register_item;
import kr.ac.solbridge.student.hyoon.dogcareproject.infodogsitter.InfoForDogSitter;
import kr.ac.solbridge.student.hyoon.dogcareproject.matchingrecyclerview.MatchrecyclerviewAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.matchingrecyclerview.Matchresult_item;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChattingList extends AppCompatActivity  implements View.OnClickListener,ReceiverThreadmain.OnReceiveListener {
    private LinearLayoutManager mLayoutManger;
    private ChattingListAdapter mAdapter;
    private ArrayList<String> mitems_for_savedata_chatting = new ArrayList<>();
    private ArrayList<ChattingListItem> mitems_for_savedata_chatting_next = new ArrayList<>();
    private ArrayList<ChattingListItem> mitems = new ArrayList<>();
    private String msg;
    private Socket mSocket = null;
    @BindView(R.id.chatting_list_recycler)
    RecyclerView chatting_list_recycler;
    private AfterLogin afterLogin;
    private SenderThread mThread1;
    private String NAME;
    private String sender;
    private String msgs;
    private String image_for_sender;
    private NotificationManager mNM;
    private Notification mNoti;
    private ArrayList<getchatlist> chattingListItems;
    private ApiConfig mapiconfig;
    private String msender_recycler;
    private String mmsg_recycler;
    private int msize;
    private String msender_recyclernext;
    private String mmsg_recyclernext;
    Chatting_Client_Side chatting_client_side;
    private ArrayList<ChattingListItem> arrayList = new ArrayList<>();
    private String json;
    private Gson gson;
    private String senderimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_list);
        ButterKnife.bind(this);
        //리싸이클러뷰배경
        chatting_list_recycler.setBackgroundResource(R.drawable.apeachwallpaper);
        afterLogin = new AfterLogin();
        chatting_client_side = new Chatting_Client_Side();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_name = sp.getString("name","");
        NAME = sp_name;
        chattingListItems = new ArrayList<>();
        //서버에연결
        ConnectToServer();
        //레트로핏초기화
        initializeRetrofit();


        Call<List<getchatlist>> call = mapiconfig.getchatlistPersonllay(NAME);
        call.enqueue(new Callback<List<getchatlist>>() {
            @Override
            public void onResponse(Call<List<getchatlist>> call, Response<List<getchatlist>> response) {
                List<getchatlist> getchatlists = response.body();
                for(int i=0; i<getchatlists.size(); i++)
                {
                    chattingListItems.add(new getchatlist(getchatlists.get(i).getSender_image(),getchatlists.get(i).getSender_chat(),getchatlists.get(i).getMsg_chat(),getchatlists.get(i).getSender_time()));
                }
                setRecyclerView();
            }

            @Override
            public void onFailure(Call<List<getchatlist>> call, Throwable t) {

            }
        });
        //getChatlistdataFromSever();
    }

    //경력사항 불러오는 httpd통신문
    private void getChatlistdataFromSever()
    {
        Call<List<getchatlist>> call = mapiconfig.getchatlistPersonllay(NAME);
        call.enqueue(new Callback<List<getchatlist>>() {
            @Override
            public void onResponse(Call<List<getchatlist>> call, Response<List<getchatlist>> response) {
                List<getchatlist> getchatlists = response.body();
                for(int i=0; i<getchatlists.size(); i++)
                {
                    afterLogin.chattingListItems.add(new getchatlist(getchatlists.get(i).getSender_image(),getchatlists.get(i).getSender_chat(),getchatlists.get(i).getMsg_chat(),getchatlists.get(i).getSender_time()));
                }
                setRecyclerView();
            }

            @Override
            public void onFailure(Call<List<getchatlist>> call, Throwable t) {

            }
        });

    }

    //TCP/IP연결
    public void ConnectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mSocket = new Socket(chatting_client_side.IPADRRESS, 7800);
                    ReceiverThreadmain thread2 = new ReceiverThreadmain(mSocket);
                    thread2.setOnReceiveListener(ChattingList.this);
                    mThread1 = new SenderThread(mSocket,NAME);
                    mThread1.start();
                    thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    //소켓닫기
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            mSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //리사이클러뷰 초기화
    private void  setRecyclerView()
    {
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        //라인그리기
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        //레이아웃 리싸이클러에 붓이기
        chatting_list_recycler.setLayoutManager(mLayoutManger);
        chatting_list_recycler.setItemAnimator(new DefaultItemAnimator());
        //선그리기
        chatting_list_recycler.addItemDecoration(dividerItemDecoration);
        //아답터추가
        mAdapter = new ChattingListAdapter(getApplicationContext(), chattingListItems, new ClickListener() {
            @Override
            public void OnPositionClicked(int position) {
                Intent intent = new Intent(getApplicationContext(),ChattingAfterReceiveActivity.class);
                intent.putExtra("발신자",chattingListItems.get(position).getSender_chat());

                startActivity(intent);

            }

            @Override
            public void OnLongClicked(int position) {

            }
        });
        //아답터 변화되는거 추가
        mAdapter.notifyDataSetChanged();
        //리싸이클러뷰에 아답터넣기
        chatting_list_recycler.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {

    }

    @WorkerThread
    @Override
    public void onReceive(String message) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] split = message.split(">");
        sender = split[0];
        msgs = split[1];
        String nickname = split[2];
        senderimage = split[3];
        String SenderThatisent = sp.getString("더그시터이름모음","");
        SharedPreferences.Editor editor =sp.edit();
        //afterLogin.chattingListItems.add(new ChattingListItem(sender,msgs));
        //mitems_for_savedata_chatting_next.add(new ChattingListItem(sender,msgs));
        editor.putString("렘수신자",sender);
        editor.putString("렘메세지",msgs);
        //editor.commit();
        //알림관련
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(),AfterLogin.class),PendingIntent.FLAG_CANCEL_CURRENT);
        mNoti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(sender)
                .setContentText(msgs)
                .setSmallIcon(R.drawable.ic_pets_black_24dp)
                .setTicker(sender +"께서 메세지를 보내셨습니다.")
                .setAutoCancel(true)
                .setContentIntent(mPendingIntent)
                .build();
        mNM.notify(7777,mNoti);
        SharedPreferences sptwo = getSharedPreferences("채팅모음",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sptwo.edit();

        //키값이 없으면 에레이리스트 생성 있으면 있는어레이리스트에넣어주기
        String keyvalue = sptwo.getString(sender,null);
        if(keyvalue == null)
        {
            gson = new Gson();
            arrayList = new ArrayList<>();
            arrayList.add(new ChattingListItem(senderimage,sender,msgs));
            json = gson.toJson(arrayList);
            editor1.putString(sender, json);
            editor1.apply();
        }
        else
        {
            Type type = new TypeToken<ArrayList<ChattingListItem>>(){}.getType();
            gson = new Gson();
           // json = sptwo.getString(sender,null);
            arrayList= gson.fromJson(keyvalue,type);
            arrayList.add(new ChattingListItem(senderimage,sender,msgs));
            json = gson.toJson(arrayList);
            editor1.putString(sender,json);
            editor1.apply();
        }

        editor.commit();
        editor1.commit();
        //final ChattingListItem chat_item = new ChattingListItem(sender,msgs);
        // UI 스레드로 실행
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //메세지 갱신
                SharedPreferences sptwo = getSharedPreferences("채팅모음",MODE_PRIVATE);
                Type type = new TypeToken<ArrayList<ChattingListItem>>(){}.getType();
                gson = new Gson();
                json = sptwo.getString(sender,null);
                arrayList= gson.fromJson(json,type);
                for(int i=0; i<chattingListItems.size(); i++)
                {
                    if(chattingListItems.get(i).getSender_chat().equals(arrayList.get(arrayList.size()-1).getSender()))
                    {
                        chattingListItems.set(i,new getchatlist(arrayList.get(arrayList.size()-1).getImage(),arrayList.get(arrayList.size()-1).getSender(),arrayList.get(arrayList.size()-1).getMsg(),arrayList.get(arrayList.size()-1).getMsg()));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

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


}
