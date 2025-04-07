package com.flowreactnativeapp

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.checkout.components.core.CheckoutComponentsFactory
import com.checkout.components.interfaces.Environment
import com.checkout.components.interfaces.api.CheckoutComponents
import com.checkout.components.interfaces.component.CheckoutComponentConfiguration
import com.checkout.components.interfaces.error.CheckoutError
import com.checkout.components.interfaces.model.ComponentName
import com.checkout.components.interfaces.model.PaymentSessionResponse
import kotlinx.coroutines.*

class FlowPlatformView(context: Context,
    private val lifecycleOwner: LifecycleOwner
) : FrameLayout(context) {

    private val container = FrameLayout(context)
    private var sessionId: String? = null
    private var sessionSecret: String? = null
    private var publicKey: String? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        val composeView = ComposeView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        }
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setContent {
            var flowComponentContent: (@Composable () -> Unit)? by remember { mutableStateOf(null) }

            // Auto-render Flow on launch
            LaunchedEffect(Unit) {
                try {
                    if (sessionId == null || sessionSecret == null || publicKey == null) {
                        Log.e("FlowPlatformView", "Missing credentials, cannot initialize")
                        return@LaunchedEffect
                    }

                    val config = CheckoutComponentConfiguration(
                        context = container.context,
                        paymentSession = PaymentSessionResponse(
                            id = sessionId!!,
                            paymentSessionToken = "YmFzZTY0:eyJpZCI6InBzXzJ2TjBTSEY5QWlyMVBwUG1DYXE0UzZvU3ZaNCIsImVudGl0eV9pZCI6ImVudF9uaHh2Y2phajc1NXJ3eno2emlkYXl5d29icSIsImV4cGVyaW1lbnRzIjp7fSwicHJvY2Vzc2luZ19jaGFubmVsX2lkIjoicGNfdGljZDZ0MnJybW51amFjYWthZnZ1a2hid3UiLCJhbW91bnQiOjEwMCwibG9jYWxlIjoiZW4tR0IiLCJjdXJyZW5jeSI6IkFFRCIsInBheW1lbnRfbWV0aG9kcyI6W3sidHlwZSI6ImNhcmQiLCJjYXJkX3NjaGVtZXMiOlsiVmlzYSIsIk1hc3RlcmNhcmQiXSwic2NoZW1lX2Nob2ljZV9lbmFibGVkIjpmYWxzZSwic3RvcmVfcGF5bWVudF9kZXRhaWxzIjoiZGlzYWJsZWQifSx7InR5cGUiOiJhcHBsZXBheSIsImRpc3BsYXlfbmFtZSI6InRlc3QiLCJjb3VudHJ5X2NvZGUiOiJTQSIsImN1cnJlbmN5X2NvZGUiOiJBRUQiLCJtZXJjaGFudF9jYXBhYmlsaXRpZXMiOlsic3VwcG9ydHMzRFMiXSwic3VwcG9ydGVkX25ldHdvcmtzIjpbInZpc2EiLCJtYXN0ZXJDYXJkIl0sInRvdGFsIjp7ImxhYmVsIjoidGVzdCIsInR5cGUiOiJmaW5hbCIsImFtb3VudCI6IjEifX0seyJ0eXBlIjoiZ29vZ2xlcGF5IiwibWVyY2hhbnQiOnsiaWQiOiIwODExMzA4OTM4NjI2ODg0OTk4MiIsIm5hbWUiOiJ0ZXN0Iiwib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDozMDAxIn0sInRyYW5zYWN0aW9uX2luZm8iOnsidG90YWxfcHJpY2Vfc3RhdHVzIjoiRklOQUwiLCJ0b3RhbF9wcmljZSI6IjEiLCJjb3VudHJ5X2NvZGUiOiJTQSIsImN1cnJlbmN5X2NvZGUiOiJBRUQifSwiY2FyZF9wYXJhbWV0ZXJzIjp7ImFsbG93ZWRfYXV0aF9tZXRob2RzIjpbIlBBTl9PTkxZIiwiQ1JZUFRPR1JBTV8zRFMiXSwiYWxsb3dlZF9jYXJkX25ldHdvcmtzIjpbIlZJU0EiLCJNQVNURVJDQVJEIl19fSx7InR5cGUiOiJ0YW1hcmEiLCJjb3VudHJ5X2NhbGxpbmdfY29kZXMiOlsiOTcxIiwiOTciXX0seyJ0eXBlIjoidGFiYnkiLCJjb3VudHJ5X2NhbGxpbmdfY29kZXMiOlsiOTcxIl19XSwiZmVhdHVyZV9mbGFncyI6WyJhbmFseXRpY3Nfb2JzZXJ2YWJpbGl0eV9lbmFibGVkIiwiZ2V0X3dpdGhfcHVibGljX2tleV9lbmFibGVkIiwibG9nc19vYnNlcnZhYmlsaXR5X2VuYWJsZWQiLCJyaXNrX2pzX2VuYWJsZWQiLCJ1c2Vfbm9uX2JpY19pZGVhbF9pbnRlZ3JhdGlvbiJdLCJyaXNrIjp7ImVuYWJsZWQiOmZhbHNlfSwibWVyY2hhbnRfbmFtZSI6InRlc3QiLCJwYXltZW50X3Nlc3Npb25fc2VjcmV0IjoicHNzXzYzODZlMDFhLTA4ZWMtNDQyMS1iYWY0LTYxMTg4Yzg0MzIwZCIsInBheW1lbnRfdHlwZSI6IlJlZ3VsYXIiLCJpbnRlZ3JhdGlvbl9kb21haW4iOiJhcGkuc2FuZGJveC5jaGVja291dC5jb20ifQ==", // <- Replace if needed
                            paymentSessionSecret = sessionSecret!!
                        ),
                        publicKey = publicKey!!,
                        environment = Environment.SANDBOX
                    )

                    val checkoutComponents: CheckoutComponents = CheckoutComponentsFactory(config).create()
                    val flowComponent = checkoutComponents.create(ComponentName.Flow)

                    if (flowComponent.isAvailable()) {
                        flowComponentContent = { flowComponent.Render() }
                        Log.d("FlowPlatformView", "✅ Flow component is available and rendering.")
                    } else {
                        Log.e("FlowPlatformView", "❌ Flow component is NOT available")
                    }

                } catch (e: CheckoutError) {
                    Log.e("FlowPlatformView", "Checkout Error: ${e.message}")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                flowComponentContent?.invoke()
            }
        }

        this.addView(composeView)
    }

    fun setSessionId(value: String) {
        sessionId = value
    }

    fun setSessionSecret(value: String) {
        sessionSecret = value
    }

    fun setPublicKey(value: String) {
        publicKey = value
    }

    fun cleanUp() {
        scope.cancel()
    }
}
