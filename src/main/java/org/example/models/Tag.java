package org.example.models;

public class Tag {
    private int id;
    private String keyword;

    // Constructors
    public Tag(int id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public Tag(String keyword) {
        this.keyword = keyword;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
