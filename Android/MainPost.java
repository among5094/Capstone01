package com.example.withpet_login;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class MainPost extends AppCompatActivity {

    // 데이터베이스
    private DatabaseReference mDatabase;
    private DatabaseReference uDatabase;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private Uri selectedImageUri;

    //bottom navigation view
    private FragmentManager fragmentManager;
    private MainMenuHomeFragment fragmentHome;
    private MainMenuProfileFragment fragmentProfile;
    private MainMenuPostFragment fragmentPost;

    private static final int REQUEST_CODE = 1;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;

    EditText edtContent;
    EditText edtTitle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // 데이터베이스
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance("https://withpet-a03ff-default-rtdb.firebaseio.com").getReference();
        // FirebaseAuth 인스턴스에서 현재 사용자 가져오기
        user = mAuth.getCurrentUser();
        // Firebase storage 접근 인스턴스 선언
        storage = FirebaseStorage.getInstance();

        Button button = findViewById(R.id.imgup); // 사진 선택 버튼
        Button postBtn = findViewById(R.id.postup); // 게시글 올리기 버튼
        imageView = findViewById(R.id.petimg); // 선택한 사진
        imageView2 = findViewById(R.id.petimg2); // 선택한 사진
        imageView3 = findViewById(R.id.petimg3); // 선택한 사진
        imageView4 = findViewById(R.id.petimg4); // 선택한 사진
        imageView5 = findViewById(R.id.petimg5); // 선택한 사진

        edtContent = findViewById(R.id.postEdit); // 작성한 게시글
        edtTitle = findViewById(R.id.titleEdit); // 작성한 제목

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //게시물 업로드 버튼
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(MainPost.this, "게시글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                    Toast.makeText(MainPost.this, "사용자 이름을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        //bottom navigation view
        fragmentManager = getSupportFragmentManager();
        fragmentHome = new MainMenuHomeFragment();
        fragmentProfile = new MainMenuProfileFragment();
        fragmentPost = new MainMenuPostFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_post);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tab_layout, fragmentPost).commitAllowingStateLoss();

        // 프로필 버튼을 선택 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_post);

        //bottom navigation view 페이지 이동
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

    //bottom navigation view ItemSelectedListener()
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
                    String fileName = "image" + postId + ".jpg"; // 파일 이름 생성
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
                                    // 게시물 정보 데이터베이스에 저장
                                    mDatabase.child(postId).setValue(post);
                                    // 게시물 추가 후 제목, 내용 텍스트 초기화
                                    edtTitle.setText("");
                                    edtContent.setText("");
                                    selectedImageUri = null; // selectedImageUri 초기화
                                    // 게시물 작성 성공 토스트 메세지
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
                    // 이미지가 선택되지 않았을 경우 이미지를 제외하고 나머지 업로드
                    Date currentTime = Calendar.getInstance().getTime();
                    long timeStamp = currentTime.getTime();
                    Post post = new Post(postId, uid, profileImgUrl, name, title, content, null, timeStamp);
                    // 게시물 정보 데이터베이스에 저장
                    mDatabase.child(postId).setValue(post);
                    // 게시물 추가 후 제목, 내용 텍스트 초기화
                    edtTitle.setText("");
                    edtContent.setText("");
                    selectedImageUri = null; // selectedImageUri 초기화
                    // 게시물 작성 성공 토스트 메세지
                    Toast.makeText(MainPost.this, "게시물이 작성되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainPost.this, "게시글을 작성해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 프로필 이미지 URL을 가져오는 메서드
    private void getUserProfileImageUrl(String uid, OnProfileImageUrlFetchedListener listener) {
        uDatabase.child("UserAccount").child(uid).child("profileImgUrl").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String profileImgUrl = task.getResult().getValue(String.class);
                listener.onProfileImageUrlFetched(profileImgUrl);
            } else {
                listener.onProfileImageUrlFetched(null);
            }
        });
    }

    interface OnProfileImageUrlFetchedListener {
        void onProfileImageUrlFetched(String profileImgUrl);
    }

    //버튼을 클릭하면 시작되는 함수
    @SuppressLint("ResourceType")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 이미지뷰에 이미지 불러오기
            if (bitmap != null) {
                if (imageView.getDrawable() == null) {
                    imageView.setImageBitmap(bitmap);
                    edtContent.setEnabled(true);
                } else if (imageView2.getDrawable() == null) {
                    imageView2.setImageBitmap(bitmap);
                } else if (imageView3.getDrawable() == null) {
                    imageView3.setImageBitmap(bitmap);
                } else if (imageView4.getDrawable() == null) {
                    imageView4.setImageBitmap(bitmap);
                } else if (imageView5.getDrawable() == null) {
                    imageView5.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(MainPost.this, "사진을 전부 선택하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
