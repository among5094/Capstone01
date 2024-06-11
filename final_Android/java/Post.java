package com.example.withpet_login;

public class Post {
    private String postId;
    private String idToken;
    private String profileImgUrl;
    private String name;
    private String title;
    private String content;
    private String imageUrl;
    private long timestamp; // 업로드 시간

    public Post() {
    }

    public Post(String postId, String idToken, String profileImgUrl,String name, String title, String content, String imageUrl, long timeStamp) {
        this.postId = postId;
        this.idToken = idToken;
        this.profileImgUrl = profileImgUrl;
        this.name = name;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timeStamp;
    }

    // get
    public String getPostId() { return postId; }
    public String getIdToken() { return idToken;}
    public String getProfileImgUrl() { return profileImgUrl; }
    public String getName() { return name; }
    public String getTitle() { return title; }

    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }

    // set
    public void setPostId(String postId) { this.postId = postId; }
    public void setTitle(String title) { this.title = title; }

    public void setContent(String content) { this.content = content; }
    public void setTimestamp() { this.timestamp = timestamp; }

}
