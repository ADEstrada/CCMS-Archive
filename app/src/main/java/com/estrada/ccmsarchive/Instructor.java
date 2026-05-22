package com.estrada.ccmsarchive;

public class Instructor {
    private String email;
    private String firstName;
    private String lastName;
    private String year;

    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRank() { return year; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setYear(String year) { this.year = year; }
}
