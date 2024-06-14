package com.example.withpet_login;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
//그리드뷰 사용

import android.widget.GridView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class userprofile extends AppCompatActivity {
    /* 홈 탭 버튼
    ImageButton btnHome;
    ImageButton btnUser;
    */
    // 데이터베이스
    private FirebaseAuth auth;
    // FirebaseAuth 객체 초기화
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //bottom navigation view
    private FragmentManager fragmentManager;
    private MainMenuHomeFragment fragmentHome;
    private MainMenuProfileFragment fragmentProfile;
    private MainMenuPostFragment fragmentPost;

    Button logoutBtn;
    Button deleteIdBtn;

    // 프로필 사진 변경
    Button account_profile_button;
    private final int PICK_IMAGE_REQUEST = 10;

    // 유저아이디 표시
    TextView profile_userid;

    // 회원정보변경 임시버튼
    Button changeUserInfoBtn;

    // 그리드뷰
    // imageIDs 배열은 GridView 뷰를 구성하는 이미지 파일들의 리소스 ID들을 담는다.
    private int[] imageIDs = new int[]{
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo,
            R.drawable.pet_logo
    };
    

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        // 프로필 사진 표시
        ImageView profileImageView = findViewById(R.id.profile_image);
        String uid = user.getUid(); // 실제 UID로 대체
        setUserProfileImage(uid, profileImageView);

        // 프로필 사진 밑 유저 이름 텍스트
        profile_userid = findViewById(R.id.profile_userid);
        setProfileUserId(uid, profile_userid);

        // 그리드뷰
        final GridView gridViewImages = findViewById(R.id.gridView);
        loadImages(gridViewImages);

        changeUserInfoBtn = findViewById(R.id.changeUserInfoBtn); // 회원정보변경 임시 버튼
        changeUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeUserInfo.class);
                startActivity(intent);
            }
        });

        // 프로필 사진 변경
        account_profile_button = findViewById(R.id.account_profile_button);
        account_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAlbum();
            }
        });

        auth = FirebaseAuth.getInstance();

        // 로그아웃 메뉴 버튼
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        registerForContextMenu(logoutBtn);
        // 회원탈퇴 버튼
        //deleteIdBtn = (Button) findViewById(R.id.deleteIdBtn);

        //bottom navigation view
        fragmentManager = getSupportFragmentManager();
        fragmentHome = new MainMenuHomeFragment();
        fragmentProfile = new MainMenuProfileFragment();
        fragmentPost = new MainMenuPostFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_userprofile);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());


        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tab_layout, fragmentProfile).commitAllowingStateLoss();

        // 프로필 버튼을 선택 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


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
                    intent = new Intent(getApplicationContext(), MainPost.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
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

    // 게시글 메뉴 버튼

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mInflater = getMenuInflater();
        if (v == logoutBtn) {
            mInflater.inflate(R.menu.logout_menu, menu);
        }
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    

    // Override onContextItemSelected to handle context menu item clicks
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // 로그아웃 메뉴를 눌렀을 때 실행될 코드
            // 로그아웃 메뉴 선택 대화상자
            AlertDialog.Builder dlg = new AlertDialog.Builder(userprofile.this);
            dlg.setMessage("로그아웃 하시겠습니까?");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    auth.signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(userprofile.this, "로그아웃이 성공적으로 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }); // 로그아웃 기능을 넣어 수정해야 함
            dlg.setNegativeButton("취소", null);
            dlg.show();
            return true;
        } else if(item.getItemId() == R.id.deleteId) {
            // 회원탈퇴 메뉴를 눌렀을 때 실행될 코드
            // 회원탈퇴 메뉴 선택 대화상자
            AlertDialog.Builder dlg = new AlertDialog.Builder(userprofile.this);
            dlg.setMessage("탈퇴 하시겠습니까? 계정을 완전히 삭제합니다.");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // 회원탈퇴 기능 추가 필요(옮겨야함)
                    // 데이터 삭제
                    // 사용자의 게시글 삭제gg
                    databaseReference.child("posts").orderByChild("idToken").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                // 각 게시글의 데이터에서 userId 가져오기
                                String userIdToDelete = postSnapshot.child("idToken").getValue(String.class);
                                // 회원탈퇴 하려는 사용자의 게시글인 경우 해당 게시글 삭제
                                if (userIdToDelete.equals(userId)) {
                                    // 게시글의 하위 데이터 삭제
                                    postSnapshot.getRef().removeValue();
                                }
                            }
                            // 사용자 정보 삭제
                            databaseReference.child("UserAccount").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // 스토리지의 사용자 폴더 삭제
                                        deleteStorageFolder(storageReference.child("users").child(userId));
                                        // 회원탈퇴가 성공적으로 이루어졌음을 사용자에게 알림
                                        Toast.makeText(userprofile.this, "회원탈퇴가 성공적으로 되었습니다.", Toast.LENGTH_SHORT).show();
                                        // 메인 화면으로 이동
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "Failed to delete user data");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error deleting user's posts: ", databaseError.toException());
                        }
                    });
                }
            }); // 탈퇴 기능을 넣어 수정해야 함
            dlg.setNegativeButton("취소", null);
            dlg.show();
            return true;
        }
        return false;
    }

    //그리드 뷰
    //이미지 그리드 어댑터 클래스
    public class ImageGridAdapter extends BaseAdapter {

        Context context = null;

        // imageIDs는 이미지 파일들의 리소스 ID들을 담는 배열입니다.
        // 이 배열의 원소들은 자식 뷰들인 ImageView 뷰들이 무엇을 보여주는지를
        // 결정하는데 활용될 것입니다.

        int[] imageIDs = null;
        //생성자
        public ImageGridAdapter(Context context, int[] imageIDs) {
            this.context = context;
            this.imageIDs = imageIDs;
        }
        //데이터 개수 반환
        public int getCount() {
            return (null != imageIDs) ? imageIDs.length : 0;
        }
        //지정된 위치의 아이템 반환
        public Object getItem(int position) {
            return (null != imageIDs) ? imageIDs[position] : 0;
        }
        //지정된 위치의 아이템 ID 반환
        public long getItemId(int position) {
            return position;
        }
        //뷰 반환
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;

            if (null != convertView)
                imageView = (ImageView) convertView;
            else {
                //비트맵 리사이징
                // GridView 뷰를 구성할 ImageView 뷰의 비트맵을 정의, 크기는 크기는 320*240
                Bitmap bmp
                        = BitmapFactory.decodeResource(context.getResources(), imageIDs[position]);
                bmp = Bitmap.createScaledBitmap(bmp, 320, 240, false);

                // GridView 뷰를 구성할 ImageView 뷰 생성
                imageView = new ImageView(context);
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(bmp);

                // 이미지 클릭 리스너 설정
                ImageClickListener imageViewClickListener = new ImageClickListener(context, imageIDs[position]);
                imageView.setOnClickListener(imageViewClickListener);
            }

            return imageView;
        }
    }
    // 이미지 클릭 리스너 클래스
    class ImageClickListener implements View.OnClickListener {
        Context context;
        int imageID;
        // 생성자
        public ImageClickListener(Context context, int imageID) {
            this.context = context;
            this.imageID = imageID;
        }
        // 이미지 클릭 시 실행되는 메서드
        @Override
        public void onClick(View v) {
            // 이미지 확대 액티비티 실행
            Intent intent = new Intent(context, userprofile_image.class);
            intent.putExtra("image ID", imageID);
            context.startActivity(intent);
        }
    }

    // 스토리지 폴더 삭제 메소드
    private void deleteStorageFolder(StorageReference folderRef) {
        folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference fileRef : listResult.getItems()) {
                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "File deleted successfully: " + fileRef.getPath());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e(TAG, "Error deleting file: " + fileRef.getPath(), exception);
                                }
                            });
                        }

                        for (StorageReference folder : listResult.getPrefixes()) {
                            deleteStorageFolder(folder);  // 재귀적으로 폴더 내의 모든 파일 삭제
                        }

                        // 모든 파일 및 하위 폴더 삭제 후 사용자 계정 삭제
                        deleteFirebaseUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Error listing files: ", exception);
                    }
                });
    }

    // 사용자 계정 삭제 메소드
    private void deleteFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                            } else {
                                Log.e(TAG, "User account deletion failed.");
                            }
                        }
                    });
        }
    }

    // 사진 선택 메소드
    private void loadAlbum() {
        Intent intentPhoto = new Intent(Intent.ACTION_PICK);
        intentPhoto.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intentPhoto, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData(); // 선택한 사진의 Uri 저장
            checkAndUploadImageToFirebase(selectedImageUri);
        }
    }

    private void checkAndUploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserAccount").child(uid);

                // profileImgUrl이 존재하는지 확인
                userRef.child("profileImgUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // profileImgUrl이 존재하는 경우
                            uploadImageToFirebase(imageUri, uid, true);
                            Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            // profileImgUrl이 존재하지 않는 경우
                            uploadImageToFirebase(imageUri, uid, false);
                            Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Database Error", "onCancelled: ", error.toException());
                        Toast.makeText(getApplicationContext(), "프로필 사진 변경 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String uid, boolean isUpdate) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users/" + uid + "/profileImage/" + "profileImage.jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            if (isUpdate) { // profileImgUrl이 존재할 때 업데이트
                updateProfileImageUrl(uid, imageUrl);
                Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다", Toast.LENGTH_SHORT).show();
            } else { // profileImgUrl이 존재하지 않을 때 생성
                saveProfileImageUrlToDatabase(uid, imageUrl);
                Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다", Toast.LENGTH_SHORT).show();
            }
        })).addOnFailureListener(e -> { // Handle unsuccessful uploads
            Log.e("Upload Error", "onFailure: ", e);
            Toast.makeText(getApplicationContext(), "프로필 사진 변경 실패", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProfileImageUrlToDatabase(String uid, String imageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(uid);
        databaseReference.child("profileImgUrl").setValue(imageUrl).addOnSuccessListener(aVoid -> {// URL 저장 성공
            Log.d("Database", "Image URL saved successfully");
        }).addOnFailureListener(e -> { // URL 저장 실패
            Log.e("Database Error", "onFailure: ", e);
        });
    }

    private void updateProfileImageUrl(String uid, String imageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(uid);
        databaseReference.child("profileImgUrl").setValue(imageUrl).addOnSuccessListener(aVoid -> { // URL 업데이트 성공
            Log.d("Database", "Image URL updated successfully");
        }).addOnFailureListener(e -> { // URL 업데이트 실패
            Log.e("Database Error", "onFailure: ", e);
        });
    }

    private void loadImages(final GridView gridView) {
        final ArrayList<String> imageUrls = new ArrayList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userPostsRef = databaseReference.child("posts");

            userPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // 여기서 각 게시물의 데이터 구조를 확인하고 적절히 코드를 수정하세요
                        String imageUrl = postSnapshot.child("imageUrl").getValue(String.class);
                        if (imageUrl != null) {
                            imageUrls.add(imageUrl);
                        }
                    }
                    com.example.withpet_login.ImageGridAdapter imageGridAdapter = new com.example.withpet_login.ImageGridAdapter(userprofile.this, imageUrls);
                    gridView.setAdapter(imageGridAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터 읽기 실패 시 처리
                }
            });
        }
    }

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference();

    // 프로필 이미지 밑 유저아이디 텍스트 표시
    // 사용자 이름 가져오기 메소드
    private void getProfileUserId(String uid, OnProfileUserIdFetchedListener listener) {
        uDatabase.child("UserAccount").child(uid).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().getValue(String.class);
                    listener.onProfileUserIdFetched(name);
                } else {
                    listener.onProfileUserIdFetched(null);
                }
            }
        });
    }

    // 사용자 ID를 설정하는 메소드
    private void setProfileUserId(String uid, TextView textView) {
        getProfileUserId(uid, new OnProfileUserIdFetchedListener() {
            @Override
            public void onProfileUserIdFetched(String name) {
                if (name != null) {
                    // TextView에 사용자 ID를 설정
                    profile_userid.setText(name);
                } else {
                    // 사용자 ID를 가져오는 데 실패한 경우 기본 메시지를 설정
                    profile_userid.setText("user name");
                    Toast.makeText(userprofile.this, "사용자 ID를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 프로필 사용자 ID를 가져오는 리스너 인터페이스
    interface OnProfileUserIdFetchedListener {
        void onProfileUserIdFetched(String name);
    }

    // 프로필 사진 표시
    // 사용자 프로필 이미지 URL 가져오기 메소드
    private void getUserProfileImageUrl(String uid, OnProfileImageUrlFetchedListener listener) {
        uDatabase.child("UserAccount").child(uid).child("profileImgUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String profileImgUrl = task.getResult().getValue(String.class);
                    listener.onProfileImageUrlFetched(profileImgUrl);
                } else {
                    listener.onProfileImageUrlFetched(null);
                }
            }
        });
    }

    // 프로필 이미지 URL을 ImageView에 설정하는 메소드
    private void setUserProfileImage(String uid, ImageView imageView) {
        getUserProfileImageUrl(uid, new OnProfileImageUrlFetchedListener() {
            @Override
            public void onProfileImageUrlFetched(String profileImgUrl) {
                if (profileImgUrl != null) {
                    // 이미지뷰에 프로필 이미지를 로드
                    Glide.with(userprofile.this).load(profileImgUrl)
                            .placeholder(R.drawable.pet_logo) // 이미지 로드 중 표시할 플레이스홀더 이미지
                            .error(R.drawable.main_usertab) // 이미지 로드 실패 시 표시할 이미지
                            .into(imageView);
                } else {
                    // URL을 가져오는 데 실패한 경우 기본 이미지를 설정
                    imageView.setImageResource(R.drawable.main_usertab);
                    Toast.makeText(userprofile.this, "프로필 이미지를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // OnProfileImageUrlFetchedListener 인터페이스 정의
    public interface OnProfileImageUrlFetchedListener {
        void onProfileImageUrlFetched(String profileImgUrl);
    }

}