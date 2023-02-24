package com.glaukous.views.input

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.MainActivity.Companion.context
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.extensions.showToast
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.PreferenceFile
import com.glaukous.utils.getRequestBody
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
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
                submitCount(view)
            }
        }
    }

    private fun submitCount(view: View) {
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
                        view.findNavController().navigate(InputDirections.actionInputToHome())
                        (context.get() as MainActivity).barcodes = ""
                        (context.get() as MainActivity).mainVM.keyEvent = 0
                    }
                    res.body()?.string().let { it?.showToast() }
                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    message.showToast()
                }
            })
    }
}
