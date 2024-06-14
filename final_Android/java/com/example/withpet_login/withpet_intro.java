package com.example.withpet_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class withpet_intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withpet_intro);

        /* 지금은 삭제한 부분
        //애니메이션 텍스트 효과
        TextView text_ani = findViewById(R.id.text_ani);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        text_ani.startAnimation(fadeIn);
        */

        //인트로 화면 1초 표시 후 닫힘(메인화면 표시)
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}