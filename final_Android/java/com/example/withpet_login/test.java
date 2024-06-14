package com.example.withpet_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.withpet_login.MainHome;
import com.example.withpet_login.Post;
import com.example.withpet_login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class test extends AppCompatActivity {
    private EditText edtTitle;
    private EditText edtContent;

    private ImageView selectedImageView;

    private DatabaseReference mDatabase;
    private DatabaseReference uDatabase;
    private FirebaseUser user;

    // 휴대폰 갤러리에 접근하기 위해 선언한 코드 번호
    private final int PICK_IMAGE_REQUEST = 10;
    private FirebaseStorage storage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // EditText 초기화
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);

        // ImageView
        selectedImageView = findViewById(R.id.selectedImageView);

        // Button
        Button postBtn = findViewById(R.id.postBtn);
        Button photoChoiceBtn = findViewById(R.id.photoChoiceBtn);

        // 데이터베이스
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance("https://withpetfirebase-default-rtdb.firebaseio.com").getReference();
        // FirebaseAuth 인스턴스에서 현재 사용자 가져오기
        user = mAuth.getCurrentUser();

        // Firebase storage 접근 인스턴스 선언
        storage = FirebaseStorage.getInstance();


        // 이전으로 버튼
        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), MainHome.class);
                startActivity(Intent);
            }
        });


        // 게시물 업로드 버튼
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                // 사용자의 이름 가져오기
                String uid = user.getUid();
                // 사용자의 프로필 사진 가져오기
                getUserProfileImageUrl(uid, new OnProfileImageUrlFetchedListener() {
                    @Override
                    public void onProfileImageUrlFetched(String profileImgUrl) {
                        // 사용자 이름 가져오기
                        uDatabase.child("UserAccount").child(uid).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String name = task.getResult().getValue(String.class);
                                    // 사용자의 이름을 가져와서 addPost() 메서드에 전달
                                    addPost(uid, profileImgUrl, name, title, content);
                                } else {
                                    // 사용자 이름을 가져오는 데 실패한 경우 처리할 내용 추가
                                    Toast.makeText(test.this, "사용자 이름을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        // 사진 선택 버튼
        photoChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAlbum();
            }
        });

    }

    // 게시물 올리기 메소드
    private void addPost(String uid, String profileImgUrl, String name, String title, String content) {

        if (!title.isEmpty() && !content.isEmpty()) {
            String postId = mDatabase.push().getKey();
            if (postId != null) {
                // 이미지를 선택한 경우 Firebase Storage에 업로드합니다
                if (selectedImageUri != null) {
                    StorageReference storageRef = storage.getReference();
                    // 특수 문자를 제거하여 파일 이름을 생성
                    String fileName = "image" + postId + ".jpg"; // "image"를 추가하고 ".jpg"로 확장자를 고정합니다
                    StorageReference imageRef = storageRef.child("users/" + user.getUid() + "/posts/" + postId + "/" + fileName);

                    imageRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 이미지 업로드가 성공하면 다운로드 URL을 가져옴
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 업로드 시간
                                    Date currentTime = Calendar.getInstance().getTime();
                                    long timeStamp = currentTime.getTime();
                                    // 이미지의 다운로드 URL로 Post 개체 생성
                                    Post post = new Post(postId, uid, profileImgUrl, name, title, content, uri.toString(), timeStamp);
                                    // 게시물 정보 데이터베이스에 저장합니다
                                    mDatabase.child(postId).setValue(post);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(test.this, "사진 선택 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // 이미지가 선택되지 않았을 경우 이미지를 제외하고 나머지 업로드
                    Date currentTime = Calendar.getInstance().getTime();
                    long timeStamp = currentTime.getTime();
                    Post post = new Post(postId, uid, profileImgUrl, name, title, content, null, timeStamp);
                    // 게시물 정보 데이터베이스에 저장
                    mDatabase.child(postId).setValue(post);
                }
            }

            // 게시물 추가 후 제목, 내용 텍스트 초기화
            edtTitle.setText("");
            edtContent.setText("");
            selectedImageUri = null; // selectedImageUri 초기화
            selectedImageView.setVisibility(View.GONE); // ImageView 숨기기
            // 게시물 작성 성공 토스트 메세지
            Toast.makeText(test.this, "게시물이 작성되었습니다", Toast.LENGTH_SHORT).show();
        }
        // 제목과 내용이 입력되지 않은 경우
        else if(title.isEmpty() && !content.isEmpty()) {
            Toast.makeText(test.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else if(!title.isEmpty() && content.isEmpty()) {
            Toast.makeText(test.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else if(title.isEmpty() && content.isEmpty()) {
            Toast.makeText(test.this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }

    }


    // 사용자 프로필 이미지 URL 가져오기 메소드
    private void getUserProfileImageUrl(String uid, OnProfileImageUrlFetchedListener listener) {
        uDatabase.child("UserAccount").child(uid).child("profileImgUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String profileImgUrl = task.getResult().getValue(String.class);
                    listener.onProfileImageUrlFetched(profileImgUrl);
                } else {
                    Toast.makeText(test.this, "프로필 이미지를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 인터페이스 정의
    private interface OnProfileImageUrlFetchedListener {
        void onProfileImageUrlFetched(String profileImgUrl);
    }


    // 사진 선택 메소드
    private void loadAlbum() {
        Intent intentPhoto = new Intent(Intent.ACTION_PICK);
        intentPhoto.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intentPhoto, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // 선택한 사진의 Uri 저장
            try {
                InputStream in = getContentResolver().openInputStream(selectedImageUri);
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                selectedImageView.setImageBitmap(img);
                selectedImageView.setVisibility(View.VISIBLE); // 이미지 선택 후에 ImageView를 표시
                // 이미지 업로드 성공시 토스트 메세지
                Toast.makeText(test.this, "사진이 선택되었습니다", Toast.LENGTH_SHORT).show();
                
            } catch (Exception e) {
                e.printStackTrace();
                // 이미지 업로드 실패시 토스트 메세지
                Toast.makeText(test.this, "사진 가져오기 실패", Toast.LENGTH_SHORT).show();
            }
        }

        // 이미지가 선택되었을 때만 이미지뷰를 보이도록 설정
        if (selectedImageUri != null) {
            selectedImageView.setVisibility(View.VISIBLE);
        } else {
            selectedImageView.setVisibility(View.GONE);
        }
    }


}
