package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.accrecycle.AccountRecycerAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.PostAdapter;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.Updateitem;
import kr.ac.solbridge.student.hyoon.dogcareproject.updateaccrecycler.getCarrerdata;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class updateAccount extends AppCompatActivity {
    @BindView(R.id.update_name_edit)
    EditText nameEdit;
    @BindView(R.id.update_profile_image)
    ImageView update_profile_image;
    @BindView(R.id.update_recycler)
    RecyclerView update_recycler_view;
    @BindView(R.id.update_btn)
    Button update_btn;
    @BindView(R.id.updateplus_btn)
    ImageView updatepluse_imageview;
    @BindView(R.id.update_imageview)
    ImageView updatecarrer_imageview;

    LinearLayout imagelinearlayout;
    LinearLayoutManager mLayoutManger;
    private ArrayList<Updateitem> myDateset;
    private String postPath;
    private String mediaPath;
    private Uri photoUri;
    private Uri imgUri, photoURI, albumURI;
    private static final int FROM_ALBUM = 1;
    private final int CAMERA_CODE = 1111;

    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름
    PostAdapter mAdapter;
    private ApiConfig mapiconfig; //레트로핏 인터페이스
    private String mCurrentPhotoPath = "/path/";
    getCarrerdata getCarrerdata;
    private SharedPreferences sp;

    //뒤로가기 눌러졌을때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        ButterKnife.bind(this);

        myDateset = new ArrayList<>();


        //권한주기
        permission();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_name = sp.getString("name","");
        String profile_img = sp.getString("업데이트사진","");
        Glide.with(getApplicationContext()).load(profile_img).into(update_profile_image);
        update_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeDialog();
            }
        });
        nameEdit.setText(sp_name);
        updateRecyclerview();
        setRecyclerview();
        buildRetrofit();

        imagelinearlayout = (LinearLayout)findViewById(R.id.natural);

        Glide.with(this).load(R.drawable.sea).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    imagelinearlayout.setBackground(resource);
                }
            }
        });

    }
    //레트로핏 초기화및빌드
    public void buildRetrofit()
    {
        //레트로핏 빌드하기
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //mapiconfig 초기화
        mapiconfig = retrofit.create(ApiConfig.class);
    }

    //리사이클러뷰 셋
    public void setRecyclerview()
    {
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonText = sp.getString("수정본","");
        Type type = new TypeToken<ArrayList<Updateitem>>(){}.getType();
        myDateset = gson.fromJson(jsonText,type);
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        update_recycler_view.setLayoutManager(mLayoutManger);
        update_recycler_view.setItemAnimator(new DefaultItemAnimator());
        update_recycler_view.addItemDecoration(dividerItemDecoration);
        mAdapter = new PostAdapter(getApplicationContext(),myDateset);
        update_recycler_view.setAdapter(mAdapter);
    }

    //경력사항 업데이트하기
    @OnClick(R.id.update_imageview)
    void OnUpdateCarrerClick(View view)
    {

    }


    //수정하기버튼 + 프로필 사진으로 넘어가기
    @OnClick(R.id.update_btn)
    void OnItemButtonClick(View view){
         SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_email = sp.getString("email","");
        String name =nameEdit.getText().toString();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("업데이트이름",name);
        editor.commit();
       // String sp_name = sp.getString("name","");
        Call<GetdataAcc> called = mapiconfig.updatename(sp_email,name);
        called.enqueue(new Callback<GetdataAcc>() {
            @Override
            public void onResponse(Call<GetdataAcc> call, Response<GetdataAcc> response) {
                GetdataAcc getdataAcc = response.body();
                if(getdataAcc.getResult().equals("YES"))
                {
                    Toast.makeText(updateAccount.this, "수정완료.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(updateAccount.this, "수정실패.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetdataAcc> call, Throwable t) {

            }
        });

        String updateimagefile = sp.getString("업데이트용","");
        int a = sp.getInt("1",-1);
        String b = sp.getString("2","");
        String c = sp.getString("3","");
        Intent intent = new Intent();
        intent.putExtra("업데이트이름",name);
        intent.putExtra("업데이트사진",updateimagefile);
        intent.putExtra("1",a);
        intent.putExtra("2",b);
        intent.putExtra("3",c);
        setResult(RESULT_OK,intent);
        finish();
    }
    //경력추가하기 버튼
    @OnClick(R.id.updateplus_btn)
    void OnUpdateCarrerButton(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(updateAccount.this);

        //레이아웃설정
        final LinearLayout layout = new LinearLayout(updateAccount.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //이미지뷰와 Edit text선언
        final ImageView carrerimage = new ImageView(updateAccount.this);
        final EditText title = new EditText(updateAccount.this);
        final EditText contents = new EditText(updateAccount.this);

        //Text view 선언
        final TextView titletv = new TextView(updateAccount.this);
        titletv.setText("제목 :");
        final TextView contentstv = new TextView(updateAccount.this);
        contentstv.setText("내용 :");

        //리니어레이아웃 레이아웃파람 높이
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
        final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

        //제목
        builder.setTitle("경력사항추가");
        //내용
        builder.setMessage("추가하시려는 내용을 입력해주세요");

        //이미지

//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);
//        layout.addView(carrerimage,imageParams);
       // layout.addView(carrerimage, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //제목
        LinearLayout.LayoutParams contentParmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(titletv, contentParmas);
        layout.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        //내용
        LinearLayout.LayoutParams dateParmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(contentstv, dateParmas);
        layout.addView(contents, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


        //layout에 실행
        builder.setView(layout);

        //다이얼로그 취소버튼
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
            }
        });

        //Insert다이어로그 ok버튼
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                int img_carrer_int = R.drawable.takewalk;
                String str_title = title.getText().toString();
                String str_contents = contents.getText().toString();

                //Edittext to integer
                //ArrayList 아이템추가
                myDateset.add(0,new Updateitem(img_carrer_int,str_title,str_contents));
                //RecyclerView의 포지션에 맞추기
                mAdapter.notifyItemInserted(1);
                mAdapter.notifyDataSetChanged();
                //secDataset.addAll(myDateset);

                //쉐어드프리퍼런스에 저장된 이메일을 불러온다.
                sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("1",img_carrer_int);
                editor.putString("2",str_title);
                editor.putString("3",str_contents);
                editor.commit();

                final String sp_email = sp.getString("email","");
                String sitnames = sp.getString("업데이트이름","");
                Call<ResultModel> call = mapiconfig.InsertCarrer(sp_email,sitnames,str_title,str_contents);
                call.enqueue(new Callback<ResultModel>() {
                    @Override
                    public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                        ResultModel getdataAcc = response.body();

                        if(getdataAcc.getResult().equals("YES"))
                        {

                            Toast.makeText(updateAccount.this, "경력사항을 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(updateAccount.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResultModel> call, Throwable t) {

                    }
                });


            }
        });

        builder.show();

    }


    public void updateRecyclerview()
    {
        update_recycler_view.setLayoutManager(new LinearLayoutManager(this));

    }

    public void permission()
    {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(updateAccount.this, "권한을 허가해준다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(updateAccount.this, "권한거부" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진촬영및앨범접근을 위한 권한설정")
                .setDeniedMessage("거부되셨어요,설정->권한에서 허용가능")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 이미지와 비디오 파일을 서버에 전송
    private void uploadFile() {


        if (postPath == null || postPath.equals("")) {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
            return;
        } else {

            // Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> map = new HashMap<>();
            File file = new File(postPath);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sp_email = sp.getString("email","");
            SharedPreferences.Editor editor = sp.edit();

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
            editor.putString("수정프로필이미지",file.getName());
            editor.commit();
            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
            Call<ServerResponse> call = getResponse.upload("token",sp_email, map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            ServerResponse serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                    }else {

                        Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                    Log.v("Response gotten is", t.getMessage());
                }
            });
        }
    }

    //사진,갤러리,취소버튼이 나오는 모달을 뛰어주는 매소드
    private void MakeDialog()
    {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(updateAccount.this);

        alt_bld.setTitle("사진 업로드").setIcon(R.drawable.ic_calendar_svg_black).setCancelable(

                false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // 사진 촬영 클릭
                        selectPhoto();
                    }
                }).setNeutralButton("앨범선택",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int id) {
                        selectAlbum();

                    }
                });

        AlertDialog alert = alt_bld.create();

        alert.show();
    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }
        }
    }

    //앨범에서 선택하기
    public void selectAlbum()
    {
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,FROM_ALBUM);
    }

    //이미지파일만들기
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    //앨범 선택 클릭
    public void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f =new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    //사진찍기
    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        postPath = currentPhotoPath;
        update_profile_image.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }

    //사진 각도 조정하기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
        {
            return;
        }
        switch (requestCode) {
            case FROM_ALBUM: {
                if (data.getData() != null) {
                    try {
                        // Get the Image from data
                        Uri selectedImage = data.getData();
                         String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mediaPath = cursor.getString(columnIndex);
                        // Set the Image in ImageView for Previewing the Media
                        cursor.close();
                        postPath = mediaPath;
                        File albumFile = null;
                        albumFile = createImageFile();
                        photoURI = data.getData();
                        albumURI = Uri.fromFile(albumFile);
                        update_profile_image.setImageURI(photoURI);
                        uploadFile();
                        galleryAddPic();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sp.edit();
                        String uri = photoURI.toString();
                        editor.putString("업데이트용",uri);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("알림", "앨범에서 가져오기 에러");
                    }
                }
                break;
            }
            case CAMERA_CODE:
                getPictureForPhoto(); //카메라에서 가져오기
                uploadFile();
                break;
        }
    }
}
