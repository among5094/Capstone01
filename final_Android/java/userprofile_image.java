package com.example.withpet_login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class userprofile_image extends Activity{
// 이미지 액티비티 클래스

    // 액티비티 생성 시 호출되는 메서드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile_photo);

        // 이미지 설정
        ImageView imageView = findViewById(R.id.imageView);
        setImage(imageView);
    }

    // 이미지 설정 메서드
    private void setImage(ImageView imageView) {
        // 인텐트로부터 이미지 ID 읽어오기
        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        if (extras != null && extras.containsKey("image ID")) {
            int imageID = extras.getInt("image ID");
            imageView.setImageResource(imageID);
        }
    }
}
