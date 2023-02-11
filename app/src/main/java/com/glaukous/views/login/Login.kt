
package com.glaukous.views.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glaukous.MainActivity
import com.glaukous.databinding.LoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Login : Fragment() {

    private lateinit var binding: LoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LoginBinding.inflate(layoutInflater)
        binding.model = viewModel
        return binding.root
    }

    /*private fun clickListeners() {
        with(binding) {
            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.)
            }
            loginBtn.setOnClickListener {
                credValidator(
                    viewModel.userId.get() ?: "", viewModel.passcode.get
                        () ?: ""
                ) { isValid ->
                    if (isValid) {
                        if (viewModel.remember.get() == true) {
                            viewModel.preferences.storeKey(email, viewModel.userId.get() ?: "")
                            viewModel.preferences.storeKey(password, viewModel.passcode.get() ?: "")
                            viewModel.preferences.storeBoolKey(
                                rememberMe, viewModel.remember.get()!!
                            )
                        }

                        viewModel.loading.set(true)
                        viewModel.login(it)
                    }
                }
            }
        }
    }*/


}
