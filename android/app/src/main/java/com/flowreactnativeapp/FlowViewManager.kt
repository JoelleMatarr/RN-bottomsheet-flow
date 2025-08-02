// android/app/src/main/java/com/flowreactnativeapp/FlowViewManager.kt
package com.flowreactnativeapp


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp




class FlowViewManager(context: ReactApplicationContext) : SimpleViewManager<FlowPlatformView>() {


    override fun getName(): String = "FlowView"


    override fun createViewInstance(reactContext: ThemedReactContext): FlowPlatformView {
        val activity = reactContext.currentActivity as? FragmentActivity
        val onRequest3ds = { /* no-op for now, you’ll handle this from React later if needed */ }
        val view = FlowPlatformView(reactContext, activity!!, onRequest3ds)

        // 🔥 THIS IS CRUCIAL — set lifecycle owners on the React View hierarchy root
//        view.setViewTreeLifecycleOwner(activity)
//        view.setViewTreeViewModelStoreOwner(activity)
//        view.setViewTreeSavedStateRegistryOwner(activity)

        // ✅ ALSO: Set them on reactContext itself (important!)
//        (reactContext as View)
//        reactContext.setViewTreeLifecycleOwner(activity)
//        reactContext.setViewTreeViewModelStoreOwner(activity)
//        reactContext.setViewTreeSavedStateRegistryOwner(activity)

        return view
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