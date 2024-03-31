package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


//shift+alt+r 로 MainActivity 에서 LoginActivity로 바꾸기
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private  FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText mEmali, mPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmali=findViewById(R.id.login_email);
        mPassword=findViewById(R.id.login_password);

        findViewById(R.id.login_signup).setOnClickListener(this); //버튼이나 텍스트뷰나 클릭할 수 있는 건 똑같음.
        findViewById(R.id.login_success).setOnClickListener(this);

    }


    public void onClick(View v) {

        //int viewId = v.getId();

        if (v.getId() == R.id.login_signup) {
            startActivity(new Intent(this, SignupActivity.class));
        }
        else if (v.getId() == R.id.login_success) {
            mAuth.signInWithEmailAndPassword(mEmali.getText().toString(), mPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                // 로그인 할 때 null인지 아닌지 파악
                                if (user != null) {
                                    Toast.makeText(LoginActivity.this, "로그인 성공: " + user.getUid(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Login error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}