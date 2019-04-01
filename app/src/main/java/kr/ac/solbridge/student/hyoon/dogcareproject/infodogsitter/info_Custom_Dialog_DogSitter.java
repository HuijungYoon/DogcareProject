package kr.ac.solbridge.student.hyoon.dogcareproject.infodogsitter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.ApiConfig;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.ResultModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class info_Custom_Dialog_DogSitter extends Dialog implements android.view.View.OnClickListener {

    //이미지
    @BindView(R.id.dogsitter_profile_img)
    ImageView dogsitter_profile_img;
    //이름
    @BindView(R.id.name_money_txt)
    TextView name_money_txt;
    //날짜
    @BindView(R.id.data_money_txt)
    TextView data_money_txt;
    //주소
    @BindView(R.id.address_money_txt)
    TextView address_money_txt;
    //가격
    @BindView(R.id.price_txt)
    TextView price_txt;
    //결제버튼
    @BindView(R.id.paymoney_btn)
    Button paymoney_btn;
    //취소
    @BindView(R.id.cancel_btn)
    Button cancel_btn;
    private ApiConfig mapiconfig;
    private String name;
    private String price;
    private String myname;

    public info_Custom_Dialog_DogSitter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__custom__dialog__dog_sitter);
        ButterKnife.bind(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        //이미지를 스트링변수에담는다.
        String updateimage = sp.getString("더그시터이미지","");
        //나의이름을 스트리변수에 담는다
        myname = sp.getString("name","");
        //업데이트이름 스트링 변수에 에 더그시터이름를 담는다
        name = sp.getString("더그시터이름모음","");
        //업데이트가격 스트링 변수에 담아준다.
        price = sp.getString("더그시터가격","");
        //날짜를 스트링 변수에 담아준다
        String calendar_date = sp.getString("date","");
        //주소를 스트링 변수에 담아준다.
        String location_data = sp.getString("주소","");
        //이미지
        Glide.with(this.getContext()).load(updateimage).into(dogsitter_profile_img);
        //이름
        name_money_txt.setText(name);
        //가격
        price_txt.setText(price);
        //날짜
        data_money_txt.setText(calendar_date);
        //주소
        address_money_txt.setText(location_data);
        //사이즈조절
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = this.getWindow();
        window.setAttributes(lp);

    }


    //결제하기 버튼 +alterdialog
    @OnClick(R.id.paymoney_btn)
    public void setPaymoney_btn()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setTitle("결제하기");
        alertDialogBuilder
                .setMessage("결제를 진행하시곘습니까?")
                .setCancelable(false)
                .setPositiveButton("결제하기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                //결제하는 곳으로 이동하기
                                Intent intent = new Intent(getContext(),paymentwebview.class);
                                getContext().startActivity(intent);
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                            });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

//        Intent intent = new Intent(this.getContext(),paymentwebview.class);
//        this.getContext().startActivity(intent);



    }
    //취소하기버튼
    @OnClick(R.id.cancel_btn)
    public void setCancel_btn()
    {
        cancel();
    }
    @Override
    public void onClick(View v) {

    }



}
