package kr.ac.solbridge.student.hyoon.dogcareproject;

import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.getchatdata;
import kr.ac.solbridge.student.hyoon.dogcareproject.chatting.getchatlist;
import kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview.LatitudeLongtitudeDogSitterRegister;
import kr.ac.solbridge.student.hyoon.dogcareproject.dogsitterRecyclerview.getDogsitter_Register;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiConfig {

    String BASE_URL = "http://34.235.147.50";

    @Multipart
    @POST("upload.php")
    Call<ServerResponse> upload(
        @Header("Authorization") String authorization, @Part("email") String sp_email,
                @PartMap Map<String, RequestBody> map
    );
    @GET("test.php")
    retrofit2.Call<ResultModel>checkemail(@Query("email") String email);

    @GET("Login.php")
    retrofit2.Call<SecondRresult>login(@Query("email") String email,@Query("pw") String pw);

    @GET("getdata.php")
    retrofit2.Call<List<GetdataAcc>> getdata(@Query("email") String email);
    @GET("insertCarrer.php")
    retrofit2.Call<ResultModel> InsertCarrer(@Query("email") String email, @Query("sitnames") String name,@Query("title") String title, @Query("contents") String contents);
    @GET("getcarrerdata.php")
    retrofit2.Call<List<GetdataAcc>> getcarrerdata(@Query("email") String email);
    @GET("updatename.php")
    retrofit2.Call<GetdataAcc> updatename(@Query("email") String email, @Query("name") String name);
    @GET("sitter_register_info.php")
    retrofit2.Call<GetdataAcc> sitter_register_info(@Query("email") String email, @Query("image") String image,@Query("name") String name, @Query("price") int price,@Query("latitude") String latitude,@Query("longtitude") String longtitude);
    @GET("sitter_register_list.php")
    retrofit2.Call<List<getDogsitter_Register>> getdogsitterRegister(@Query("email") String email);
    @GET("getsitter_latitude_longtitude.php")
    retrofit2.Call<List<LatitudeLongtitudeDogSitterRegister>> getdogsitterLatitudeLongtitude();
    @GET("sitters_carrer_info_personally.php")
    retrofit2.Call<List<getCarrerdata>> getCarrerdataPersonally(@Query("sitnames") String sitnames);
    @GET("insert_chat_info.php")
    retrofit2.Call<getchatdata> insertchatdata(@Query("image") String image,@Query("sender") String sender, @Query("msg") String msg, @Query("receiver") String receiver,@Query("send_time") String send_time);
    @GET("getchatlist.php")
    retrofit2.Call<List<getchatlist>> getchatlistPersonllay(@Query("receiver") String receiver);
    @GET("getchatpersonally.php")
    retrofit2.Call<List<getchatlist>> getchatPersonsData(@Query("sender") String sender);
    @GET("cash.php")
    retrofit2.Call<ResultModel> putpaymoney(@Query("price") String price);



}

