package yoonhuijung.dogcareproject.get;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {

    public static String BASE_URL = "http://34.235.147.50/";

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}
