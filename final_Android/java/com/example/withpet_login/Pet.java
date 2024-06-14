package com.example.withpet_login;

public class Pet {
    private String name; // 반려견 이름
    private String species; // 반려견 종
    private String birth; // 반려견 생년월일
    private String etc; // 반려견 기타사항
    private String gender; // 반려견 성별

    // 생성자
    public Pet() {

    }

    // Get
    public String getName() { return name; }

    public String getSpecies() { return species; }
    public String getBirth() { return birth; }
    public String getEtc() { return etc; }
    public String getGender() { return gender; }

    // Set
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }

    public void setBirth(String birth) { this.birth = birth; }
    public void setEtc(String etc) { this.etc = etc; }
    public void setGender(String gender) { this.gender = gender; }
}