package com.glaukous.views.home

import com.glaukous.genericadapters.AbstractModel

data class ItemCard(
    val date: String,
    val floor: String,
    val barcode: String,
    val quantity: String,
) : AbstractModel()
