package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Calendar;
import java.util.Date;


import kr.ac.solbridge.student.hyoon.dogcareproject.R;

public class calendar extends AppCompatActivity implements OnDateSelectedListener {
   private  MaterialCalendarView materialCalendarView;
   Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
       materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        CalendarOption();
       //날짜 클릭리스너
        materialCalendarView.setOnDateChangedListener(this);



    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    public void CalendarOption()
    {
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018,0,1))
                .setMaximumDate(CalendarDay.from(2030,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new OneDayDecorator());
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {
        Log.d("Selected",""+selected);
        Toast.makeText(this,"enterDateSelected"+date,Toast.LENGTH_SHORT).show();
        if(selected == true)
        {
            Toast.makeText(this,"OnClick"+date,Toast.LENGTH_SHORT).show();
            //현재날짜를 선택하게되면 AlertDialog를 켜게된다.
            int month = date.getMonth()+1;
            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_edit_document)
                    .setTitle("산책시간선택")
                    .setMessage("선택날짜: "+date.getYear()+"년 "+month+" 월"+date.getDay()+" 일")
                    .setPositiveButton("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int month = date.getMonth()+1;
                            //도그워킹 액티비티로 이동
                            Intent intent = new Intent(getApplication(),DogWalking.class);
                            startActivity(intent);
                            //쉐어드프리퍼런스를 이용해서 내가 선택한 년/월/일을 저장한다.
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("date",date.getYear()+"년"+month+"월"+date.getDay()+"일");
                            editor.commit();

                        }
                    })
                    .setNegativeButton("취소",null)
                    .show();



        }

    }
}
//일요일색깔을 빨갛게 해주는 메소드
 class SundayDecorator implements DayViewDecorator{

    private final Calendar calendar = Calendar.getInstance();
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}

//토요일색깔을 파랗게 해주는 메소드
class SaturdayDecorator implements DayViewDecorator{

    private final Calendar calendar = Calendar.getInstance();
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.BLUE));
    }
}
//현재날짜 색깔을 지정하는 메소드
class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;

    public OneDayDecorator() {
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
        view.addSpan(new ForegroundColorSpan(Color.GREEN));
    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}

