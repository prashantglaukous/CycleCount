package com.glaukous.views.home

import android.content.Context
import android.view.KeyEvent
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.genericadapters.RecyclerAdapter
import com.glaukous.interfaces.Barcode
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {

    val isAccepted = ObservableField(false)
    val comingFrom = ObservableField(false)
    val itemRecyclerView = RecyclerAdapter<ItemCard>(R.layout.item_card)
    init {
        itemRecyclerView.addItems(
            listOf(
                ItemCard(
                    date = "06/02/2023",
                    floor = "AX062",
                    barcode = "Item12385",
                    quantity = "15"
                ),ItemCard(
                    date = "06/02/2023",
                    floor = "AX062",
                    barcode = "Item12385",
                    quantity = "15"
                ),
                ItemCard(
                    date = "06/02/2023",
                    floor = "AX062",
                    barcode = "Item12385",
                    quantity = "15"
                ),
            )
        )
    }
    fun onClick(view: View) {
        when (view.id) {
            R.id.btnAccept -> isAccepted.set(!isAccepted.get()!!)
            R.id.floatingButton -> view.findNavController()
                .navigate(HomeDirections.actionHomeToScanner(null))
        }
    }
}