package com.example.firebasetest;
//회원가입 페이지

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;
import java.util.HashMap;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mEmailText, mPasswordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailText = findViewById(R.id.sign_email);
        mPasswordText= findViewById(R.id.sign_password);
        
        //this대신 익명 클래스 쓰기
        findViewById(R.id.sign_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
            }
        });
    }

    //회원가입 완료 버튼
    public void onClick(View v) {

        mAuth.createUserWithEmailAndPassword(mEmailText.getText().toString(), mPasswordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){

                    public void onComplete(@NonNull Task<AuthResult> task){
                        //회원가입이 완료됐을 때
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user != null ) {
                                //사용자의 데이터를 DB에 넣기( 데이터를 Firebase Firestore 데이터베이스에 저장)
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.documentId, user.getUid()); //user의 id
                                userMap.put(FirebaseID.email,  mEmailText.getText().toString()); //user의 email
                                userMap.put(FirebaseID.password, mPasswordText.getText().toString()); //user의 password
                                mStore.collection(FirebaseID.user).document(user.getUid()).set(userMap, SetOptions.merge());
                                finish();
                            }
                        }
                        else { //회원가입이 실패했을 때
                            //Toast.makeText(SignupActivity.this, "Sign up error.",
                                    //Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}