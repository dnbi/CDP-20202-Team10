package com.example.kioskmainpage.Activity.Waiting;

import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.example.kioskmainpage.Activity.BestNewMenuActivity;
import com.example.kioskmainpage.Activity.EasyMenuSelectionActivity;
import com.example.kioskmainpage.Activity.MainActivity;
import com.example.kioskmainpage.Activity.Pay.Senior_Pay_Loading;
import com.example.kioskmainpage.Activity.Senior_MenuOption.Senior_OrderListActivity;
import com.example.kioskmainpage.Adapter.SelectedListAdapter;
import com.example.kioskmainpage.Myapplication;
import com.example.kioskmainpage.R;

import java.util.Locale;

public class Order_Method_Selection_Popup extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    private TextView title;
    public SelectedListAdapter adapter;
    Intent intent;
    private TextToSpeech tts;
    String engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__method__selection__popup);

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        this.setFinishOnTouchOutside(false);

        title = (TextView)findViewById(R.id.title_view);
        //Spannable span = (Spannable) title.getText();
        //span.setSpan(new ForegroundColorSpan(getColor(R.color.light_green)), 27, 34, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate(1.0f);
                    tts.speak("원하시는 주문 방식을 선택해 주세요. 키오스크 사용이 처음이시면 간단주문을 눌러 사용해보세요.",TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClick_normal(View v){
        setResult(4);
        finish();
        return;
    }

    public void onClick_easy(View v){
        setResult(5);
        finish();
        return;
    }

    public void onClick_cancel(View v){
        setResult(3);
        finish();
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onBackPressed() {
        return;
    }
}
