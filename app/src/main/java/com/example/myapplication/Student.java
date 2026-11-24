package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Student {

    private String firstName;
    private String lastName;
    private String email;
    private String studentClass;
    private String major;
    private boolean notifications;
    private boolean termsAccepted;

    public Student(String firstName, String lastName, String email,
                   String studentClass, String major,
                   boolean notifications, boolean termsAccepted) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentClass = studentClass;
        this.major = major;
        this.notifications = notifications;
        this.termsAccepted = termsAccepted;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getMajor() {
        return major;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void save(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences("student", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putString("studentClass", studentClass);
        editor.putString("major", major);
        editor.putBoolean("notifications", notifications);
        editor.putBoolean("termsAccepted", termsAccepted);

        editor.apply();
    }

    public static Student load(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences("student", Context.MODE_PRIVATE);

        if (!prefs.contains("firstName")) {
            return null;
        }

        return new Student(
                prefs.getString("firstName", ""),
                prefs.getString("lastName", ""),
                prefs.getString("email", ""),
                prefs.getString("studentClass", ""),
                prefs.getString("major", ""),
                prefs.getBoolean("notifications", false),
                prefs.getBoolean("termsAccepted", false)
        );
    }

    public void putInIntent(Intent intent) {

        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("email", email);
        intent.putExtra("studentClass", studentClass);
        intent.putExtra("major", major);
        intent.putExtra("notifications", notifications);
        intent.putExtra("termsAccepted", termsAccepted);
    }

    public static Student fromIntent(Intent intent) {

        if (intent == null || intent.getExtras() == null) {
            return null;
        }

        if (!intent.hasExtra("firstName") ||
                !intent.hasExtra("lastName") ||
                !intent.hasExtra("email") ||
                !intent.hasExtra("studentClass") ||
                !intent.hasExtra("major") ||
                !intent.hasExtra("notifications") ||
                !intent.hasExtra("termsAccepted")) {
            return null;
        }

        return new Student(
                intent.getStringExtra("firstName"),
                intent.getStringExtra("lastName"),
                intent.getStringExtra("email"),
                intent.getStringExtra("studentClass"),
                intent.getStringExtra("major"),
                intent.getBooleanExtra("notifications", false),
                intent.getBooleanExtra("termsAccepted", false)
        );
    }
}

