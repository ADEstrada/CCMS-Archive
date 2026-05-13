package com.estrada.ccmsarchive;

public class Course {
    private String courseCode; // Ito yung Key (e.g., IT100)
    private String courseName;

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String code) { this.courseCode = code; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String name) { this.courseName = name; }
}