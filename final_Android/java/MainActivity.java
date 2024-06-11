package com.example.withpet_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    // 데이터베이스
    private FirebaseAuth mFirebaseAuth; // Firebase 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스

    // 아이디 비밀번호 에디트텍스트
    private EditText edtId;
    private EditText edtPw;

    //회원가입 버튼
    private Button btnCreateAccount;
    //로그인 버튼
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // 아이디 비밀번호 입력창 에디트텍스트
        edtId = (EditText) findViewById(R.id.edtId);
        edtPw = (EditText) findViewById(R.id.edtPw);

        //회원가입 버튼
        btnCreateAccount = (Button) findViewById(R.id.btn_create_account);
        //로그인 버튼
        btnLogin = (Button) findViewById((R.id.btn_login)) ;

        //회원가입 버튼 클릭 이벤트
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), create_account.class);
                startActivity(intent);
            }
        });

        //로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edtId.getText().toString();
                String strPassword = edtPw.getText().toString();

                // 이메일과 비밀번호가 비어 있는지 확인
                if (strEmail.isEmpty() || strPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "이메일 또는 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    // Firebase를 사용하여 로그인 시도
                    mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //로그인 성공
                                Intent intent = new Intent(getApplicationContext(), MainHome.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(MainActivity.this,"아이디 또는 비밀번호가 올바르지 않습니다",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}