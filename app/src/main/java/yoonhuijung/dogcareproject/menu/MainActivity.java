package yoonhuijung.dogcareproject.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

import custom_font.MyTextView;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import yoonhuijung.dogcareproject.Interface.ApiConfig;
import yoonhuijung.dogcareproject.get.SecondRresult;
import yoonhuijung.dogcareproject.get.ResultModel;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final int RC_SIGN_IN = 10;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageView main_imgview;
    private List<ResultModel> resultModelList;
    private List<SecondRresult> resultModelListtwo;
    //private LoginApi mLoginApi;
    private ApiConfig mapiConfig;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private com.kakao.usermgmt.LoginButton kakaologinButton;
    private SessionCallback callback;
    private  EditText mail_edit;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //com.kakao.auth.Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        callback = new SessionCallback();
       // com.kakao.auth.Session.getCurrentSession().addCallback(callback);
        Requestme();
        //페이스북 콜백매니저
        callbackManager = CallbackManager.Factory.create();

        //페이스북 로그이버튼


        //이메일
         mail_edit = (EditText) findViewById(R.id.mail_login);
        //로그인
        final EditText pw_edit = (EditText) findViewById(R.id.lock_login);

        //페이스북로그인

        loginButton = (LoginButton) findViewById(R.id.login_button);
        ImageView img_fb = (ImageView) findViewById(R.id.custom_fb_imageview);
        img_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                startActivity(new Intent(MainActivity.this, AfterLogin.class));

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        resultModelList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.e("주소", ApiConfig.BASE_URL);


        mapiConfig = retrofit.create(ApiConfig.class);


        //이미지 사이즈 조정하기
//        main_imgview = (ImageView) findViewById(R.id.main_imgview);
        //강아지 사진 넣기
//        Glide
//                .with(this)
//                .load(R.drawable.dog).into(main_imgview);




        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //구글 로그인 버튼생성

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

       final SignInButton button = (SignInButton) findViewById(R.id.sign_in_button);
       //커스텀 디자인클릭
       ImageView google_custom_btn = (ImageView) findViewById(R.id.google_img);
        google_custom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                button.performClick();

            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });

        //회원가입 누를시 회원가입 엑티비티로 화면 전환

        MyTextView register_btn = (MyTextView) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인텐트 생성
                Intent intent = new Intent(getApplication(), User_Register.class);
                startActivity(intent);
            }
        });


        //로그인 버튼
        ImageView login_btn = (ImageView) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<SecondRresult> call = mapiConfig.login(mail_edit.getText().toString(), pw_edit.getText().toString());

                call.enqueue(new Callback<SecondRresult>() {
                    @Override
                    public void onResponse(Call<SecondRresult> call, final Response<SecondRresult> response) {
                        SecondRresult result = response.body();
                        if (result.getResult().equals("NO")) {

                            Toast.makeText(MainActivity.this, "로그인성공", Toast.LENGTH_LONG).show();
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("email",mail_edit.getText().toString());
                            editor.commit();

                            //화면전환
                            startActivity(new Intent(MainActivity.this, AfterLogin.class));

                        } else {
                            //clicked_validate_btn = true;
                            Toast.makeText(MainActivity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SecondRresult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //카카오콜백
        if (com.kakao.auth.Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);


        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "로그인 성공.", Toast.LENGTH_SHORT).show();
                            //인텐트 생성
                            Intent intent = new Intent(getApplication(), AfterLogin.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.

                        }

                        // ...
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    long number = userProfile.getId();
                    Log.e("아이디값", String.valueOf(number));
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

        }
    }
    public void Requestme()
    {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("실패", "error message=" + errorResult);
//                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

                Log.d("세션종료", "onSessionClosed1 =" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d("아니다", "onNotSignedUp ");

            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.e("UserProfile", result.toString());
                Log.e("UserProfile", result.getId() + "");
            }
        });

    }
}

