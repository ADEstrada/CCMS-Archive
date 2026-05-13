package com.estrada.ccmsarchive;

public class Student {
    private String studentId; // Ito ay manggagaling sa Key
    private String firstName;
    private String lastName;
    private String program;
    private String year;

    // Setters para sa ID
    public void setStudentId(String id) { this.studentId = id; }

    // Getters
    public String getStudentId() { return studentId; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getProgram() { return program; }
    public String getYear() { return year; }
}