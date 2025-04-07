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
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontStyle
//import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.checkout.components.core.CheckoutComponentsFactory
import com.checkout.components.interfaces.Environment
import com.checkout.components.interfaces.api.CheckoutComponents
import com.checkout.components.interfaces.component.CheckoutComponentConfiguration
import com.checkout.components.interfaces.component.ComponentOption
import com.checkout.components.interfaces.error.CheckoutError
import com.checkout.components.interfaces.model.ComponentName
import com.checkout.components.interfaces.model.PaymentMethodName
import com.checkout.components.interfaces.model.PaymentSessionResponse
import com.checkout.components.interfaces.uicustomisation.font.FontName
import com.checkout.components.interfaces.uicustomisation.BorderRadius
import com.checkout.components.interfaces.uicustomisation.designtoken.ColorTokens
import com.checkout.components.interfaces.uicustomisation.font.*
import com.checkout.components.interfaces.uicustomisation.designtoken.DesignTokens
import com.checkout.components.wallet.wrapper.GooglePayFlowCoordinator
import kotlinx.coroutines.*

class FlowPlatformView(context: Context,
    private val lifecycleOwner: LifecycleOwner
) : FrameLayout(context) {

    private val container = FrameLayout(context)
    private var sessionId: String? = null
    private var sessionSecret: String? = null
    private var publicKey: String? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var checkoutComponents: CheckoutComponents

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

                    val coordinator = GooglePayFlowCoordinator(
                        context = context, // ✅ Requires ComponentActivity
                        handleActivityResult = { resultCode, data ->
                            handleActivityResult(resultCode, data)

                        }
                    )

                    val flowCoordinators = mapOf(PaymentMethodName.GooglePay to coordinator)

                    val designTokens = DesignTokens(
                        colorTokens = ColorTokens(
                            colorPrimary = 0xFFEA5D29.toLong(),       // vivid orange
                            colorAction = 0xFFEA5D29.toLong(),
                            colorBackground = 0xFFFFFFFF.toLong(),    // solid white
                            colorBorder = 0xFFEA5D29.toLong(),
                            colorDisabled = 0xFFB8B8B8.toLong(),
                            colorFormBackground = 0xFFFFFFFF.toLong(),
                            colorFormBorder = 0xFFC9C9C9.toLong(),
                            colorInverse = 0xFFFFFFFF.toLong(),
                            colorOutline = 0xFFEA5D29.toLong(),
                            colorSecondary = 0xFF000000.toLong(),     // solid black for text
                            colorSuccess = 0xFFEA5D29.toLong(),
                            colorError = 0xFFFF0000.toLong()
                        ),
                        borderButtonRadius = BorderRadius(all = 20),
                        borderFormRadius = BorderRadius(all = 12),
                        fonts = mapOf(
                            FontName.Subheading to Font(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                            ),
                            FontName.Input to Font(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                            ),
                            FontName.Button to Font(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                            ),
                            FontName.Label to Font(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.SansSerif,
                            ),
                        )
                    )

                    val config = CheckoutComponentConfiguration(
                        context = container.context,
                        paymentSession = PaymentSessionResponse(
                            id = sessionId!!,
                            paymentSessionToken = "YmFzZTY0:eyJpZCI6InBzXzJ2T2JzTnN4OEw4MWtNR3FGUlNRZmVPcFdRNCIsImVudGl0eV9pZCI6ImVudF9uaHh2Y2phajc1NXJ3eno2emlkYXl5d29icSIsImV4cGVyaW1lbnRzIjp7fSwicHJvY2Vzc2luZ19jaGFubmVsX2lkIjoicGNfdGljZDZ0MnJybW51amFjYWthZnZ1a2hid3UiLCJhbW91bnQiOjEwMCwibG9jYWxlIjoiZW4tR0IiLCJjdXJyZW5jeSI6IlNBUiIsInBheW1lbnRfbWV0aG9kcyI6W3sidHlwZSI6ImNhcmQiLCJjYXJkX3NjaGVtZXMiOlsiVmlzYSIsIk1hc3RlcmNhcmQiXSwic2NoZW1lX2Nob2ljZV9lbmFibGVkIjpmYWxzZSwic3RvcmVfcGF5bWVudF9kZXRhaWxzIjoiZGlzYWJsZWQifSx7InR5cGUiOiJhcHBsZXBheSIsImRpc3BsYXlfbmFtZSI6InRlc3QiLCJjb3VudHJ5X2NvZGUiOiJTQSIsImN1cnJlbmN5X2NvZGUiOiJTQVIiLCJtZXJjaGFudF9jYXBhYmlsaXRpZXMiOlsic3VwcG9ydHMzRFMiXSwic3VwcG9ydGVkX25ldHdvcmtzIjpbInZpc2EiLCJtYXN0ZXJDYXJkIl0sInRvdGFsIjp7ImxhYmVsIjoidGVzdCIsInR5cGUiOiJmaW5hbCIsImFtb3VudCI6IjEifX0seyJ0eXBlIjoiZ29vZ2xlcGF5IiwibWVyY2hhbnQiOnsiaWQiOiIwODExMzA4OTM4NjI2ODg0OTk4MiIsIm5hbWUiOiJ0ZXN0Iiwib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDozMDAxIn0sInRyYW5zYWN0aW9uX2luZm8iOnsidG90YWxfcHJpY2Vfc3RhdHVzIjoiRklOQUwiLCJ0b3RhbF9wcmljZSI6IjEiLCJjb3VudHJ5X2NvZGUiOiJTQSIsImN1cnJlbmN5X2NvZGUiOiJTQVIifSwiY2FyZF9wYXJhbWV0ZXJzIjp7ImFsbG93ZWRfYXV0aF9tZXRob2RzIjpbIlBBTl9PTkxZIiwiQ1JZUFRPR1JBTV8zRFMiXSwiYWxsb3dlZF9jYXJkX25ldHdvcmtzIjpbIlZJU0EiLCJNQVNURVJDQVJEIl19fV0sImZlYXR1cmVfZmxhZ3MiOlsiYW5hbHl0aWNzX29ic2VydmFiaWxpdHlfZW5hYmxlZCIsImdldF93aXRoX3B1YmxpY19rZXlfZW5hYmxlZCIsImxvZ3Nfb2JzZXJ2YWJpbGl0eV9lbmFibGVkIiwicmlza19qc19lbmFibGVkIiwidXNlX25vbl9iaWNfaWRlYWxfaW50ZWdyYXRpb24iXSwicmlzayI6eyJlbmFibGVkIjpmYWxzZX0sIm1lcmNoYW50X25hbWUiOiJ0ZXN0IiwicGF5bWVudF9zZXNzaW9uX3NlY3JldCI6InBzc18yZjZkMDNlZC05OGFhLTQ2MTctYWUyMC03Yzg1YmNjZjk0ZDgiLCJwYXltZW50X3R5cGUiOiJSZWd1bGFyIiwiaW50ZWdyYXRpb25fZG9tYWluIjoiYXBpLnNhbmRib3guY2hlY2tvdXQuY29tIn0=", // <- Replace if needed
                            paymentSessionSecret = sessionSecret!!
                        ),
                        publicKey = publicKey!!,
                        environment = Environment.SANDBOX,
                        appearance = designTokens,
                        flowCoordinators = flowCoordinators
                    )

                     checkoutComponents = CheckoutComponentsFactory(config).create()

                    //--------------------Cards Only using Flow--------------------//
//                    val flowComponent = checkoutComponents.create(PaymentMethodName.Card)
//
//                    if (flowComponent.isAvailable()) {
//                        flowComponentContent = { flowComponent.Render() }
//                        Log.d("FlowPlatformView", "✅ Flow component is available and rendering.")
//                    } else {
//                        Log.e("FlowPlatformView", "❌ Flow component is NOT available")
//                    }

                    //--------------------GooglePay Only using Flow (uncomment)--------------------//
//                    val gpayComponent = checkoutComponents.create(PaymentMethodName.GooglePay)
//
//                    if (gpayComponent.isAvailable()) {
//                        flowComponentContent = { gpayComponent.Render() }
//                        Log.d("FlowPlatformView", "✅ Flow component is available and rendering.")
//                    } else {
//                        Log.e("FlowPlatformView", "❌ Flow component is NOT available")
//                    }


                    //--------------------Flow full component Card + GooglePay--------------------//
                    val flowFullComponent = checkoutComponents.create(ComponentName.Flow)

                    if (flowFullComponent.isAvailable()) {
                        flowComponentContent = { flowFullComponent.Render() }
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

    private fun handleActivityResult(resultCode: Int, data: String) {
        checkoutComponents?.handleActivityResult(resultCode, data)
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
