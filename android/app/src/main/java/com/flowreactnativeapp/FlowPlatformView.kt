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
                            secret = sessionSecret!!
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
