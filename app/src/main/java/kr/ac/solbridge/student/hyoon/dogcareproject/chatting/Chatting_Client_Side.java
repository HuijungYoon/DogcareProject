package kr.ac.solbridge.student.hyoon.dogcareproject.chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.solbridge.student.hyoon.dogcareproject.ApiConfig;
import kr.ac.solbridge.student.hyoon.dogcareproject.ClickListener;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;
import kr.ac.solbridge.student.hyoon.dogcareproject.apprtc.CallActivity;
import kr.ac.solbridge.student.hyoon.dogcareproject.apprtc.SSLConnect;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chatting_Client_Side extends AppCompatActivity implements View.OnClickListener,ReceiverThread.OnReceiveListener {

    @BindView(R.id.message_edit)
    EditText message_edit;
    @BindView(R.id.send_btn)
    Button send_btn;
    @BindView(R.id.chatRecycler)
    RecyclerView chatRecycler;
    @BindView(R.id.videocall_client_side)
    Button videocall_client_side;
    private LinearLayoutManager mLayoutManger;
    private ChattingAdapter mAdapter;
    private ArrayList<Chat_item> chatItemArrayList;
    //private SenderThread mthread11;
    private SenderThread mThread1;
    private Socket mSocket = null;
    public String NAME;
    public String SitterName;
    public static final String IPADRRESS= "your IPaddress";
    //public static final String IPADRRESS= "192.168.0.58";
    public static String division;
    private ApiConfig mapiconfig;

    //영상통화관련
    private static boolean commandLineRun = false;
    public SharedPreferences sharedPref;
    private String keyprefRoomServerUrl;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private static final int CONNECTION_REQUEST = 1;
    private String keyprefVideoCallEnabled;
    private String keyprefScreencapture;
    private String keyprefCamera2;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefFlexfec;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisableBuiltInAec;
    private String keyprefDisableBuiltInAgc;
    private String keyprefDisableBuiltInNs;
    private String keyprefEnableLevelControl;
    private String keyprefDisableWebRtcAGCAndHPF;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoom;
    private String keyprefRoomList;
    private ArrayList<String> roomList;
    private ArrayAdapter<String> adapter;
    private String keyprefEnableDataChannel;
    private String keyprefOrdered;
    private String keyprefMaxRetransmitTimeMs;
    private String keyprefMaxRetransmits;
    private String keyprefDataProtocol;
    private String keyprefNegotiated;
    private String keyprefDataId;
    private static final String rootAddr = "https://appr.tc";
    private static String PageSource=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting__client__side);
        ButterKnife.bind(this);

        //리싸이클러뷰배경
        chatRecycler.setBackgroundResource(R.drawable.theme_background_image);
        //채팅배열목록
        chatItemArrayList = new ArrayList<>(); //어레이리스트초기화
        send_btn.setOnClickListener(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_name = sp.getString("name","");


        //영통관련
        Settingvideocall();
        //ssl오류관련
        sslrelated();

        NAME = sp_name;
        setRecyclerView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 일단은 테스트 용으로 본인의 아이피를 입력해서 진행하겠습니다.
                try {
                    mSocket = new Socket(IPADRRESS, 7800);
                    // 두번째 파라메터 로는 본인의 닉네임을 적어줍니다.
                    mThread1 = new SenderThread(mSocket, NAME);
                    ReceiverThread thread2 = new ReceiverThread(mSocket);
                    thread2.setOnReceiveListener(Chatting_Client_Side.this);
                    mThread1.start();
                    thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //영상통화버튼
    @OnClick(R.id.videocall_client_side)
    void VideoCall(View view)
    {
        //난수를 생성한다
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String roomnumstr = sp.getString("방진짜번호","");
        String testroom ="67135";
        connectToRoom(testroom,false,false,false,0);
    }


    //ssl관련
    public void sslrelated()
    {
        //SSL인증서 오류해결
        //(javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.)
        SSLConnect ssl = new SSLConnect();
        ssl.postHttps(rootAddr,1000,1000);
        final String Queuesource = rootAddr;
        new Thread()
        {
            public void run()
            {
                PageSource = getHttpHTML_GET(Queuesource);
            }
        }.start();
    }

    public static String getHttpHTML_GET(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        String line;
        String result = "";
        String parameter =  String.format("input_file_name=%s", URLEncoder.encode("inp_file_1323792610_20160225131004.txt"));
        // String parameter =  String.format("param1=%s¶m2=%s",URLEncoder.encode("param1"),URLEncoder.encode("param2"));

        try {
            //url = new URL(urlToRead);
            url = new URL(urlToRead +  "?" + parameter);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//          br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"euc-kr"));
            while ((line = br.readLine()) != null)
                result += line + "\n";
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //영상통화관련
    public void Settingvideocall()
    {
        // Get setting keys.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefScreencapture = getString(R.string.pref_screencapture_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefFlexfec = getString(R.string.pref_flexfec_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key);
        keyprefOrdered = getString(R.string.pref_ordered_key);
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key);
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key);
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key);
        keyprefNegotiated = getString(R.string.pref_negotiated_key);
        keyprefDataId = getString(R.string.pref_data_id_key);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            // Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    public boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }


    public String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    public int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }

    public boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    //영상통화
    public void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                              boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        //쉐어드 프리퍼런스에 저장된 룸 url을 스트링 변수 roomUrl에 담는다.
        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        //비디오로 찍는 권한을 허락해줘서 카메라기능중 비디오를 쓸수있게해준다..
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // 카메라기능중 스크린캡쳐기능권한을 허락해준다.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // 카메라2의기능인데 기존 카메라 api보다 훨씬 많은 기능들을사용할수 있다.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // 기본적으로 저장된 코드를 사용한다.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // 화면 표번에 나타나는 글씨들을 캡쳐할것인지 설정하는기능
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // 음소거 설정기능
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // 오디오 과정 설정해주는 기능
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // 오디오 관련 OPEN SL ES
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    //Log.e(TAG, "Wrong video resolution setting: " + resolution);
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    // Log.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity.
        // Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallActivity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    //리사이클러뷰 초기화
    private void  setRecyclerView()
    {
        mLayoutManger = new LinearLayoutManager(this);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        chatRecycler.setLayoutManager(mLayoutManger);
        chatRecycler.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ChattingAdapter(getApplicationContext(), chatItemArrayList, new ClickListener() {
            @Override
            public void OnPositionClicked(int position) {

            }

            @Override
            public void OnLongClicked(int position) {

            }
        });
        chatRecycler.setAdapter(mAdapter);
    }

    //메세지보내기
    @Override
    public void onClick(View v) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sp_sittername = sp.getString("더그시터이름모음","");
        String image = sp.getString("업데이트사진","");
        String sp_senderimage = sp.getString("imageurl","");
        onReceiveme(message_edit.getText().toString()+">"+NAME);
        mThread1.sendMessage(NAME+">"+message_edit.getText().toString()+">"+sp_sittername+">"+sp_senderimage);
        //레트로핏으로 mysql에 저장
        initializeRetrofit();
        Date dt = new Date();
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String send_time = sdfNow.format(dt).toString();
        getCarrerdataFromSever(image,NAME,message_edit.getText().toString(),sp_sittername,send_time);
        message_edit.setText("");

    }
    @WorkerThread
    public void onReceiveme(final String message) {
        String[] split = message.split(">");
        String msg = split[0];
        String nickname = split[1];
        final Chat_item chat_item = new Chat_item(nickname,msg,true);
        if (NAME.equals(nickname)) {
            // 나
            chat_item.isMe = true;
        }
        chat_item.nickname = nickname;
        chat_item.messagechatting = msg;
        // UI 스레드로 실행
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 메세지 갱신
                chatItemArrayList.add(chat_item);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    @WorkerThread
    public void onReceive(final String message) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] split = message.split(">");
        String sender = split[0];
        String msg = split[1];
        String nickname = split[2];
        String senderimage = split[3];
        String SenderThatisent = sp.getString("더그시터이름모음","");
        //내가보낸사람과 서버에서 보낸사람이 불일치하면 걸러준다.
        if(!SenderThatisent.equals(sender))
        {
            return;
        }
        final Chat_item chat_item = new Chat_item(sender,msg,false);
        chat_item.isMe = false;
        chat_item.nickname = sender;
        chat_item.messagechatting = msg;
        // UI 스레드로 실행
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 메세지 갱신
                chatItemArrayList.add(chat_item);
                mAdapter.notifyDataSetChanged();
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

    //채팅문삽입하는 레트로핏
    public void getCarrerdataFromSever(final String z, String a, final String b, final String c, final String d)
    {
        Call<getchatdata> call =mapiconfig.insertchatdata(z,a,b,c,d);
        call.enqueue(new Callback<getchatdata>() {
            @Override
            public void onResponse(Call<getchatdata> call, Response<getchatdata> response) {
                getchatdata getchatdata = response.body();
                if(getchatdata.getResult().equals("YES"))
                {
                    System.out.println("성공");
                }
                else
                {
                    System.out.println("실패");
                }
            }

            @Override
            public void onFailure(Call<getchatdata> call, Throwable t) {

            }
        });


    }


    }

