package com.glaukous.views.home

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.MainActivity.Companion.context
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
    val isDataAvailable=MutableLiveData(true)

    init {
        itemRecyclerView.setOnItemClick { view, _, position ->
            when (view.id) {
                R.id.cvItem -> {
                    view.findNavController().navigate(
                        HomeDirections.actionHomeToInput(
                            barcode = itemRecyclerView.getItemAt(position).itemBarCode ?: "",
                            itemCode= itemRecyclerView.getItemAt(position).itemCode ?: "",
                            quantity = itemRecyclerView.getItemAt(position).quantityCount ?: 1,
                            date = date.get() ?: "",
                            floor = floor.get() ?: "",
                            cycleCountId = cycleCountId.get() ?: 0
                        )
                    )
                }
            }
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.floatingButton -> {
                if ((context.get() as MainActivity).mainVM.keyEvent == 0) {
                    view.findNavController()
                        .navigate(
                            HomeDirections.actionHomeToScanner(
                                null,
                                floor = floor.get(),
                                date = date.get(),
                                cycleCountId = cycleCountId.get() ?: 0
                            )
                        )
                } else {
                    verifyItemCode((context.get() as MainActivity).barcodes.trim(), view)
                    (context.get() as MainActivity).barcodes = ""
                    (context.get() as MainActivity).mainVM.keyEvent = 0
                }
            }
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
                        if (it.data!=null){
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
                        }else{
                            "No data available!".showToast()
                            isDataAvailable.value=false
                        }
                    }
                }

            }

            override fun onError(message: String, responseCode: Int) {
                super.onError(message, responseCode)
                isDataLoaded.set(false)
                message.showToast()
            }
        })
    }

    fun verifyItemCode(itemBarCode: String, view: View?) = viewModelScope.launch {
        repository.makeCall(
            loader = true,
            requestProcessor = object : ApiProcessor<Response<JsonElement>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                    return retrofitApi.verifyItem(repository.authToken, itemBarCode)
                }

                override fun onResponse(res: Response<JsonElement>) {
                    if (res.isSuccessful && res.body() != null) {
                        jsonElementToData<VerifyItem>(res.body()) { verifiedItem ->
                            if (verifiedItem.isVerified == true) {
                                if (view?.findNavController()?.currentDestination?.id == R.id.home) {
                                    view.findNavController().navigate(
                                        HomeDirections.actionHomeToInput(
                                            barcode = itemBarCode.trim(),
                                            itemCode=verifiedItem.itemCode?:"",
                                            quantity = verifiedItem.completedCount.takeIf {
                                                (it ?: 0) > 0
                                            } ?: 3.takeIf {
                                                (verifiedItem.itemCode?:"").trim().startsWith("NBR")
                                                        || (verifiedItem.itemCode?:"").trim().startsWith("IBR")
                                            } ?: 1,
                                            date = date.get() ?: "",
                                            floor = floor.get() ?: "",
                                            cycleCountId = cycleCountId.get() ?: 0
                                        ))
                                }
                            }
                            verifiedItem.successMessage?.showToast()
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

