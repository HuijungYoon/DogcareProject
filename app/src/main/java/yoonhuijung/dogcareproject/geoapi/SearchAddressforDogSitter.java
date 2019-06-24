package yoonhuijung.dogcareproject.geoapi;

import android.Manifest;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;

import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.location.places.PlaceBuffer;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;


import java.util.ArrayList;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import yoonhuijung.dogcareproject.RecyclerCollection.adapter.PlaceAutocompleteAdapter;
import yoonhuijung.dogcareproject.menu.DogSitterActivity;


public class SearchAddressforDogSitter extends AppCompatActivity implements PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,

        GoogleApiClient.ConnectionCallbacks, OnClickListener {


    private static final int REQ_CODE_SPEECH_INPUT = 100;


    private GoogleApiClient googleApiClient;

    private RecyclerView rvAutocomplateKeyword;

    private LinearLayoutManager llm;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(

            new LatLng(-0, 0), new LatLng(0, 0));

    private EditText edSearch = null;

    private Button current_location;

    //구글  api 현재위치 추척
    private PlaceDetectionClient mPlaceDetectionClient;

    //위치 정보 사용시 동의한다.
    private boolean mLocationPermissionGranted;

    public int i =0;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static boolean mycurrentbtnboolean = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_addressfor_dog_sitter);


        //초기화
        init();

        //현재 로케이션에대한 권한을 허락해준다.
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(SearchAddressforDogSitter.this, "권한을 허가해준다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SearchAddressforDogSitter.this, "권한거부" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("구글접근을위해서는 주소록 접근이 필요해요")
                .setDeniedMessage("거부되셨어요,설정->권한에서 허용가능")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        // 초기화
        current_location = (Button) findViewById(R.id.mylocation_btn_dogsitter);

        // 내현재위치불러오기버튼
        current_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Log.i("되는건가요?", String.format("Place '%s' has likelihood: %g",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));

                            if(i == 0) {
                                i++;

                                double mycurrent_latitude_dogsitter;
                                double mycurrent_longtitude_dogsitter;
                                mycurrentbtnboolean = true;
                                //위도경도 더블->스트링으로 변환
                                mycurrent_latitude_dogsitter = placeLikelihood.getPlace().getLatLng().latitude;
                                String str_mycurrent_latitude_dogsitter =Double.toString(mycurrent_latitude_dogsitter);
                                mycurrent_longtitude_dogsitter = placeLikelihood.getPlace().getLatLng().longitude;
                                String str_mycurrent_longtitude_dogsitter =Double.toString(mycurrent_longtitude_dogsitter);

                                String mylocationaddress = placeLikelihood.getPlace().getAddress().toString();
                                String mylocationname = placeLikelihood.getPlace().getName().toString();
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("내위치더그시터",mylocationaddress+"("+mylocationname+")");
                                editor.putInt("비교값더그시터",i);
                                editor.putString("내위치위도더그시터",str_mycurrent_latitude_dogsitter);
                                editor.putString("내위치경도더그시터",str_mycurrent_longtitude_dogsitter);
                                editor.commit();

                            }

                        }
                        likelyPlaces.release();
                        Intent intent = new Intent(getApplicationContext(), DogSitterActivity.class);
                        startActivity(intent);



                    }
                });

            }
        });
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

    }

    private void init() {
        //버튼 초기화

        // 장소 찾기 초기화
        initPlace();
        getLocationPermission();

    }

    //현재 위치에 대한 허가여부를 일겅온다.
    private void getLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    //펄미션에 대하 결과
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void initPlace() {

        this.googleApiClient = new GoogleApiClient.Builder(this)

                .enableAutoManage(this, 0 /* clientId */, this)

                .addApi(Places.GEO_DATA_API)

                .build();


        this.rvAutocomplateKeyword = (RecyclerView) findViewById(R.id.address_recycle_dogsitter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());

        rvAutocomplateKeyword.addItemDecoration(dividerItemDecoration);

        this.rvAutocomplateKeyword.setHasFixedSize(true);

        this.llm = new LinearLayoutManager(SearchAddressforDogSitter.this);

        this.rvAutocomplateKeyword.setLayoutManager(llm);


        this.edSearch = (EditText) findViewById(R.id.search_ed_dogsitter);

        this.placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, R.layout.item_search,

                googleApiClient, BOUNDS_INDIA, null);

        this.rvAutocomplateKeyword.setAdapter(placeAutocompleteAdapter);


        // 글자를 입력하면 place api를 요청한다.

        this.edSearch.addTextChangedListener(new TextWatcher() {

            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > 0) {

                    if (placeAutocompleteAdapter != null) {

                        rvAutocomplateKeyword.setVisibility(View.VISIBLE);
                    }

                } else {

                    if (placeAutocompleteAdapter != null) {

                        placeAutocompleteAdapter.clearList();

                        rvAutocomplateKeyword.setVisibility(View.GONE);

                    }


                }

                if (!s.toString().equals("") && googleApiClient.isConnected()) {

                    placeAutocompleteAdapter.getFilter().filter(s.toString());

                } else if (!googleApiClient.isConnected()) {

                    Log.e("", "NOT CONNECTED");

                }

            }


            @Override

            public void afterTextChanged(Editable s) {


            }

        });


    }

    @Override

    public void onClick(View v) {


        switch (v.getId()) {
        }



    }



    @Override

    public void onConnected(Bundle bundle) {



    }



    @Override

    public void onConnectionSuspended(int i) {



    }



    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {



    }

    @Override

    public void onStart() {

        this.googleApiClient.connect();

        super.onStart();



    }


    @Override

    public void onStop() {

        this.googleApiClient.disconnect();

        super.onStop();

    }


    @Override
    public void OnPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> resultList, int position) {
        if (resultList != null) {

            try {

                final String placeId = String.valueOf(resultList.get(position).placeId);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi

                        .getPlaceById(googleApiClient, placeId);

                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override

                    public void onResult(PlaceBuffer places) {

                        if (places.getCount() == 1) {

                            // 이곳에서 키워드를 선택한 데이터를 처리한다.

                            Location location = new Location(places.get(0).getName().toString());

                            location.setLatitude(places.get(0).getLatLng().latitude);

                            location.setLongitude(places.get(0).getLatLng().longitude);

                            double latitude_for_dogsitter;
                            double longitude_for_dositter;
                            mycurrentbtnboolean = false;
                            //위도경도 더블->스트링으로 변환
                            latitude_for_dogsitter = places.get(0).getLatLng().latitude;
                            String str_mycurrent_latitude =Double.toString(latitude_for_dogsitter);
                            longitude_for_dositter = places.get(0).getLatLng().longitude;
                            String str_mycurrent_longtitude =Double.toString(longitude_for_dositter);


                            Intent intent = new Intent(getApplicationContext(),DogSitterActivity.class);
                            intent.putExtra("add_dogsitter",places.get(0).getName().toString());
                            intent.putExtra("adds_dogsitter",places.get(0).getAddress().toString());
                            startActivity(intent);

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("주소더그시터",places.get(0).getAddress().toString()+"("+places.get(0).getName().toString()+")");
                            editor.putString("위도직접입력더그시터",str_mycurrent_latitude);
                            editor.putString("경도직접입력더그시터",str_mycurrent_longtitude);
                            editor.commit();

                        } else {

                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();

                        }

                    }

                });

            } catch (Exception e) {



            } finally {



                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        placeAutocompleteAdapter.clearList();

                        rvAutocomplateKeyword.setVisibility(View.GONE);

                    }

                });



            }



        }
    }
}