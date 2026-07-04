package com.dino.learn.dto;

public class UserVO {

    private Long id;
    private String name;
    private String email;
    private int stars;
    private int difficulty;

    public UserVO() {
    }

    public UserVO(Long id, String name, String email, int stars, int difficulty) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.stars = stars;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
