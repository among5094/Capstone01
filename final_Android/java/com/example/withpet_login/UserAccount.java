package com.example.withpet_login;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    //변수들
    private String idToken; //사용자의 고유 식별자(UID)
    private String emailId; // 사용자의 이메일 주소
    private String password; // 사용자의 비밀번호
    private String name; // 사용자의 이름
    private String userId; // 사용자의 아이디
    private String nickname; // 사용자의 닉네임
    private String phoneNum; // 사용자의 휴대전화번호
    private String profileImgUrl; // 사용자의 프로필 사진 URL

    // 사용자 반려견 정보 리스트
    private List<Pet> pets;

    //생성자
    public UserAccount(){
        // 반려동물 정보를 저장하는 ArrayList 초기화
        pets = new ArrayList<>();
    }

    //객체 변수 값 반환
    public String getIdToken(){ return idToken; }
    public String getEmailId() { return emailId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getPhoneNum() { return phoneNum; }
    public String getProfileImgUrl() { return profileImgUrl; }



    //외부 변수 값 -> 객체 변수 값 저장
    public void setIdToken(String idToken){ this.idToken = idToken; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
    public void setProfileImgUrl(String profileImgUrl) { this.profileImgUrl = profileImgUrl; }


    // 반려동물 정보를 ArrayList에 추가하는 메서드
    public void addPet(Pet pet) {
        if (    this.pets == null) {
            this.pets = new ArrayList<>();
        }
        this.pets.add(pet);
    }

    // 반려동물 정보를 ArrayList에서 제거하는 메서드
    public void removePet(Pet pet) { pets.remove(pet); }

    // 사용자의 모든 반려동물 정보를 반환하는 메서드
    public List<Pet> getPets() { return pets; }

    // 반려동물 정보를 ArrayList로 설정하는 메서드
    public void setPets(List<Pet> pets) { this.pets = pets; }


}
