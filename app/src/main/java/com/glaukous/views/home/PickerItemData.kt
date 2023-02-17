package com.glaukous.views.home

import com.glaukous.genericadapters.AbstractModel
import com.glaukous.utils.getUtcToLocalFormat
import com.google.gson.annotations.SerializedName

class PickerItemData(

    @field:SerializedName("Floor")
    val floor: String? = null,

    @field:SerializedName("CycleCountId")
    val cycleCountId: Int? = null,

    @field:SerializedName("DateOfCreation")
    val dateOfCreation: String? = null,

    @field:SerializedName("StatusDisPlay")
    val statusDisPlay: String? = null,

    @field:SerializedName("Items")
    val items: List<Items>? = null,

    @field:SerializedName("CycleCountStatus")
    val cycleCountStatus: Int? = null
){
    val getDate=dateOfCreation?.getUtcToLocalFormat()
}

class Items(
    @field:SerializedName("ItemCode")
    val itemCode: String? = null,

    @field:SerializedName("ItemDetailsId")
    val itemDetailsId: Int? = null,

    @field:SerializedName("ItemBarCode")
    val itemBarCode: String? = null,

    @field:SerializedName("QuantityCount")
    val quantityCount: Int? = null
):AbstractModel()
