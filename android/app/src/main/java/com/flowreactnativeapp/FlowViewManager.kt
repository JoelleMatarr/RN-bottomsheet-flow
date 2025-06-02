// android/app/src/main/java/com/flowreactnativeapp/FlowViewManager.kt
package com.flowreactnativeapp


import androidx.lifecycle.LifecycleOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp




class FlowViewManager(context: ReactApplicationContext) : SimpleViewManager<FlowPlatformView>() {


    override fun getName(): String = "FlowView"


    override fun createViewInstance(reactContext: ThemedReactContext): FlowPlatformView {
        val activity = reactContext.currentActivity
        if(activity is LifecycleOwner) {
            return FlowPlatformView(activity,activity)
        }else{
            throw IllegalStateException("Activity is not lifecycle owner!")
        }
    }


    @ReactProp(name = "paymentSessionID")
    fun setSessionId(view: FlowPlatformView, sessionId: String) {
        view.setSessionId(sessionId)
    }


    @ReactProp(name = "paymentSessionSecret")
    fun setSessionSecret(view: FlowPlatformView, sessionSecret: String) {
        view.setSessionSecret(sessionSecret)
    }


    @ReactProp(name = "publicKey")
    fun setPublicKey(view: FlowPlatformView, publicKey: String) {
        view.setPublicKey(publicKey)
    }
}
