package com.example.withpet_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class create_account extends AppCompatActivity {
    //회원가입_사용자 정보 입력_이전으로 버튼
    //activit_main 화면으로 돌아감 (로그인 화면)
    private Button btnBackCreate1;
    //회원가입 사용자 정보 입력 다음으로 버튼
    private Button btnNextCreate1;

    //텍스트뷰
    private TextView textName;
    private TextView textNickname;
    private TextView textID;
    private TextView textPassword;
    private TextView textCheckpw;
    private TextView textPhonenum;
    private TextView textEmailAddress;

    //에딧텍스트
    private EditText editName;
    private EditText editNickname;
    private EditText editID;
    private EditText editPassword;
    private EditText editCheckpw;
    private EditText editPhonenum;
    private EditText editEmailAddress;

    //비밀번호 확인 버튼
    private Button btnCheckpw;


    //각 에딧텍스트 조건 충족 확인
    boolean nameisok, nicknameisok, idisok, passwordisok, checkpwisok, phonenumisok, emailaddressisok;

    //모든 에딧텍스트 true인지 나타내는 변수
    boolean isok = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //회원가입 사용자 정보 입력 이전으로 버튼
        btnBackCreate1 = (Button) findViewById(R.id.btn_back);
        //회원가입 사용자 정보 입력 다음으로 버튼
        btnNextCreate1 = (Button) findViewById(R.id.btn_next);

        //회원가입 사용자 정보 입력 텍스트뷰들
        textName = (TextView) findViewById(R.id.create_text_username);
        textNickname = (TextView) findViewById(R.id.create_text_nickname);
        textID = (TextView) findViewById(R.id.create_text_id);
        textPassword = (TextView) findViewById(R.id.create_text_pw);
        textCheckpw = (TextView) findViewById(R.id.create_text_checkpw);
        textPhonenum = (TextView) findViewById(R.id.create_text_phonenum);
        textEmailAddress = (TextView) findViewById(R.id.create_text_eamil_address);

        //회원가입 사용자 정보 입력 에딧텍스트들
        editName = (EditText) findViewById(R.id.create_username);
        editNickname = (EditText) findViewById(R.id.create_nickname);
        editID = (EditText) findViewById(R.id.create_id);
        editPassword = (EditText) findViewById(R.id.create_pw);
        editCheckpw = (EditText) findViewById(R.id.create_checkpw);
        editPhonenum = (EditText) findViewById(R.id.create_phonenum);
        editEmailAddress = (EditText) findViewById(R.id.create_email_address);

        //비밀번호 확인 버튼
        btnCheckpw = (Button) findViewById(R.id.create_checkpw_btn);

        //회원가입 사용자 정보 입력 이전으로 버튼 클릭 이벤트
        btnBackCreate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //회원가입 사용자 정보 입력 다음으로 버튼 클릭 이벤트
        btnNextCreate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), create_account_two.class);
                //모든 정보가 입력이 되었는지 확인
                if (nameisok && nicknameisok && idisok && passwordisok && checkpwisok && phonenumisok && emailaddressisok) {
                    isok = true;
                }
                //모든 정보가 입력 되었으면(isok가 true이면) 다음으로 버튼 누를 시 다음 페이지로 넘어감
                if (isok) {
                    intent.putExtra("name", editName.getText().toString());
                    intent.putExtra("nickname", editNickname.getText().toString());
                    intent.putExtra("id", editID.getText().toString());
                    intent.putExtra("password", editPassword.getText().toString());
                    intent.putExtra("phonenum", editPhonenum.getText().toString());
                    intent.putExtra("email", editEmailAddress.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(create_account.this, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //이름 에딧텍스트 채워지면 닉네임 텍스트뷰와 에딧텍스트 visible로 변경
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textNickname.setVisibility(View.VISIBLE);
                    editNickname.setVisibility(View.VISIBLE);
                    nameisok = true;
                } else {
                    textNickname.setVisibility(View.INVISIBLE);
                    editNickname.setVisibility(View.INVISIBLE);
                    nameisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //닉네임 에딧텍스트 채워지면 아이디 텍스트뷰와 에딧텍스트 visible로 변경
        editNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textID.setVisibility(View.VISIBLE);
                    editID.setVisibility(View.VISIBLE);
                    nicknameisok = true;
                } else {
                    textID.setVisibility(View.INVISIBLE);
                    editID.setVisibility(View.INVISIBLE);
                    nicknameisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //아이디 에딧텍스트 채워지면 비밀번호 텍스트뷰와 에딧텍스트 visible로 변경
        editID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textPassword.setVisibility(View.VISIBLE);
                    editPassword.setVisibility(View.VISIBLE);
                    idisok = true;
                } else {
                    textPassword.setVisibility(View.INVISIBLE);
                    editPassword.setVisibility(View.VISIBLE);
                    idisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //비밀번호 에딧텍스트 채워지면 비밀번호 확인 텍스트뷰와 에딧텍스트 visible로 변경
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textCheckpw.setVisibility(View.VISIBLE);
                    editCheckpw.setVisibility(View.VISIBLE);
                    btnCheckpw.setVisibility(View.VISIBLE);
                    passwordisok = true;
                } else {
                    textCheckpw.setVisibility(View.INVISIBLE);
                    editCheckpw.setVisibility(View.INVISIBLE);
                    btnCheckpw.setVisibility(View.INVISIBLE);
                    passwordisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //비밀번호 확인 에딧텍스트 채워지면 휴대폰 번호 텍스트뷰와 에딧텍스트 visible로 변경
        editCheckpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textPhonenum.setVisibility(View.VISIBLE);
                    editPhonenum.setVisibility(View.VISIBLE);
                } else {
                    textPhonenum.setVisibility(View.INVISIBLE);
                    editPhonenum.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //비밀번호 확인 버튼(+비밀번호 조건 확인)
        btnCheckpw.setOnClickListener(v -> {
            //비밀번호 조건 충족시
            if (isValidPassword(editPassword.getText().toString())) {
                if (editPassword.getText().toString().equals(editCheckpw.getText().toString())) {
                    btnCheckpw.setText("일치");
                    btnCheckpw.setTextColor(Color.rgb(0, 215, 0));
                    Toast.makeText(create_account.this, "비밀번호가 일치합니다.", Toast.LENGTH_LONG).show();
                    checkpwisok = true;
                } else {
                    btnCheckpw.setText("불일치");
                    btnCheckpw.setTextColor(Color.rgb(200, 0, 0));
                    Toast.makeText(create_account.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
                    checkpwisok = false;
                }
                //비밀번호 조건 충족하지 않을시
            } else {
                Toast.makeText(create_account.this, "비밀번호는 최소 8자 이상이며, 특수문자나 대문자를 포함해야 합니다.", Toast.LENGTH_LONG).show();
            }
        });

        //휴대폰 번호 에딧텍스트 채워지면 이메일 주소 텍스트뷰와 에딧텍스트 visible로 변경
        editPhonenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textEmailAddress.setVisibility(View.VISIBLE);
                    editEmailAddress.setVisibility(View.VISIBLE);
                    phonenumisok = true;
                } else {
                    textEmailAddress.setVisibility(View.INVISIBLE);
                    editEmailAddress.setVisibility(View.INVISIBLE);
                    phonenumisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //이메일 주소 에딧텍스트 채워지면 emailaddressisok true
        editEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    emailaddressisok = true;
                } else {
                    emailaddressisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //비밀번호 조건 충족 확인 메소드
    private boolean isValidPassword(String password) {
        // 최소 8자 이상, 최소 하나의 대문자, 최소 하나의 특수 문자를 포함해야 함
        return !TextUtils.isEmpty(password) && password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    }
}
