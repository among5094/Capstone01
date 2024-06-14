package com.example.withpet_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeUserInfo extends AppCompatActivity {

    // 데이터베이스
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    // 에디트 텍스트
    private EditText edtName;
    private EditText edtNickname;
    private EditText edtId;
    private EditText edtPw;
    private EditText edtPhoneNum;

    // 버튼
    private Button changeBtn;
    private Button backBtn;

    // 기존 회원 정보
    private String originalName;
    private String originalNickname;
    private String originalId;
    private String originalPw;
    private String originalPhoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_userinfo);

        //** 테스트용 **

        edtName = findViewById(R.id.edtName);
        edtNickname = findViewById(R.id.edtNickname);
        edtId = findViewById(R.id.edtId);
        edtPw = findViewById(R.id.edtPw);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);
        changeBtn = findViewById(R.id.changeBtn);
        backBtn = findViewById(R.id.backBtn);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = auth.getUid();

        // 에디트 텍스트 비활성화
        setEditTextsEnabled(false);

        // 유저의 정보 에디트 텍스트에 채워 놓음
        if (userId != null) {
            databaseReference.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // 데이터가 존재하면 EditText에 설정
                        originalName = snapshot.child("name").getValue(String.class);
                        originalNickname = snapshot.child("nickname").getValue(String.class);
                        originalId = snapshot.child("userId").getValue(String.class);
                        originalPw = snapshot.child("password").getValue(String.class);
                        originalPhoneNum = snapshot.child("phoneNum").getValue(String.class);

                        edtName.setText(originalName);
                        edtNickname.setText(originalNickname);
                        edtId.setText(originalId);
                        edtPw.setText(originalPw);
                        edtPhoneNum.setText(originalPhoneNum);
                    } else {
                        Toast.makeText(ChangeUserInfo.this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChangeUserInfo.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ChangeUserInfo.this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 변경하기 버튼
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 변경하기 버튼을 누르면 에디트 텍스트 활성화 하고 저장하기로 버튼의 텍스트 변경
                if (changeBtn.getText().toString().equals("변경하기")) {
                    setEditTextsEnabled(true);
                    changeBtn.setText("저장하기");
                } else {
                    String name = edtName.getText().toString();
                    String nickname = edtNickname.getText().toString();
                    String id = edtId.getText().toString();
                    String pw = edtPw.getText().toString();
                    String phonenum = edtPhoneNum.getText().toString();

                    // 정보가 변경되지 않았으면 저장하지 않고, 다시 버튼의 텍스트를 변경하기로 바꾸고 에디트 텍스트 비활성화
                    if (name.equals(originalName) && nickname.equals(originalNickname) && id.equals(originalId) && pw.equals(originalPw) && phonenum.equals(originalPhoneNum)) {
                        Toast.makeText(ChangeUserInfo.this, "변경된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        setEditTextsEnabled(false);
                        changeBtn.setText("변경하기");
                    } else {
                        if (userId != null) {
                            databaseReference.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // 데이터가 존재하면 업데이트
                                        DatabaseReference userRef = snapshot.getRef();
                                        userRef.child("name").setValue(name);
                                        userRef.child("nickname").setValue(nickname);
                                        userRef.child("userId").setValue(id);
                                        userRef.child("password").setValue(pw);
                                        userRef.child("phoneNum").setValue(phonenum);

                                        // 업데이트하고 버튼의 텍스트를 다시 변경하기로 바꾸고 에디트 텍스트 비활성화
                                        Toast.makeText(ChangeUserInfo.this, "사용자 정보가 변경 되었습니다", Toast.LENGTH_SHORT).show();
                                        setEditTextsEnabled(false);
                                        changeBtn.setText("변경하기");

                                        // 기존 회원 정보 업데이트(에디트텍스트에 대입할 변수들)
                                        originalName = name;
                                        originalNickname = nickname;
                                        originalId = id;
                                        originalPw = pw;
                                        originalPhoneNum = phonenum;
                                    } else {
                                        Toast.makeText(ChangeUserInfo.this, "사용자 정보 변경 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChangeUserInfo.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ChangeUserInfo.this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // 이전으로 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userprofile.class);
                startActivity(intent);
            }
        });
    }

    // EditText들을 활성화 또는 비활성화
    // enabled가 true이면 EditText들이 활성화, false이면 비활성화
    private void setEditTextsEnabled(boolean enabled) {
        edtName.setEnabled(enabled);
        edtNickname.setEnabled(enabled);
        edtId.setEnabled(enabled);
        edtPw.setEnabled(enabled);
        edtPhoneNum.setEnabled(enabled);
    }
}