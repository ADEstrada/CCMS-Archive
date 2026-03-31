package com.estrada.ccmsarchive;

public class ProjectPreview {

    private String projectName;
    private String description;
    private String uploader;

    public ProjectPreview(String projectName, String description, String uploader) {
        this.projectName = projectName;
        this.description = description;
        this.uploader = uploader;
    }

    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getUploader() { return uploader; }
}

