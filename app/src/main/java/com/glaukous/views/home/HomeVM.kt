package com.glaukous.views.home

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.genericadapters.RecyclerAdapter
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import com.glaukous.pref.token
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

const val TAG = "HomeVM"

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

        Log.e(TAG, "$TAG: ${preferencesUtils.retrieveKey(token)}")
        itemRecyclerView.addItems(
            listOf(
                ItemCard(
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
            R.id.btnAccept -> {
                getCycleCountByPicker()
            }
            R.id.floatingButton -> view.findNavController()
                .navigate(HomeDirections.actionHomeToScanner(null))
        }
    }

    private fun getCycleCountByPicker() = viewModelScope.launch {
        repository.makeCall(loader = true, requestProcessor = object :
            ApiProcessor<Response<JsonElement>> {
            override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                return retrofitApi.getCycleCountByPicker(preferencesUtils.retrieveKey(token) ?: "")
            }

            override fun onResponse(res: Response<JsonElement>) {
                Log.e(TAG, "onResponse: $res")
            }

            override fun onError(message: String, responseCode: Int) {
                super.onError(message, responseCode)
                Log.e(TAG, "onResponse: $message")
            }
        })
    }

}