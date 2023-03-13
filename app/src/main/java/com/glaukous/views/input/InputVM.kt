package com.glaukous.views.input

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.MainActivity.Companion.context
import com.glaukous.R
import com.glaukous.databinding.InputBinding
import com.glaukous.databinding.ScannerBinding
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.extensions.jsonElementToData
import com.glaukous.extensions.showToast
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import com.glaukous.utils.getRequestBody
import com.glaukous.views.scanner.ScannerDirections
import com.glaukous.views.scanner.VerifyItem
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class InputVM @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {


    val cycleCountId = ObservableField(0)
    val barcode = ObservableField("")
    val quantity = ObservableField(1)
    val floor = ObservableField("")
    val date = ObservableField("")
    val submitted = ObservableField(false)
    private var done = false

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnCancel -> {
                (context.get() as MainActivity).barcodes = ""
                (context.get() as MainActivity).mainVM.keyEvent = 0
                view.findNavController()
                    .navigate(InputDirections.actionInputToHome("Input"))
            }

            /*R.id.ivMinus -> {
                if (quantity.get()!! > 1 && quantity.get() != null) {
                    quantity.set(quantity.get()!!.minus(1))
                }
            }*/

            R.id.ivPlus -> view.findNavController().navigate(
                InputDirections.actionInputToScanner(
                    barcode = barcode.get(),
                    quantity = quantity.get() ?: 0,
                    floor = floor.get(),
                    date = date.get(),
                    cycleCountId = cycleCountId.get() ?: 0
                )
            )

            R.id.btnDone -> {
                done = true
                submitCount(view)
            }
        }
    }

    fun submitCount(view: View) {
        repository.makeCall(
            loader = true,
            requestProcessor = object : ApiProcessor<Response<ResponseBody>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<ResponseBody> {
                    return retrofitApi.submitCount(
                        repository.authToken, Gson().toJson(
                            Submit(
                                cycleCountId = cycleCountId.get(),
                                itemDetails = listOf(
                                    ItemDetailsItem(
                                        itemQuantity = quantity.get(),
                                        itemBarcode = barcode.get(),
                                        floor = floor.get()
                                    )
                                )
                            )
                        ).getRequestBody()
                    )
                }

                override fun onResponse(res: Response<ResponseBody>) {
                    if (res.isSuccessful) {
                        if (done) {
                            view.findNavController().navigate(InputDirections.actionInputToHome())
                            (context.get() as MainActivity).barcodes = ""
                            (context.get() as MainActivity).mainVM.keyEvent = 0
                        }
                        submitted.set(true)
                    }
                    res.body()?.string().let { it?.showToast() }
                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    message.showToast()
                }
            })
    }

    fun verifyItemCode(itemCode: String, view: InputBinding?) = viewModelScope.launch {
        repository.makeCall(
            loader = true,
            requestProcessor = object : ApiProcessor<Response<JsonElement>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                    return retrofitApi.verifyItem(repository.authToken, itemCode)
                }

                override fun onResponse(res: Response<JsonElement>) {
                    if (res.isSuccessful && res.body() != null) {
                        jsonElementToData<VerifyItem>(res.body()) { verifiedItem ->
                            "This item has ${verifiedItem.completedCount} quantity.".showToast()
                            if (verifiedItem.isVerified == true) {
                                barcode.set(itemCode)
                                quantity.set(verifiedItem.completedCount?.takeIf { it >= 1 } ?: 1)
                            } else {
                                view?.root?.findNavController()?.popBackStack()
                            }
                            verifiedItem.successMessage?.showToast()
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
