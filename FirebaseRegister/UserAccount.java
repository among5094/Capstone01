package com.example.firebaseregister;

public class UserAccount {

    //변수들
    private String idToken; //사용자의 고유 식별자(UID)저장
    private String emailId; // 사용자의 이메일 주소를 저장
    private String password; //사용자의 비밀번호를 저장


    //생성자
    public UserAccount(){

    }

    public  String getIdToken(){ return idToken; } //idToken 필드의 값을 반환
    public String getEmailId() { return emailId; } //emailId 필드의 값을 반환
    public String getPassword() { return password; } //password 필드의 값을 반환


    //외부에서 전달받은 idToken 값을 객체idToken 필드에 저장
    public void setIdToken(String idToken){ this.idToken=idToken; }

    //외부에서 전달받은 idToken 값을 객체idToken 필드에 저장
    public void setEmailId(String emailId) { this.emailId=emailId; }

    //외부에서 전달받은 idToken 값을 객체idToken 필드에 저장
    public void setPassword(String password) { this.password=password; }



}