package com.glaukous.views.scanner

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.databinding.ScannerBinding
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.extensions.jsonElementToData
import com.glaukous.extensions.showToast
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import com.glaukous.pref.token
import com.glaukous.views.home.TAG
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ScannerVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {
    val cycleCountId = ObservableField(0)

    val date = ObservableField("")
    val floor = ObservableField("")


    fun navigateToInput(view: View, barcode: String, quantity: Int) {
        view.findNavController().navigate(
            ScannerDirections.actionScannerToInput(
                barcode = barcode,
                quantity = quantity,
                floor = floor.get() ?: "",
                date = date.get() ?: "",
                cycleCountId = cycleCountId.get() ?: 0
            )
        )

    }

    fun verifyItemCode(itemCode: String, view: ScannerBinding?) = viewModelScope.launch {
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
                                view?.root?.findNavController()?.navigate(
                                    ScannerDirections.actionScannerToInput(
                                        barcode = itemCode.trim(),
                                        quantity = 3.takeIf {
                                            itemCode.trim().startsWith("NBR")
                                                    || itemCode.trim().startsWith("IBR")
                                        } ?: 1,
                                        floor = floor.get() ?: "",
                                        date = date.get() ?: "",
                                        cycleCountId = cycleCountId.get() ?: 0))
                            } else {
                                view?.root?.findNavController()?.popBackStack()
                            }
                            it.successMessage?.showToast()
                        }

                    }

                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    message.showToast()
                    view?.root?.findNavController()?.popBackStack()
                }
            })
    }
}