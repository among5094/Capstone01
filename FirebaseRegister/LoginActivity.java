package com.example.firebaseregister;
//파이어베이스 연동했으니까 이제 구현하기

//안드로이드 부분 임포트
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

/*  Firebase와 같은 Google Play 서비스를 사용하는 애플리케이션에서 비동기적 작업을 수행하고 그 결과를 처리하는 임포트문 */
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//파이어베이스 임포트
import com.google.firebase.auth.AuthResult;
/* Firebase Authentication을 통해 인증된 사용자의 결과를 나타내는 클래스.
 사용자가 성공적으로 로그인하거나 계정을 생성한 후 반환됨. */

import com.google.firebase.auth.FirebaseAuth;
/* Firebase Authentication을 관리하는 클래스.
 사용자 인증, 로그인, 로그아웃 등의 기능을 제공함. */

//아래의 두 임포트는 Firebase Realtime Database의 데이터를 참조하고 조작하는 데 사용되는 클래스.
import com.google.firebase.database.DatabaseReference;//Firebase 데이터베이스
import com.google.firebase.database.FirebaseDatabase;//특정 위치에 대한 참조


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어 베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText mEtEmail,mEtPwd; //이메일,패스워드
    private Button mBtnRegister, mBtnLogin; //회원가입 버튼, 로그인 버튼



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase 인증 및 데이터베이스 작업을 수행하기 위해 필요한 인스턴스를 가져오기 위해
        mFirebaseAuth = FirebaseAuth.getInstance();// Firebase 인증을 위한 인스턴스를 가져옴.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(); //Firebase Realtime Database에서 데이터를 읽거나 쓸 수 있다.

        //이메일이랑 패스워드 가지고 오기
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        
        //회원가입, 로그인 버튼 생성
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnLogin = findViewById(R.id.btn_login);


        //로그인 버튼 눌렀을 때 LoginActivity에서 MainActivity 화면을 전환을 위한 이벤트 리스너
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// 이메일과 비밀번호 입력값을 가져옴
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();


                // Firebase Authentication을 사용하여 이메일과 비밀번호로 로그인 시도
                mFirebaseAuth.signInWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //task: Firebase에서 로그인 요청의 결과를 담고 있음.
                        /* onComplete메소드: 비동기 작업이 완료되었을 때 호출됨. */

                        // 로그인 성공했을 때
                        if(task.isSuccessful()){
                            //로그인 성공
                            Intent intent= new Intent(LoginActivity.this, MainActivity.class); //인텐트는 화면 전환할 때 사용함
                            //LoginActivity(로그인 화면)에서 MainActivity(로그인에 성공했을 때 나올 화면)으로 전환
                            startActivity(intent);//활동 시작
                            finish();//메인 화면으로 완전히 전환되었음을 의미
                        }else {
                            //// 로그인 실패 했을 때 사용자에게 메시지 표시하기
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //회원가입 버튼 눌렀을 때 LoginActivity에서 RegisterActivity로 화면을 전환
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this, RegisterActivity.class); //현재의 LoginActivity에서 새로운 RegisterActivity로 이동
                startActivity(intent);// 로그인 화면에서 회원가입 화면으로 전환

            }
        });

    }
}