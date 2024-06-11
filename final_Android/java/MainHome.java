package com.example.withpet_login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class MainHome extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private MainMenuHomeFragment fragmentHome;
    private MainMenuProfileFragment fragmentProfile;
    private MainMenuPostFragment fragmentPost;

    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<String> postUidList = new ArrayList<>();
    private DatabaseReference databaseReference;

    private RecyclerView postRecyclerView;
    private MainPageFragmentRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // 스와이프 리프레시 레이아웃(새로고침)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        // 피드 레이아웃 (리사이클러뷰)
        postRecyclerView = findViewById(R.id.post_recyclerview);
        // 아이템 클릭 리스너 정의
        MainPageFragmentRecyclerAdapter.OnItemClickListener itemClickListener = new MainPageFragmentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post, int position) {
                // 아이템 클릭 시 동작 정의
                // 여기에 클릭 시 동작을 추가합니다.
            }

            @Override
            public void onItemClick(Post post, int position, String action) {
                // 삭제 버튼 클릭 시 동작 정의
                // 여기에 삭제 버튼 클릭 시 동작을 추가합니다.
            }
        };

        // MainPageFragmentRecyclerAdapter 객체 생성 및 클릭 리스너 전달
        adapter = new MainPageFragmentRecyclerAdapter(postList, itemClickListener);

        // 리사이클러뷰에 어댑터 설정
        postRecyclerView.setAdapter(adapter);

        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 새로고침 동작 설정
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        // Realtime Database에서 데이터 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear();
                    postUidList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post item = snapshot.getValue(Post.class);
                        postList.add(0,item); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                        postUidList.add(0, snapshot.getKey()); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
            }
        });

        fragmentManager = getSupportFragmentManager();
        fragmentHome = new MainMenuHomeFragment();
        fragmentProfile = new MainMenuProfileFragment();
        fragmentPost = new MainMenuPostFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigationview_home);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tab_layout, fragmentHome).commitAllowingStateLoss();

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.navigation_home) {
                    return true;
                } else if (item.getItemId() == R.id.navigation_post) {
                    intent = new Intent(getApplicationContext(), MainPost.class);
                    startActivity(intent);
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
        // 데이터 로드
        loadPostData();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 데이터 로드
        loadPostData();

        // Realtime Database에서 데이터 다시 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear();
                    postUidList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post item = snapshot.getValue(Post.class);
                        postList.add(0, item); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                        postUidList.add(0, snapshot.getKey()); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
                Log.e(TAG, "Error getting data from Realtime Database: " + databaseError.getMessage());
            }
        });
    }

    private void loadPostData() {
        // Realtime Database에서 데이터 다시 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Post> newPostList = new ArrayList<>();
                    ArrayList<String> newPostUidList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post item = snapshot.getValue(Post.class);
                        newPostList.add(0, item); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                        newPostUidList.add(0, snapshot.getKey()); //역순으로 가져옴(새로운것이 위에[0을 적음으로써])
                    }
                    updateRecyclerView(newPostList, newPostUidList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
                Log.e(TAG, "Error getting data from Realtime Database: " + databaseError.getMessage());
            }
        });
    }

    private void updateRecyclerView(ArrayList<Post> newPostList, ArrayList<String> newPostUidList) {
        // 이전 데이터 리스트와 새로운 데이터 리스트를 비교하여 업데이트가 필요한 경우에만 RecyclerView를 업데이트
        // 예) 이전 리스트와 새로운 리스트의 크기가 다를 때에만 업데이트
        if (postList.size() != newPostList.size()) {
            postList.clear();
            postList.addAll(newPostList);
            postUidList.clear();
            postUidList.addAll(newPostUidList);
            adapter.notifyDataSetChanged();
        }
    }

    // 새로고침 메소드
    private void refreshData() {
        // 데이터 새로고침 시작
        swipeRefreshLayout.setRefreshing(true);

        // Realtime Database에서 데이터 다시 가져오기
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Post> newPostList = new ArrayList<>();
                    ArrayList<String> newPostUidList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post item = snapshot.getValue(Post.class);
                        newPostList.add(0, item); // 역순으로 가져옴(새로운 것이 위에[0을 적음으로써])
                        newPostUidList.add(0, snapshot.getKey()); // 역순으로 가져옴(새로운 것이 위에[0을 적음으로써])
                    }
                    updateRecyclerView(newPostList, newPostUidList);
                }

                // 데이터 새로고침 완료
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
                Log.e(TAG, "Error getting data from Realtime Database: " + databaseError.getMessage());

                // 데이터 새로고침 실패 시도 중지
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    // 바텀네비게이션
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
}
