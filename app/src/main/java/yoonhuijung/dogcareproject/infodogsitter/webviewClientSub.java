package yoonhuijung.dogcareproject.infodogsitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class webviewClientSub extends Activity {

    private WebView mainWebView;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentwebview);

//        mainWebView = (WebView) findViewById(R.id.mainWebView);
//        mainWebView.loadUrl("http://www.naver.com");

        Intent intent = getIntent();
        String url = intent.getDataString();
        System.out.println();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String url = intent.getDataString();
        System.out.println(url);
    }


}
