package com.glaukous.views.input

import com.google.gson.annotations.SerializedName

 class Submit(

	@field:SerializedName("cycleCountId")
	val cycleCountId: Int? = null,

	@field:SerializedName("itemDetails")
	val itemDetails: List<ItemDetailsItem?>? = null
)

 class ItemDetailsItem(

	@field:SerializedName("itemQuantity")
	val itemQuantity: Int? = null,

	@field:SerializedName("itemBarcode")
	val itemBarcode: String? = null,

	@field:SerializedName("floor")
	val floor: String? = null
)
