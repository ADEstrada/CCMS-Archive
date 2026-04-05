package com.estrada.ccmsarchive;

public class ProjectPreview {

    private String projectName;
    private String description;
    private String uploader;

    // Jasmine - added course
    private String course;

    public ProjectPreview(String projectName, String description, String uploader) {
        this.projectName = projectName;
        this.description = description;
        this.uploader = uploader;
        this.course = "";
    }

    public ProjectPreview(String projectName, String description, String course, String uploader) {
        this.projectName = projectName;
        this.description = description;
        this.course = course;
        this.uploader = uploader;
    }

    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getUploader() { return uploader; }
    public String getCourse() { return course; }

}

