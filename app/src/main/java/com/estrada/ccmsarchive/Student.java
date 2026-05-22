package com.estrada.ccmsarchive;

public class Student {
    private String studentId;
    private String firstName; 
    private String lastName;  
    private String program;
    private String year;     

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStudentId() { return studentId; }
    public String getProgram() { return program; }
    public String getYear() { return year; }

    public void setStudentId(String id) { this.studentId = id; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
