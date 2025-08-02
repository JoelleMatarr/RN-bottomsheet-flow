// android/app/src/main/java/com/flowreactnativeapp/FlowPlatformView.kt

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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.checkout.components.core.CheckoutComponentsFactory
import com.checkout.components.interfaces.Environment
import com.checkout.components.interfaces.api.CheckoutComponents
import com.checkout.components.interfaces.component.CheckoutComponentConfiguration
import com.checkout.components.interfaces.component.ComponentCallback
import com.checkout.components.interfaces.error.CheckoutError
import com.checkout.components.interfaces.model.PaymentMethodName
import com.checkout.components.interfaces.model.PaymentSessionResponse
import com.checkout.components.interfaces.uicustomisation.BorderRadius
import com.checkout.components.interfaces.uicustomisation.designtoken.ColorTokens
import com.checkout.components.interfaces.uicustomisation.designtoken.DesignTokens
import com.checkout.components.interfaces.uicustomisation.font.*
import com.checkout.components.wallet.wrapper.GooglePayFlowCoordinator
import kotlinx.coroutines.*

class FlowPlatformView(context: Context, private val activity: FragmentActivity, private val onRequest3DS: () -> Unit) : FrameLayout(context) {

    private val container = FrameLayout(context)
    private var sessionId: String? = null
    private var sessionSecret: String? = null
    private var publicKey: String? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var checkoutComponents: CheckoutComponents

    init {
        // We will not render anything here. The rendering logic will be moved to a separate method.
        // This prevents the "ViewTreeLifecycleOwner not found" error during view initialization.
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // This is the crucial part. Now that the view is attached, we can render safely.
        setViewTreeLifecycleOwner(activity)
        setViewTreeViewModelStoreOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)

        renderFlowComponent()
    }

    private fun renderFlowComponent() {
        val composeView = ComposeView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        // It's still good practice to set these here, just in case
        composeView.setViewTreeLifecycleOwner(activity)
        composeView.setViewTreeViewModelStoreOwner(activity)
        composeView.setViewTreeSavedStateRegistryOwner(activity)

        composeView.setContent {
            var flowComponentContent: (@Composable () -> Unit)? by remember { mutableStateOf(null) }

            // The rest of your rendering logic goes here
            LaunchedEffect(Unit) {
                try {
                    // ... (Your existing rendering logic is now inside this block) ...
                    if (sessionId == null || sessionSecret == null || publicKey == null) {
                        Log.e("FlowPlatformView", "Missing credentials, cannot initialize")
                        return@LaunchedEffect
                    }

                    val customComponentCallback = ComponentCallback(
                        onReady = { component ->
                            Log.d("flow component","test onReady "+component.name)
                        },
                        onSubmit = { component ->
                            Log.d("flow", "onSubmit -> trigger 3DS, dismissing sheet")
                            onRequest3DS()
                        },
                        onSuccess = { component, paymentID ->
                            Log.d("flow payment success ${component.name}", paymentID)
                        },
                        onError = { component, checkoutError ->
                            Log.d("flow callback Error","onError "+checkoutError.message+", "+checkoutError.code)
                        },
                    )

                    // Your existing setup code for the SDK goes here...
                    val config = CheckoutComponentConfiguration(
                        context = container.context,
                        paymentSession = PaymentSessionResponse(
                            id = sessionId!!,
                            secret = sessionSecret!!
                        ),
                        publicKey = publicKey!!,
                        environment = Environment.SANDBOX,
                        componentCallback = customComponentCallback
//                        appearance = designTokens,
//                        flowCoordinators = flowCoordinators
                    )

                    checkoutComponents = CheckoutComponentsFactory(config).create()

                    val flowComponent = checkoutComponents.create(PaymentMethodName.Card)
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


