package com.glaukous.views.input

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.glaukous.MainVM
import com.glaukous.R
import com.glaukous.databinding.InputBinding
import com.glaukous.extensions.showToast
import com.glaukous.interfaces.Barcode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Input : Fragment(), Barcode {
    private var _binding: InputBinding? = null
    private var binding = _binding
    private val viewModel by viewModels<InputVM>()
    private val args by navArgs<InputArgs>()
    private val mainVM by activityViewModels<MainVM>()

    companion object {
        var inputCode: Barcode? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = InputBinding.inflate(layoutInflater, container, false)
        binding?.viewModel = viewModel
        inputCode = this
        viewModel.barcode.set(args.barcode)
        viewModel.quantity.set(args.quantity)
        return binding?.root
    }


    private var count = 0
    private var barCodeData:String=""
    override fun barcode(barcode: String) {
        if (barcode.isNotEmpty()){
            this.barCodeData =barcode
        }
        ++count
        Log.e("Input", "barcode: $count, \n $barCodeData")
        val increasedBy =
            3.takeIf { barCodeData.trim().startsWith("NBR") || barCodeData.trim().startsWith("IBR") }
                ?: 1.takeIf { barCodeData.trim().equals(args.barcode, true) } ?: 0

        if (count / 2 == 1) {
            try {
                if (barCodeData.isNotEmpty() && barCodeData.equals(viewModel.barcode.get(), true)) {
                    updateButton()
                    viewModel.quantity.set(
                        viewModel.quantity.get()
                            ?.plus(increasedBy) ?: 0
                    )
                } else {
                    count = 0
                    "Code doesn't match".showToast(requireContext())
                }

            } catch (e: Exception) {
                Log.e("Exception", "barcode: ${e.message}")
            }
        }
    }

    private fun updateButton() {
        Handler(Looper.getMainLooper()).postDelayed({
            mainVM.keyEvent = 0
            count = 0
        }, 200)
    }

    override fun navigateToScanner() {
        super.navigateToScanner()
        if (findNavController().currentDestination?.id == R.id.input) {
            findNavController()
                .navigate(
                    InputDirections.actionInputToScanner(
                        barcode = viewModel.barcode.get(),
                        quantity = viewModel.quantity.get() ?: 0
                    )
                )
        }
    }
}