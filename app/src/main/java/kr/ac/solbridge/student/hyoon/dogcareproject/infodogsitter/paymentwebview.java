package kr.ac.solbridge.student.hyoon.dogcareproject.infodogsitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kr.ac.solbridge.student.hyoon.dogcareproject.ApiConfig;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.ResultModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class paymentwebview extends Activity {

    private WebView mainWebView;
    private static final String APP_SCHEME = "iamportapp://";
    private ApiConfig mapiconfig;
    private String name;
    private String price;
    private String myname;
    private static String url = "http://34.235.147.50/cash.php/";
    private String postData;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentwebview);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //나의이름을 스트리변수에 담는다
        myname = sp.getString("name","");
        //업데이트이름 스트링 변수에 에 더그시터이름를 담는다
        name = sp.getString("더그시터이름모음","");
        //업데이트가격 스트링 변수에 담아준다.
        price = sp.getString("더그시터가격","");


        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.setWebViewClient(new webviewClient(this));
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        try {
            postData = "price="+ URLEncoder.encode(price,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mainWebView, true);
        }
        Intent intent = getIntent();
        Uri intentData = intent.getData();

        if ( intentData == null ) {

            mainWebView.postUrl(url,postData.getBytes());
        } else {
            //isp 인증 후 복귀했을 때 결제 후속조치
            String url = intentData.toString();
            if ( url.startsWith(APP_SCHEME) ) {
                String redirectURL = url.substring(APP_SCHEME.length()+3);
                mainWebView.loadUrl(redirectURL);
            }
        }



    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getDataString();
        if ( url.startsWith(APP_SCHEME) ) {
            String redirectURL = url.substring(APP_SCHEME.length()+3);
            mainWebView.loadUrl(redirectURL);
        }
    }
}
