package com.estrada.ccmsarchive;

import java.util.List;

public class ProjectPreview {

    private String projectName;
    private String description;
    private String uploader;
    private String program;
    private List<String> imageData;


    public ProjectPreview(String title, String description, String uploader, String program, List<String> imageData) {
        this.projectName = title;
        this.description = description;
        this.uploader = uploader;
        this.program = program;
        this.imageData = imageData;
    }

    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getUploader() { return uploader; }
    public String getProgram() { return program; }
    public List<String> getImageData() {
        return imageData;
    }

}

