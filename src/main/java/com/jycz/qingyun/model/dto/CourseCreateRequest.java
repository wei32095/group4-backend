package com.jycz.qingyun.model.dto;

public class CourseCreateRequest {
    private String name;
    private String description;
    private String coverImage;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}