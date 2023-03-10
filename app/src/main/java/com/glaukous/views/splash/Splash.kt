package com.glaukous.views.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.glaukous.MainActivity
import com.glaukous.R
import com.glaukous.databinding.SplashBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Splash : Fragment(R.layout.splash) {

    var binding: SplashBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashBinding.bind(view)
        MainActivity.navigator.showAppBar(show=false)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                arguments?.containsKey("notiType") == true -> handleNotification(
                    arguments
                )
                requireActivity().intent.extras?.containsKey("notiType") == true ->
                    handleNotification(
                        requireActivity().intent.extras
                    )

                else -> {
                    binding?.root?.findNavController()
                        ?.navigate(SplashDirections.actionSplashToLogin())
                }
            }

        }, 2000)
    }

    private fun handleNotification(bundle: Bundle?) {
        bundle?.let {
            try {
                when (bundle.getString("notiType")?.toInt()) {
                    /**MESSAGES*/
                    /*   0 -> userLoggedIn(
                           token,
                           SplashDirections.actionSplashToChatBox(
                               ChatRequestModel(
                                   chatId = bundle.getString("chatId"),
                                   name = bundle.getString("name"),
                                   promptAnswer = bundle.getString("promptAnswer"),
                                   receiverId = bundle.getString("receiverId"),
                                   baseProfilePic = bundle.getString("baseProfilePic"),
                                   promptId = bundle.getString("promptId"),
                                   promptName = bundle.getString("promptName"),
                                   isNotiAvailable = true
                               )
                           )
                       )

                       */
                    /**LIKES*//*
                    1 -> userLoggedIn(
                        token,
                        SplashDirections.actionSplashToLiked(isNotiAvailable = true)
                    )

                    */
                    /**MATCH*//*
                    2 -> userLoggedIn(token, SplashDirections.actionSplashToChat())

                    */
                    /**ADMIN*//*
                    3 -> userLoggedIn(token)*/
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}