// android/app/src/main/java/com/flowreactnativeapp/CheckoutModule.kt

package com.flowreactnativeapp

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.UiThreadUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import android.os.Handler
import android.os.Looper

class CheckoutModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun getName() = "CheckoutModule"

    @ReactMethod
    fun showCheckoutBottomSheet(sessionId: String, sessionSecret: String, publicKey: String) {
        UiThreadUtil.runOnUiThread {
            val currentActivity = currentActivity
            if (currentActivity is FragmentActivity) {
                val flowPlatformView = FlowPlatformView(currentActivity, currentActivity) {
                    dismissBottomSheet()
                }
                flowPlatformView.setSessionId(sessionId)
                flowPlatformView.setSessionSecret(sessionSecret)
                flowPlatformView.setPublicKey(publicKey)

                bottomSheetDialog = BottomSheetDialog(currentActivity)
                bottomSheetDialog?.setContentView(flowPlatformView)
                bottomSheetDialog?.show()
            }
        }
    }

    private fun dismissBottomSheet() {
        UiThreadUtil.runOnUiThread {
            Handler(Looper.getMainLooper()).postDelayed({
                bottomSheetDialog?.dismiss()
                bottomSheetDialog = null
            }, 1500) // delay in milliseconds
        }
    }
}