package com.estrada.ccmsarchive;

public class PostPreview {

    private String projectName;
    private String status;

    public PostPreview(String projectName, String status) {
        this.projectName = projectName;
        this.status = status;
    }
    public String getProjectName() { return projectName; }
    public String getStatus() { return status; }
}

