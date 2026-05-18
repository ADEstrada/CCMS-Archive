package com.estrada.ccmsarchive;

public class Instructor {
    private String email;
    private String firstName;
    private String lastName;
    private String year; // Ito ang academic rank sa JSON mo

    // Getters
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRank() { return year; }

    // Setters (Dito mawawala ang error sa JsonHelper)
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setYear(String year) { this.year = year; }
}