package com.exemple.socialapp.domain;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private List<Long> friends;
    private boolean admin = false;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = firstName;
        this.password = lastName;
    }

    public User(String firstName, String lastName, String username, String password, List<Long> friends, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.friends = friends;
        this.admin = admin;
    }

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.friends = Collections.emptyList();
        this.admin = false;
    }

    public User(String firstName, String lastName, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = firstName;
        this.password = lastName;
        this.admin = admin;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getId(){
        return super.getId();
    }

    public boolean isAdmin(){
        return admin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Long> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "FirstName='" + firstName + '\'' +
                ", LastName='" + lastName + '\'' +
                ", Username='" + username + '\'' +
                ", Password='" + password + '\'' +
                ", Admin=" + admin +
                ", ID=" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        if (!super.equals(o)) return false;
        return isAdmin() == that.isAdmin() && Objects.equals(getFirstName(), that.getFirstName()) && Objects.equals(getLastName(), that.getLastName()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFirstName(), getLastName(), getUsername(), getPassword(), isAdmin());
    }
}