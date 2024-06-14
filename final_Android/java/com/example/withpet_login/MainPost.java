package com.example.withpet_login;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MainPost extends AppCompatActivity {

    // Firebase 데이터베이스 참조 변수
    private DatabaseReference mDatabase;
    private DatabaseReference uDatabase;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private Uri selectedImageUri;

    // 프래그먼트 관리자 및 프래그먼트 변수
    //bottom navigation view
    private FragmentManager fragmentManager;
    private MainMenuHomeFragment fragmentHome;
    private MainMenuProfileFragment fragmentProfile;
    private MainMenuPostFragment fragmentPost;

    // 이미지뷰 변수들
    private static final int REQUEST_CODE = 1;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;

    EditText edtContent;
    EditText edtTitle;

    // 회원정보변경 임시 버튼
    Button changeUserInfoBtn;

    // TensorFlow Lite 관련 변수
    private Interpreter tflite;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // 데이터베이스 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance("https://withpet-a03ff-default-rtdb.firebaseio.com").getReference();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        Button button = findViewById(R.id.imgup); // 사진 선택 버튼
        Button postBtn = findViewById(R.id.postup); // 게시글 올리기 버튼
        imageView = findViewById(R.id.petimg); // 선택한 사진
        imageView2 = findViewById(R.id.petimg2); // 선택한 사진
        imageView3 = findViewById(R.id.petimg3); // 선택한 사진
        imageView4 = findViewById(R.id.petimg4); // 선택한 사진
        imageView5 = findViewById(R.id.petimg5); // 선택한 사진

        edtContent = findViewById(R.id.postEdit); // 작성한 게시글(게시글 내용 입력창)
        edtTitle = findViewById(R.id.titleEdit); // 작성한 제목(게시글 제목 입력창)


        // TensorFlow Lite 모델 로드
        try {
            tflite = new Interpreter(loadModelFile(this, "hm.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TFLite", "Failed to load model: " + e.getMessage());
        }

        // 사진 선택 버튼 클릭 리스너 설정
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // 게시물 업로드 버튼 클릭 리스너 설정
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(MainPost.this, "게시글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이미지가 선택되지 않았을 경우 업로드 중단
                if (selectedImageUri == null) {
                    Toast.makeText(MainPost.this, "동물 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 사용자의 이름과 프로필 이미지 URL 가져오기
                String uid = user.getUid();
                getUserProfileImageUrl(uid, new OnProfileImageUrlFetchedListener() {
                    @Override
                    public void onProfileImageUrlFetched(String profileImgUrl) {
                        uDatabase.child("UserAccount").child(uid).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String name = task.getResult().getValue(String.class);
                                    addPost(uid, profileImgUrl, name, title, content);
                                } else {
                                    Toast.makeText(MainPost.this, "사용자 이름을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        // Bottom Navigation View 설정(프래그먼트 관리자 초기화)
        fragmentManager = getSupportFragmentManager();
        fragmentHome = new MainMenuHomeFragment();
        fragmentProfile = new MainMenuProfileFragment();
        fragmentPost = new MainMenuPostFragment();

        // Bottom Navigation View 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_post);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tab_layout, fragmentPost).commitAllowingStateLoss();

        bottomNavigationView.setSelectedItemId(R.id.navigation_post);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(getApplicationContext(), MainHome.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.navigation_post) {
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
                    intent = new Intent(getApplicationContext(), userprofile.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    // Bottom Navigation View 항목 선택 리스너
    class ItemSelectedListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (menuItem.getItemId() == R.id.navigation_home) {
                transaction.replace(R.id.tab_layout, fragmentHome).commitAllowingStateLoss();
            } else if (menuItem.getItemId() == R.id.navigation_post) {
                transaction.replace(R.id.tab_layout, fragmentPost).commitAllowingStateLoss();
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                transaction.replace(R.id.tab_layout, fragmentProfile).commitAllowingStateLoss();
            }

            return true;
        }
    }

    // 게시물 업로드 메소드
    private void addPost(String uid, String profileImgUrl, String name, String title, String content) {
        if (!title.isEmpty()) {
            String postId = mDatabase.push().getKey();
            if (postId != null) {
                // 이미지를 선택한 경우 Firebase Storage에 업로드합니다
                if (selectedImageUri != null) {
                    StorageReference storageRef = storage.getReference();
                    String fileName = "image" + postId + ".jpg";
                    StorageReference imageRef = storageRef.child("users/" + user.getUid() + "/posts/" + postId + "/" + fileName);

                    imageRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 업로드 시간
                                    Date currentTime = Calendar.getInstance().getTime();
                                    long timeStamp = currentTime.getTime();
                                    // 이미지의 다운로드 URL로 Post 개체 생성
                                    Post post = new Post(postId, uid, profileImgUrl, name, title, content, uri.toString(), timeStamp);
                                    // 게시물 정보 데이터베이스에 저장
                                    mDatabase.child(postId).setValue(post);
                                    // 게시물 추가 후 제목, 내용 텍스트 초기화
                                    edtTitle.setText("");
                                    edtContent.setText("");
                                    selectedImageUri = null;
                                    Toast.makeText(MainPost.this, "게시물이 작성되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainPost.this, "사진 선택 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Date currentTime = Calendar.getInstance().getTime();
                    long timeStamp = currentTime.getTime();
                    Post post = new Post(postId, uid, profileImgUrl, name, title, content, null, timeStamp);
                    mDatabase.child(postId).setValue(post);
                    edtTitle.setText("");
                    edtContent.setText("");
                    selectedImageUri = null;
                    Toast.makeText(MainPost.this, "게시물이 작성되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainPost.this, "게시글을 작성해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 사용자 프로필 이미지 URL을 가져오는 메소드
    private void getUserProfileImageUrl(String uid, OnProfileImageUrlFetchedListener listener) {
        uDatabase.child("UserAccount").child(uid).child("profileImgUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String profileImgUrl = task.getResult().getValue(String.class);
                    listener.onProfileImageUrlFetched(profileImgUrl);
                } else {
                    Toast.makeText(MainPost.this, "프로필 이미지를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // TensorFlow Lite 모델 파일을 로드하는 메소드
    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // TensorFlow Lite 모델을 이용하여 이미지 분류를 수행하는 메소드
    private String classifyImage(Bitmap bitmap) {
        int inputSize = 416; // 모델이 기대하는 입력 크기 설정 (예: 416x416x3)

        // 입력 버퍼 생성
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3).order(ByteOrder.nativeOrder());

        // 입력 이미지 리사이즈
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);

        // 이미지 데이터를 입력 버퍼에 넣기
        int[] intValues = new int[inputSize * inputSize];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                inputBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }

        // YOLOv5 모델의 출력 크기를 기준으로 버퍼 크기를 조정합니다
        int outputSize = 10647; // YOLOv5 모델의 출력 크기 (예시)
        int numClasses = 6; // 클래스 수 (고양이, 강아지, 새, 토끼, 물고기, 햄스터)
        float[][][] output = new float[1][outputSize][numClasses + 5]; // YOLOv5의 출력 크기
        tflite.run(inputBuffer, output);

        // 확률이 가장 높은 클래스를 찾습니다
        int maxIndex = -1;
        float maxProbability = 0;
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < numClasses; j++) {
                float classProbability = output[0][i][4] * output[0][i][5 + j];
                if (classProbability > maxProbability) {
                    maxProbability = classProbability;
                    maxIndex = j;
                }
            }
        }

        // 신뢰도가 0.7 이상인 경우만 반환함
        if (maxProbability >= 0.7) {
            switch (maxIndex) {
                case 0:
                    return "Cat";
                case 1:
                    return "Dog";
                case 2:
                    return "Bird";
                case 3:
                    return "Rabbit";
                case 4:
                    return "Fish";
                case 5:
                    return "Hamster";
                default:
                    return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }




    // onActivityResult 메소드: 사진 선택 후 호출
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
                String result = classifyImage(bitmap);
                if (!result.equals("Unknown")) {
                    Toast.makeText(this, "이 사진 속 동물은" + result + "입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "확실하지 않은 사진입니다. 다른 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // 프로필 이미지 URL을 가져오는 리스너 인터페이스
    interface OnProfileImageUrlFetchedListener {
        void onProfileImageUrlFetched(String profileImgUrl);
    }



}
