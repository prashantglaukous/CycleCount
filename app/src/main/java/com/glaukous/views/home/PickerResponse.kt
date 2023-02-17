package com.glaukous.views.home

import com.google.gson.annotations.SerializedName

 class PickerResponse(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("key")
	val key: Int? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)