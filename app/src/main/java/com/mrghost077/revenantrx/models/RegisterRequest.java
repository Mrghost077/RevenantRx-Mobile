package com.mrghost077.revenantrx.models;

import com.google.gson.annotations.SerializedName;

import kotlin.jvm.internal.SerializedIr;

public class RegisterRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("phoneNum")
    private String phoneNum;

    @SerializedName("role")
    private String role;

    public RegisterRequest(String name, String email, String password, String phoneNum, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.role = role;
    }
}
