package com.glaukous.views.login

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.datastore.LOGIN_DATA
import com.glaukous.extensions.jsonElementToData
import com.glaukous.extensions.jsonStringToData
import com.glaukous.extensions.showToast
import com.glaukous.networkcalls.ApiProcessor
import com.glaukous.networkcalls.Repository
import com.glaukous.networkcalls.RetrofitApi
import com.glaukous.pref.*
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val repository: Repository,
    private val apiInterface: RetrofitApi,
    private val preferencesUtils: PreferenceFile
) : ViewModel() {

    var remember: ObservableField<Boolean> = ObservableField(false)
    val userId = ObservableField("")
    val passcode = ObservableField("")
    var loading: ObservableField<Boolean> = ObservableField(false)


    init {
        remember.set(preferencesUtils.retrieveBoolKey(rememberMe))
        userId.set(preferencesUtils.retrieveKey(email))
        passcode.set(preferencesUtils.retrieveKey(password))
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.rememberChkBox -> remember.set(!(remember.get() ?: false))
            R.id.loginBtn -> {
                verifyCredentials(
                    remember = remember.get()!!,
                    userId = userId.get() ?: "",
                    passCode = passcode.get() ?: ""
                ) {
                    login(view)
                }
            }
        }
    }

    private fun login(view: View) = viewModelScope.launch {
        repository.makeCall(
            loader = true,
            requestProcessor = object : ApiProcessor<Response<JsonElement>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<JsonElement> {
                    return retrofitApi.login(
                        userId = userId.get() ?: "",
                        password = passcode.get() ?: ""
                    )
                }

                override fun onResponse(res: Response<JsonElement>) {
                    if (res.isSuccessful && res.body() != null) {
                        jsonElementToData<LoginResponse>(
                            res.body(),
                        ) { loginData ->

                            jsonStringToData<LoginData>(loginData.data) {
                                dataStoreUtil.saveObject(LOGIN_DATA, it)
                                it.token?.let { tokenData ->
                                    preferencesUtils.storeKey(
                                        token,
                                        "Bearer $tokenData"
                                    )
                                }
                                view.findNavController()
                                    .navigate(LoginDirections.actionLoginToHome())
                            }
                        }

                    }

                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    message.showToast()
                }
            }
        )

    }


    private fun verifyCredentials(
        remember: Boolean,
        userId: String,
        passCode: String,
        approve: () -> Unit
    ) {
        val isCredValid = when {
            userId.isEmpty() -> false
            passCode.isEmpty() -> false
            else -> true
        }
        if (remember && isCredValid) {
            preferencesUtils.storeKey(email, userId)
            preferencesUtils.storeKey(password, passCode)
            preferencesUtils.storeBoolKey(rememberMe, remember)
        }
        if (isCredValid)
            approve()
        else
            "Please enter a valid credentials".showToast()

    }
}