package com.clementsnelson.smoothriders;

// Constructor for User objects
public class User {

    // Class variables
    public String fullName, age, email;

    // Default constructor
    public User() {

    }

    // Constructor using parameters
    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

    // Getters for class variables
    public String getFullName(){
        return fullName;
    }
    public String getAge(){
        return age;
    }
    public String getEmail(){
        return email;
    }

} // end of User class
