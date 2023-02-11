package com.glaukous.views.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.glaukous.MainActivity
import com.glaukous.MainVM
import com.glaukous.R
import com.glaukous.databinding.HomeBinding
import com.glaukous.extensions.showToast
import com.glaukous.interfaces.Barcode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Home : Fragment(), Barcode {
    private var _binding: HomeBinding? = null
    private var binding = _binding
    private val viewModel by viewModels<HomeVM>()
    private val mainVM by activityViewModels<MainVM>()
    private val args by navArgs<HomeArgs>()


    companion object {
        var barcode: Barcode? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = HomeBinding.inflate(layoutInflater, container, false)
        binding?.viewModel = viewModel
        barcode = this
        MainActivity.navigator.showAppBar(show = true)
        viewModel.comingFrom.set(args.comingFrom == "Input")
        viewModel.isAccepted.set(args.comingFrom == "Input")
        return binding?.root
    }

    private var entry = 0
    override fun barcode(barcode: String) {
        entry++
        if (barcode.isNotEmpty() && entry / 2 == 0) {
            updateButton()
            Handler(Looper.getMainLooper()).postDelayed({
                if (findNavController().currentDestination?.id == R.id.home
                    && viewModel.isAccepted.get() == true
                ) {
                    findNavController().navigate(
                        HomeDirections.actionHomeToInput(
                            barcode = barcode.trim(),
                            quantity = 3.takeIf {
                                barcode.trim().startsWith("NBR")
                                        || barcode.trim().startsWith("IBR")
                            } ?: 1))
                } else {
                    "Please Accept the cycle count".showToast()
                }

            }, 200)
        }
    }

    private fun updateButton() {
        Handler(Looper.getMainLooper()).postDelayed({
            entry = 0
            mainVM.keyEvent = 0
        }, 200)
    }

    override fun navigateToScanner() {
        super.navigateToScanner()

        if (findNavController().currentDestination?.id == R.id.home
            && viewModel.isAccepted.get() == true
        ) {
            findNavController()
                .navigate(HomeDirections.actionHomeToScanner(null))
        } else {
            "Please Accept the cycle count".showToast()
        }
        Log.e("TAG", "navigateToScanner: MainActivity")
    }
}