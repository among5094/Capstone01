package com.example.firebaseregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.firebase.auth.FirebaseUser;
/* LoginActivity.java에서 새로 추가된 임포트문!
* 로그인한 사용자의 정보를 관리하하고 이 클래스를 통해 사용자의 아이디, 이메일, 프로필 사진 URL 등 사용자의 상세 정보에 접근할 수 있음. */

//아래의 두 임포트는 Firebase Realtime Database의 데이터를 참조하고 조작하는 데 사용되는 클래스.
import com.google.firebase.database.DatabaseReference;//Firebase 데이터베이스
import com.google.firebase.database.FirebaseDatabase;//특정 위치에 대한 참조


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; //파이어 베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText mEtEmail,mEtPwd; //이메일,패스워드
    private Button mBtnRegister; //회원가입 등록버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase 인증 및 데이터베이스 작업을 수행하기 위해 필요한 인스턴스를 가져오기 위해 선언
        mFirebaseAuth = FirebaseAuth.getInstance();// Firebase 인증을 위한 인스턴스를 가져옴.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();//Firebase Realtime Database에서 데이터를 읽거나 쓸 수 있다.

        //이메일이랑 패스워드 가지고 오기
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        
        //회원가입 완료 버튼
        mBtnRegister=findViewById(R.id.btn_register);

        //버튼이 클릭됐을 때
        mBtnRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                //이메일 주소와 비밀번호가 문자열 형태인 변수에 저장
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                // 이메일과 비밀번호를 사용해서 Firebase에 새로운 사용자를 등록
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); //현재 등록된 사용자의 정보를 가져옴

                            // 객체를 생성하고 사용자의 UID, 이메일, 비밀번호를 저장
                            UserAccount account = new UserAccount(); //생성된 현재 사용자의 고유 식별자(UID)를 반환 이 UID를 account 객체의 idToken 필드에 저장됨
                            account.setIdToken(firebaseUser.getUid());// 사용자가 등록할 때 사용한 이메일 주소를 account 객체의 emailId 필드에 저장
                            account.setEmailId(firebaseUser.getEmail()); // 사용자가 등록할 때 사용한 이메일 주소를 account 객체의 emailId 필드에 저장
                            account.setPassword(strPwd); // 사용자가 등록할 때 입력한 비밀번호가 account 객체의 password 필드에 저장

                            //Firebase Realtime Database의 참조인 mDatabaseRef를 사용하여 데이터베이스에 사용자 정보를 저장하는 역할
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            //회원가입 성공 시 토스트 메세지
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다!", Toast.LENGTH_SHORT).show();

                        } else {
                            //회원가입 실패 시 토스트 메세지
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}