package com.glaukous.views.login

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.glaukous.R
import com.glaukous.datastore.DataStoreUtil
import com.glaukous.datastore.LOGIN_DATA
import com.glaukous.datastore.USER_CREDS
import com.glaukous.extensions.jsonElementToData
import com.glaukous.extensions.jsonStringToData
import com.glaukous.extensions.showToast
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
        dataStoreUtil.readObject(USER_CREDS, LoginCredentials::class.java) {
            if (it != null) {
                remember.set(it.remember)
                userId.set(it.userId)
                passcode.set(it.password)
            }
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.rememberChkBox -> remember.set(!(remember.get() ?: false))
            R.id.loginBtn -> {
                verifyCredentials(
                    remember = remember.get()!!,
                    userId = userId.get() ?: "",
                    password = passcode.get() ?: ""
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
                    Log.e("TAG", "onResponse: ${res.body()}")
                    if (res.isSuccessful && res.body() != null) {
                        jsonElementToData<LoginResponse>(
                            res.body(),
                        ) { loginData ->

                            Log.e("TAG", "onResponse: ${loginData.data}")
                            jsonStringToData<LoginData>(loginData.data) {
                                Log.e("TAG", "onResponse: $it")
                                dataStoreUtil.saveObject(LOGIN_DATA, it)
                                it.token?.let { tokenData -> preferencesUtils.storeKey(token, tokenData) }
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
        password: String,
        approve: () -> Unit
    ) {
        val isCredValid = when {
            userId.isEmpty() -> false
            password.isEmpty() -> false
            else -> true
        }
        if (remember && isCredValid) {
            dataStoreUtil.saveObject(USER_CREDS, LoginCredentials(userId, password, remember))
        }
        if (isCredValid)
            approve()
        else
            "Please enter a valid credentials".showToast()

    }
}


data class LoginCredentials(
    val userId: String,
    val password: String,
    val remember: Boolean
)


