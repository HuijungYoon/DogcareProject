package yoonhuijung.dogcareproject.videocall;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.webrtc.RendererCommon;

import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class CallFragment extends android.app.Fragment {
    private View controlView;
    private TextView contactView;
    private ImageButton disconnectButton;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleMuteButton;
    private TextView captureFormatText;
    private SeekBar captureFormatSlider;
    private OnCallEvents callEvents;
    private RendererCommon.ScalingType scalingType;
    private boolean videoCallEnabled = true;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        void onCallHangUp();
        void onCameraSwitch();
        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
        void onCaptureFormatChange(int width, int height, int framerate);
        boolean onToggleMic();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);

        // Create UI controls.
        contactView = (TextView) controlView.findViewById(R.id.contact_name_call);
        disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);
        toggleMuteButton = (ImageButton) controlView.findViewById(R.id.button_call_toggle_mic);
        captureFormatText = (TextView) controlView.findViewById(R.id.capture_format_text_call);
        captureFormatSlider = (SeekBar) controlView.findViewById(R.id.capture_format_slider_call);

        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCallHangUp();
            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCameraSwitch();
            }
        });

        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                } else {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
                    scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
                }
                callEvents.onVideoScalingSwitch(scalingType);
            }
        });
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabled = callEvents.onToggleMic();
                toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
            }
        });

        return controlView;
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
            String contactName = args.getString(CallActivity.EXTRA_ROOMID);
            contactView.setText(contactName);
            videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
            captureSliderEnabled = videoCallEnabled
                    && args.getBoolean(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
        if (captureSliderEnabled) {
            captureFormatSlider.setOnSeekBarChangeListener(
                    new CaptureQualityController(captureFormatText, callEvents));
        } else {
            captureFormatText.setVisibility(View.GONE);
            captureFormatSlider.setVisibility(View.GONE);

        }
    }

    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }
}
