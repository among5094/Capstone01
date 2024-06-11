package com.example.withpet_login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class create_account_two extends AppCompatActivity {

    // 데이터베이스
    private FirebaseAuth mFirebaseAuth; // Firebase 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스

    //회원가입 반려견 정보 입력 버튼
    Button btnBackCreate2;
    Button btnNextCreate2;
    //회원가입 반려견 정보 입력 버튼 끝

    private EditText petName;
    private EditText petEtc;
    private RadioGroup petGender;
    private RadioButton selectedRadioButton;

    //캘린더 변수
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private int yearD, monthD, dayD;
    //캘린더 변수 끝

    //반려견 종 기타
    private TextView petSpeciesEtc;
    private Spinner petSpeciesSpnr;
    private EditText petSpeciesEdt;
    //반려견 종 기타 끝

    //각 에딧텍스트 조건 충족 확인
    boolean petnameisok, petbirthisok, petgenderisok, petspeciesisok, petetcisok;


    //모든 에딧텍스트 true인지 나타내는 변수
    boolean isok = false;

    //반려견 성별 변수
    private String create_Petgender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_two);

        // 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance("https://withpet-a03ff-default-rtdb.firebaseio.com").getReference();

        //이전, 다음으로 버튼
        btnBackCreate2 = (Button) findViewById(R.id.btn_back);
        btnNextCreate2 = (Button) findViewById(R.id.btn_next);
        //이전, 다음으로 버튼 끝

        // 반려견 정보
        petName = (EditText) findViewById(R.id.create_petname);
        petEtc = (EditText) findViewById(R.id.create_etc);
        petGender = (RadioGroup) findViewById(R.id.create_petgender_radiogroup);
        petSpeciesEdt = (EditText) findViewById(R.id.create_petspecies_etc);
        // 반려견 정보 끝

        //캘린더 변수
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        //캘린더 변수 끝

        //반려견 종 기타
        petSpeciesEtc = (TextView) findViewById(R.id.create_petspecies_etc);
        petSpeciesSpnr = (Spinner) findViewById(R.id.create_spinner_petspecies);
        //반려견 종 기타 끝

        //회원가입 반려견 정보 입력 이전으로 버튼 클릭 이벤트
        btnBackCreate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), create_account.class);
                startActivity(intent);
            }
        });

        //회원가입 반려견 입력 회원가입 완료 버튼 클릭 이벤트
        btnNextCreate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // 모든 정보가 입력되었는지 확인
                if (petnameisok && petbirthisok && petgenderisok && petspeciesisok && petetcisok) {
                    // 사용자가 입력한 정보를 변수에 할당
                    // create_account -> create_account2 데이터 받아오기
                    // ex) strName = name으로 하면 Realtime Database로는 정보가 넘어가지 않는 오류
                    Intent intent_toss = getIntent();
                    String strName = intent_toss.getStringExtra("name");
                    String strNickname = intent_toss.getStringExtra("nickname");
                    String strId = intent_toss.getStringExtra("id");
                    String strPassword = intent_toss.getStringExtra("password");
                    String strPhonenum = intent_toss.getStringExtra("phonenum");
                    String strEmail = intent_toss.getStringExtra("email");
                    String strPetname = petName.getText().toString();
                    String strPetbirth = (String) dateButton.getTag();
                    String strPetgender = create_Petgender;
                    String strPetspecies = petSpeciesSpnr.getSelectedItem().toString();
                    String strPetetc = petEtc.getText().toString();

                    // 모든 정보가 입력되었으면(isok가 true이면) 확인 버튼 누를 시 회원가입 후 로그인으로 넘어감
                    // 회원가입 처리 시작
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(create_account_two.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 회원가입이 성공한 경우에만 데이터베이스에 정보를 저장
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                // 사용자 객체 생성
                                UserAccount account = new UserAccount();
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPassword);
                                account.setName(strName);
                                account.setUserId(strId);
                                account.setNickname(strNickname);
                                account.setPhoneNum(strPhonenum);
                                account.setProfileImgUrl(null);

                                // 반려견 객체 생성
                                Pet pet = new Pet();
                                pet.setName(strPetname);
                                pet.setBirth(strPetbirth);
                                pet.setSpecies(strPetspecies);
                                pet.setEtc(strPetetc);
                                pet.setGender(strPetgender);

                                // UserAccount에 반려동물 정보 추가
                                account.addPet(pet);


                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 데이터베이스에 성공적으로 저장됨
                                        Log.d(TAG, "Data saved successfully");
                                        Toast.makeText(create_account_two.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                        startActivity(intent); // 로그인으로 넘어감
                                        finish(); // 현재 화면 종료
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 데이터베이스 저장 실패
                                        Log.e(TAG, "Error saving data to Firebase Realtime Database", e);
                                    }
                                });
                            } else {
                                // 회원가입에 실패한 경우의 처리
                                Toast.makeText(create_account_two.this, "회원가입에 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(create_account_two.this, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //회원가입 버튼 클릭 리스너 끝


        //반려견 종 기타
        //선택 항목이 변경될 때마다 호출되는 리스너 설정
        petSpeciesSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("직접입력")) {
                    petSpeciesEtc.setVisibility(View.VISIBLE);
                    //반려견 종 기타
                    petSpeciesEdt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (!charSequence.toString().isEmpty()) {
                                petspeciesisok = true;
                            } else {
                                petspeciesisok = false;
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                } else {
                    petSpeciesEtc.setVisibility(View.GONE);
                    petspeciesisok = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //반려견 종 기타 끝

        //모두 채워졌는지 확인
        //반려견 이름
        petName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    petnameisok = true;
                } else {
                    petnameisok = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //반려견 성별
        petGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // 선택이 된 경우
                if (i != -1) {
                    selectedRadioButton = findViewById(i);
                    create_Petgender = selectedRadioButton.getText().toString();
                    petgenderisok = true;
                }
                // 선택이 되지 않은 경우
                else {
                    petgenderisok = false;
                }
            }
        });


        petetcisok = true;
        //반려견 기타사항
        petEtc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //모두 채워졌는지 확인 끝
    }

    //캘린더 입력
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        yearD = year;
        monthD = month;
        dayD = day;

        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
                petbirthisok = true;

                // 데이터베이스에 저장할 날짜 형식
                String formattedDate = makeFormattedDateString(year, month, day);
                dateButton.setTag(formattedDate); // dateButton의 태그에 저장
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    //생년월일 입력 후 표시 글자
    private String makeDateString(int day, int month, int year)
    {
        return year + "년 " + month + "월 " + day + "일";
    }

    //데이터베이스에 저장할 날짜 형식(yyyy-mm-dd)
    private String makeFormattedDateString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }



}