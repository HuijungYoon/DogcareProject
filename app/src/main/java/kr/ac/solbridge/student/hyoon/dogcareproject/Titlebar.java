package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import kr.ac.solbridge.student.hyoon.dogcareproject.MainActivity;
import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class Titlebar extends RelativeLayout
{
    private Context context;

    public Titlebar(Context context) {
        super(context);
        init();
    }

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Titlebar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.context = getContext();
        inflate(getContext(), R.layout.title_bar, this);
    }

    private OnClickListener btnBackClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(context instanceof Activity)) {
                return;
            }

            // 이 레이아웃을 생성한 액티비티가 메인 액티비티가 아닌 경우에 한해
            // 이전 화면으로 돌아가는 작업을 수행한다.
            if (!(context instanceof MainActivity)) {
                Activity activity = (Activity)context;
                activity.finish();
            }
        }
    };

    private OnClickListener btnHomeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // 메인 화면으로 복귀. 이전에 생성된 액티비티들은 모두 종료.
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);

            if ((context instanceof Activity)) {
                Activity activity = (Activity)context;
                activity.finish();
            }
        }
    };
}

