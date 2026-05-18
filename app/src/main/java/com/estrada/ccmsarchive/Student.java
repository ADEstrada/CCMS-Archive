package com.estrada.ccmsarchive;

public class Student {
    private String studentId;
    private String firstName; // Tugma sa JSON key "firstName"
    private String lastName;  // Tugma sa JSON key "lastName"
    private String program;
    private String year;      // Tugma sa JSON key "year"

    // Getters - Ito ang nag-aalis ng red lines sa Seeder!
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