package com.flowreactnativeapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

class FlowFragment(
    private val renderer: (@Composable () -> Unit)?
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        Log.d("✅ FlowFragment", "Rendering ComposeView")
        composeView.setContent {
            renderer?.invoke()
        }
        return composeView
    }
}
