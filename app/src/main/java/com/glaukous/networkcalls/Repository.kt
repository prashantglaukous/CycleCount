package com.glaukous.networkcalls


import android.util.Log
import com.glaukous.MainActivity
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.pref.PreferenceFile
import com.glaukous.pref.token
import com.glaukous.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(
    private val retrofitApi: RetrofitApi,
    private val cacheUtil: CacheUtil<String, Response<Any>>,
    private val dataStoreUtil: DataStoreUtil,
    private val preferenceFile: PreferenceFile
) {
    fun <T> makeCall(
        loader: Boolean,
        requestProcessor: ApiProcessor<Response<T>>,
    ) {
                    getResponseFromCall( loader,  requestProcessor)
    }

    private fun <T> getResponseFromCall(
        loader: Boolean,
        requestProcessor: ApiProcessor<Response<T>>
    ) {
        try {
            val activity = MainActivity.context.get()

            activity?.let {
                if (!activity.isNetworkAvailable()) {
                    activity.showNegativeAlerter(activity.getString(R.string.your_device_offline))
                    return
                }
                if (loader) {
//                    activity.showProgress()
                }
            }

            val dataResponse: Flow<Response<Any>> = flow {
                val response =
                    requestProcessor.sendRequest(retrofitApi) as Response<Any>
                emit(response)
            }.flowOn(Dispatchers.IO)

            CoroutineScope(Dispatchers.Main).launch {
                dataResponse.catch { exception ->
                    exception.printStackTrace()
//                    hideProgress()
                    activity?.let {
                        activity.showNegativeAlerter(exception.message ?: "")
                    }
                }.collect { response ->
                    Log.d("resCodeIs", "====${response.code()}")
//                    hideProgress()
                    when {
                        response.code() in 100..199 -> {
                            /**Informational*/
                            activity?.let {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: "",
                                    response.code()
                                )
                                activity.showNegativeAlerter(
                                    activity.resources?.getString(R.string.some_error_occured) ?: ""
                                )
                            }

                        }
                        response.isSuccessful -> {
                            /**Success*/
                            Log.d("successBody", "====${response.body()}")

                            requestProcessor.onResponse(response as Response<T>)
                        }
                        response.code() in 300..399 -> {
                            /**Redirection*/
                            activity?.let {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: "",
                                    response.code()
                                )
                                activity.showNegativeAlerter(
                                    activity.resources?.getString(R.string.some_error_occured) ?: ""
                                )
                            }
                        }
                        response.code() == 401 -> {
                            /**UnAuthorized*/
                            activity?.let {
                                activity.sessionExpired(preferenceFile)
                                requestProcessor.onError("unAuthorized", response.code())
                            }

                        }
                        response.code() == 404 -> {
                            /**Page Not Found*/
                            activity?.let {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: "",
                                    response.code()
                                )
                                activity.showNegativeAlerter(
                                    activity.resources?.getString(R.string.some_error_occured) ?: ""
                                )
                            }
                        }
                        response.code() in 500..599 -> {
                            /**ServerErrors*/
                            activity?.let {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: "",
                                    response.code()
                                )
                                activity.showNegativeAlerter(
                                    activity.resources?.getString(R.string.some_error_occured) ?: ""
                                )
                            }
                        }
                        else -> {
                            /**ClientErrors*/
                            val res = response.errorBody()!!.string()
                            val jsonObject = JSONObject(res)
                            when {
                                jsonObject.has("message") -> {
                                    Log.e(
                                        "Repository",
                                        "makeCall: ${jsonObject.getString("message")}"
                                    )
                                    requestProcessor.onError(
                                        jsonObject.getString("message"),
                                        response.code()
                                    )

                                    if (!jsonObject.getString("message")
                                            .equals("Data not found", true)
                                    )
                                        activity?.let {
                                            activity
                                                .showNegativeAlerter(jsonObject.getString("message"))
                                        }
                                }
                                jsonObject.has("error") -> {
                                    val message =
                                        jsonObject.getJSONObject("error").getString("text") ?: ""
                                    Log.e("Repository", "makeCall: ${message}")
                                    requestProcessor.onError(
                                        message,
                                        response.code()
                                    )

                                    if (!message.equals("Data not found", true))
                                        activity?.let {
                                            activity
                                                .showNegativeAlerter(message)
                                        }
                                }
                                else -> {
                                    activity?.let {
                                        requestProcessor.onError(
                                            activity.resources?.getString(R.string.some_error_occured)
                                                ?: "", response.code()
                                        )
                                        activity.showNegativeAlerter(
                                            activity.resources?.getString(R.string.some_error_occured)
                                                ?: ""
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
