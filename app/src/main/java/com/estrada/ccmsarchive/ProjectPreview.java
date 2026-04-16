package com.estrada.ccmsarchive;

import java.util.List;

public class ProjectPreview {

    private String projectName;
    private String description;
    private String uploader;
    private String program;
    private List<String> imageData;

    // NEW FIELDS
    private String status;
    private String course;
    private String techUsed;
    private String contributors;

    // Updated Constructor
    public ProjectPreview(String title, String description, String uploader, String program,
                          List<String> imageData, String status, String course,
                          String techUsed, String contributors) {
        this.projectName = title;
        this.description = description;
        this.uploader = uploader;
        this.program = program;
        this.imageData = imageData;
        this.status = status;
        this.course = course;
        this.techUsed = techUsed;
        this.contributors = contributors;
    }

    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getUploader() { return uploader; }
    public String getProgram() { return program; }
    public List<String> getImageData() { return imageData; }


    public String getStatus() { return status; }
    public String getCourse() { return course; }
    public String getTechUsed() { return techUsed; }
    public String getContributors() { return contributors; }
}