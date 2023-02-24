package com.glaukous.views.scanner

import com.google.gson.annotations.SerializedName

class VerifyItem(

    @field:SerializedName("completedCount")
    val completedCount: Int? = null,

    @field:SerializedName("isVerified")
    val isVerified: Boolean? = null,

    @field:SerializedName("successMessage")
    val successMessage: String? = null
)
