package com.glaukous.views.login

import com.google.gson.annotations.SerializedName

data class LoginData(

    @field:SerializedName("RefreshToken")
    val refreshToken: String? = null,

    @field:SerializedName("Email")
    val email: String? = null,

    @field:SerializedName("UserID")
    val userID: Int? = null,

    @field:SerializedName("PickerID")
    val pickerID: Int? = null,

    @field:SerializedName("ProjectID")
    val projectID: String? = null,

    @field:SerializedName("Token")
    val token: String? = null,

    @field:SerializedName("Name")
    val name: String? = null,

    @field:SerializedName("Password")
    val password: String? = null
)
