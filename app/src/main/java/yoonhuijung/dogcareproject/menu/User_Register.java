package yoonhuijung.dogcareproject.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import yoonhuijung.dogcareproject.Interface.ApiConfig;
import yoonhuijung.dogcareproject.get.ResultModel;


public class User_Register extends AppCompatActivity {

    private static String IP_ADDRESS = "34.235.147.50";
    private static String TAG = "hellopet";

    //에딧텍스트 이메일,비밀번호,비밀번호확인,이름
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private EditText mEditTextPwconfirm;
    private EditText mEditTextName;
    private TextView mTextViewResult;
    private TextView mTextViewChidResult;
  //  private UserApi muserApi;
    private ApiConfig mapiConfig;
    private List<ResultModel> resultModelList;
    private static  boolean bool_retro = false;
    static private Boolean clicked_validate_btn = false;
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__register);

////        //타이틀바 이름 바꾸기
////        ActionBar actionBar = getSupportActionBar();
////        actionBar.setTitle("회원가입");
//        //타이틀바에 백버튼 만들기
//        actionBar.setDisplayHomeAsUpEnabled(true);
        //에딧텍스트 이메일,비밀번호,비밀번호확인,이름,결과 초기화
        mEditTextEmail = (EditText) findViewById(R.id.email_edit);
        mEditTextPw = (EditText) findViewById(R.id.pw_edit);
        mEditTextPwconfirm = (EditText) findViewById(R.id.pwconfirm_edit);
        mEditTextName = (EditText) findViewById(R.id.name_edit);
        mTextViewResult = (TextView) findViewById(R.id.result_txtview);
        mTextViewChidResult = (TextView) findViewById(R.id.checkid_result);
        //알람창다이얼로그
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
         final AlertDialog.Builder builders = new AlertDialog.Builder(this);

        resultModelList = new ArrayList<>();

        //레트로핏초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.e("주소",ApiConfig.BASE_URL);


        mapiConfig = retrofit.create(ApiConfig.class);


        //회원가입 버튼
        ImageView buttonAdduser = (ImageView) findViewById(R.id.adduser_btn);
        buttonAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEditTextEmail.getText().toString();
                String pw = mEditTextPw.getText().toString();
                String pwconfirm = mEditTextPwconfirm.getText().toString();
                String name = mEditTextName.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert_original.php", email, pw, pwconfirm, name);

//                mEditTextEmail.setText("");
                mEditTextPw.setText("");
                mEditTextPwconfirm.setText("");
//                mEditTextName.setText("");
                //인텐트로 화면전환


            }
        });

        //아이디중복버튼
        Button buttonValidate_email = (Button) findViewById(R.id.validate_email_btn);
        buttonValidate_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<ResultModel> call = mapiConfig.checkemail(mEditTextEmail.getText().toString());

                clicked_validate_btn = true;
                call.enqueue(new Callback<ResultModel>() {
                    @Override
                    public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                        ResultModel result = response.body();

                        mTextViewChidResult.setText("");
                        if(result.getResult().equals("NO") && !mTextViewChidResult.getText().equals("사용가능한 아이디입니다."))
                        {
                             //Toast.makeText(User_Register.this,"NO",Toast.LENGTH_LONG);

                            mTextViewChidResult.setText("중복된아이디입니다,");
                            bool_retro = false;
                            //Toast.makeText(User_Register.this, "중복된 아이디입니다.", Toast.LENGTH_SHORT).show();
                            //clicked_validate_btn = false;
                        }else
                        {
                            mTextViewChidResult.setText("사용 가능한 아이디입니다.");
                            bool_retro = true;
                            //clicked_validate_btn = true;
                            //Toast.makeText(User_Register.this, "가입할수있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
//
                    }

                    @Override
                    public void onFailure(Call<ResultModel> call, Throwable t) {

                    }
                });

                 if(mEditTextEmail.getText().toString().length() ==0)
                {
                    mTextViewChidResult.setText("이메일을 입력해주세요,");
                }

            }



        });


    }




    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(User_Register.this,"잠시만 기다려주세요",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG,"POST response -" + result);

            if(result.equals("\t새로운 사용자를 추가했습니다.") && clicked_validate_btn && bool_retro) {
                Intent intent = new Intent(getApplication(), AfterLogin.class);
                //이름 쉐어드프리퍼런스에 저장
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("이름",mEditTextName.getText().toString());
                editor.commit();
                startActivity(intent);
            }
            else if(!clicked_validate_btn)
            {
                mTextViewResult.setText("중복확인 버튼을 눌러주세요,");
            }
            else if(mEditTextName.getText().toString().length() == 0)
            {
                mTextViewResult.setText("이름을 입력해주세요");
            }
            else if(!bool_retro)
            {
                mTextViewResult.setText("중복된 아이디입니다.");
            }

            else
            {
                mTextViewResult.setText("입력하신 비밀번호를 다시한번 확인해주세요,");
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String email = (String)params[1];
            String pw = (String)params[2];
            String pwconfirm = (String)params[3];
            String name = (String)params[4];
            String serverURL = (String)params[0];
            String postParameters = "email=" + email + "&pw=" + pw + "&pwconfirm=" + pwconfirm + "&name=" + name;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }


        }
    }

}
