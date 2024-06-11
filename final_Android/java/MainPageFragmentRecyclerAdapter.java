package com.example.withpet_login;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPageFragmentRecyclerAdapter extends RecyclerView.Adapter<MainPageFragmentRecyclerAdapter.ViewHolder> {

    private ArrayList<Post> postList; // 포스트 목록을 저장하는 ArrayList
    private OnItemClickListener listener; // 아이템 클릭 리스너

    // 생성자
    public MainPageFragmentRecyclerAdapter(ArrayList<Post> postList, OnItemClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    // ViewHolder 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImgUrlCircleImageView; // 프로필 사진
        public TextView userNameTextView; // 유저 이름
        public TextView titleTextView; // 게시물 제목
        public ImageView postImageView; // 게시물 이미지
        public TextView contentTextView; // 게시물 내용
        public TextView timestampTextView; // 업로드 시간 표시
        public Button delPostBtn; // 삭제 버튼

        public ViewHolder(View itemView) {
            super(itemView);
            profileImgUrlCircleImageView = itemView.findViewById(R.id.post_profile_image); // 프로필 사진
            userNameTextView = itemView.findViewById(R.id.post_user_name); // 유저 이름
            titleTextView = itemView.findViewById(R.id.post_title); // 게시물 제목
            postImageView = itemView.findViewById(R.id.post_image); // 게시물 이미지
            contentTextView = itemView.findViewById(R.id.post_content); // 게시물 내용
            timestampTextView = itemView.findViewById(R.id.post_timestamp); // 업로드 시간 표시
            delPostBtn = itemView.findViewById(R.id.delPostBtn); // 삭제 버튼
        }

        public void bind(final Post post, final OnItemClickListener listener) {
            // Realtime Database의 profileImgUrl을 사용하여 이미지 로드
            Glide.with(itemView.getContext()).load(post.getProfileImgUrl()).into(profileImgUrlCircleImageView);

            // 뷰에 데이터 설정
            userNameTextView.setText(post.getName()); // 유저 이름 설정
            titleTextView.setText(post.getTitle()); // 게시물 제목 설정
            contentTextView.setText(post.getContent()); // 게시물 내용 설정

            // Realtime Database의 imageUrl을 사용하여 이미지 로드 및 표시
            Glide.with(itemView.getContext()).load(post.getImageUrl()).into(postImageView);

            // 클릭 리스너 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭된 아이템과 위치를 리스너에 전달
                    listener.onItemClick(post, getAdapterPosition());
                }
            });

            // 삭제 버튼에 클릭 리스너 설정
            delPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 삭제 버튼이 클릭되었을 때 리스너에 전달
                    listener.onItemClick(post, getAdapterPosition(), "delete");

                    // Firebase Realtime Database에서 게시물 삭제
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("posts").child(post.getPostId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 게시물 데이터 삭제 성공
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 게시물 데이터 삭제 실패
                        }
                    });

                    // storage에서 사진 삭제
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(post.getIdToken()).child("posts").child(post.getPostId());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 이미지 삭제 성공
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 이미지 삭제 실패
                        }
                    });
                }
            });
        }




    }

    // 아이템 클릭 리스너를 위한 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Post post, int position);
        void onItemClick(Post post, int position, String action);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 데이터 바인딩
        Post post = postList.get(position); // 수정된 부분
        holder.userNameTextView.setText(post.getName());
        holder.titleTextView.setText(post.getTitle());
        holder.contentTextView.setText(post.getContent());
        Glide.with(holder.itemView.getContext()).load(post.getImageUrl()).into(holder.postImageView);
        Glide.with(holder.itemView.getContext()).load(post.getProfileImgUrl()).into(holder.profileImgUrlCircleImageView);
        // 상대적인 시간 표시를 위해 현재 시간과 게시된 시간 비교
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - post.getTimestamp();
        long elapsedTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis);

        // 업로드 시간 텍스트 뷰에 시간 설정
        String timestampText;
        if (elapsedTimeMinutes < 1) {
            timestampText = "방금 전";
        } else if (elapsedTimeMinutes < 60) {
            timestampText = elapsedTimeMinutes + "분 전";
        } else if (elapsedTimeMinutes < 60 * 24) {
            timestampText = TimeUnit.MINUTES.toHours(elapsedTimeMinutes) + "시간 전";
        } else if (elapsedTimeMinutes < 60 * 24 * 7) {
            timestampText = TimeUnit.MINUTES.toDays(elapsedTimeMinutes) + "일 전";
        } else {
            // 7일 이상 전에 게시된 경우에는 날짜 및 시간을 표시
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            timestampText = sdf.format(new Date(post.getTimestamp()));
        }
        holder.timestampTextView.setHint(timestampText);

        // 현재 사용자의 UID 가져오기
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // 게시물의 작성자 ID 토큰 가져오기
        String postAuthorToken = post.getIdToken();

        // 현재 사용자가 게시물 작성자인 경우에만 삭제 버튼을 보이도록 설정
        if (currentUserUid.equals(postAuthorToken)) {
            holder.delPostBtn.setVisibility(View.VISIBLE);
        } else {
            holder.delPostBtn.setVisibility(View.GONE);
        }

        // 삭제 버튼에 클릭 리스너 설정
        holder.delPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 버튼이 클릭되었을 때 리스너에 전달
                listener.onItemClick(post, position, "delete");

                // Firebase Realtime Database에서 게시물 삭제
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("posts").child(post.getPostId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 게시물 데이터 삭제 성공
                        // 게시물 폴더 삭제
                        deletePostFolder(post.getPostId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 게시물 데이터 삭제 실패
                        // 실패해도 게시물 폴더 삭제를 시도
                        deletePostFolder(post.getPostId());
                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {
        // 포스트 목록의 크기 반환
        return postList.size();
    }


    // postId 이름으로 된 폴더만 삭제하는 메소드
    private void deletePostFolder(String postId) {
        // postId를 사용하여 해당 폴더의 StorageReference 가져오기
        StorageReference folderRef = FirebaseStorage.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("posts")
                .child(postId);

        // 해당 폴더의 모든 파일 및 하위 폴더를 삭제하는 코드
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
                    // postId 이름으로 된 폴더만 재귀적으로 삭제
                    if (folder.getPath().contains(postId)) {
                        deleteStorageFolder(folder);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error listing files: ", exception);
            }
        });
    }

    // Storage 폴더 삭제 메소드
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Error listing files: ", exception);
                    }
                });
    }




}
