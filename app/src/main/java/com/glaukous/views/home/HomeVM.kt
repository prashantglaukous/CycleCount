package com.glaukous.views.home

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.extensions.jsonElementToData
import com.glaukous.extensions.jsonStringToData
import com.glaukous.extensions.showToast
import com.glaukous.genericadapters.RecyclerAdapter
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import com.glaukous.utils.getUtcToLocalFormat
import com.glaukous.views.scanner.VerifyItem
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {

    val isDataLoaded = ObservableField(false)
    val date = ObservableField("")
    val floor = ObservableField("")
    val cycleCountId = ObservableField(0)
    val itemRecyclerView = RecyclerAdapter<Items>(R.layout.item_card)


    fun onClick(view: View) {
        when (view.id) {
            R.id.floatingButton -> view.findNavController()
                .navigate(
                    HomeDirections.actionHomeToScanner(
                        null,
                        floor = floor.get(),
                        date = date.get(),
                        cycleCountId = cycleCountId.get() ?: 0
                    )
                ) /*{
                view.findNavController().navigate(
                    HomeDirections.actionHomeToInput(
                        barcode = "CFCF240814",
                        quantity = 1000063,
                        date = "16-02-2023",
                        floor = "GF",
                        cycleCountId = 16
                    ))
            }*/

        }
    }

    fun getCycleCountByPicker() = viewModelScope.launch {
        repository.makeCall(loader = true, requestProcessor = object :
            ApiProcessor<Response<JsonElement>> {
            override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                return retrofitApi.getCycleCountByPicker(
                    repository.authToken
                )
            }

            override fun onResponse(res: Response<JsonElement>) {
                if (res.isSuccessful && res.body() != null) {
                    jsonElementToData<PickerResponse>(res.body()) {
                        jsonStringToData<PickerItemData>(it.data) { pickerItemData ->
                            isDataLoaded.set(true)
                            date.set(pickerItemData.dateOfCreation?.getUtcToLocalFormat())
                            floor.set(pickerItemData.floor ?: "")
                            cycleCountId.set(pickerItemData.cycleCountId)
                            pickerItemData.items?.let { items ->
                                itemRecyclerView.addItems(
                                    items
                                )
                            }
                        }
                    }
                }
            }

            override fun onError(message: String, responseCode: Int) {
                super.onError(message, responseCode)
                isDataLoaded.set(false)
            }
        })
    }

    fun verifyItemCode(itemCode: String, view: View?) = viewModelScope.launch {
        repository.makeCall(
            loader = true,
            requestProcessor = object : ApiProcessor<Response<JsonElement>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                    return retrofitApi.verifyItem(repository.authToken, itemCode)
                }

                override fun onResponse(res: Response<JsonElement>) {
                    if (res.isSuccessful && res.body() != null) {
                        jsonElementToData<VerifyItem>(res.body()) {
                            if (it.isVerified == true) {
                                if (view?.findNavController()?.currentDestination?.id == R.id.home) {
                                    view.findNavController().navigate(
                                        HomeDirections.actionHomeToInput(
                                            barcode = itemCode.trim(),
                                            quantity = 3.takeIf {
                                                itemCode.trim().startsWith("NBR")
                                                        || itemCode.trim().startsWith("IBR")
                                            } ?: 1,
                                            date = date.get() ?: "",
                                            floor = floor.get() ?: "",
                                            cycleCountId = cycleCountId.get() ?: 0
                                        ))
                                }
                            }
                            it.successMessage?.showToast()
                        }
                    }
                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    message.showToast()
                }
            })
    }

}

