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
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import android.widget.Toast;

public class userprofile extends AppCompatActivity {
    /* 홈 탭 버튼
    ImageButton btnHome;
    ImageButton btnUser;
    */
    // 데이터베이스
    private FirebaseAuth auth;

    //bottom navigation view
    private FragmentManager fragmentManager;
    private MainMenuHomeFragment fragmentHome;
    private MainMenuProfileFragment fragmentProfile;
    private MainMenuPostFragment fragmentPost;

    Button logoutBtn;
    Button deleteIdBtn;

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

        auth = FirebaseAuth.getInstance();

        // 로그아웃 메뉴 버튼
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        registerForContextMenu(logoutBtn);
        // 회원탈퇴 버튼
        //deleteIdBtn = (Button) findViewById(R.id.deleteIdBtn);

        // 그리드 뷰
        // 사진들을 보여줄 GridView 뷰의 어댑터 객체를 정의하고 그것을 이 뷰의 어댑터로 설정
        // => 뷰와 어댑터 연결
        GridView gridViewImages = (GridView) findViewById(R.id.gridView);
        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this, imageIDs);
        gridViewImages.setAdapter(imageGridAdapter);

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


}