package com.estrada.ccmsarchive;

public class PostPreview {

    private String projectName;
    private String status;
    private String projectId;

    public PostPreview(String projectId, String projectName, String status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.status = status;
    }

    public String getProjectId() { return projectId; }
    public String getProjectName() { return projectName; }
    public String getStatus() { return status; }
}
