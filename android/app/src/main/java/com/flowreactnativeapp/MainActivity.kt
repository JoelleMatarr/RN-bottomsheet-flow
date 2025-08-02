//package com.flowreactnativeapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import com.facebook.react.ReactActivity
//import com.facebook.react.ReactApplication
//import com.facebook.react.ReactRootView
//import com.facebook.react.ReactInstanceManager
//import com.facebook.react.ReactNativeHost
////import com.facebook.react.bridge.ReactApplication
//import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
//class MainActivity : ReactActivity() {
//    override fun getMainComponentName(): String {
//        return "FlowReactNativeApp"
//    }
//}

//package com.flowreactnativeapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import com.facebook.react.ReactActivityDelegate
//import com.facebook.react.ReactRootView
//import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
//import androidx.fragment.app.FragmentActivity
//
//class MainActivity : ComponentActivity(), DefaultHardwareBackBtnHandler {
//
//    private lateinit var reactDelegate: ReactActivityDelegate
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val localDelegate = object : ReactActivityDelegate(this, "FlowReactNativeApp") {
//            public override fun createRootView(): ReactRootView {
//                return ReactRootView(context)
//            }
//        }
//
//        reactDelegate = localDelegate
//        val rootView = localDelegate.createRootView()
//        setContentView(rootView)
//        localDelegate.onCreate(savedInstanceState)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        reactDelegate.onResume()
//    }
//
//    override fun onPause() {
//        reactDelegate.onPause()
//        super.onPause()
//    }
//
//    override fun onDestroy() {
//        reactDelegate.onDestroy()
//        super.onDestroy()
//    }
//
//    // ✅ REQUIRED for DefaultHardwareBackBtnHandler
//    override fun invokeDefaultOnBackPressed() {
//        super.onBackPressedDispatcher.onBackPressed()
//    }
//}

package com.flowreactnativeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler

class MainActivity : FragmentActivity(), DefaultHardwareBackBtnHandler {

    companion object {
        var instance: MainActivity? = null
            private set
    }

    private lateinit var reactDelegate: ReactActivityDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this // ✅ Store reference to access from native modules

        val localDelegate = object : ReactActivityDelegate(this, "FlowReactNativeApp") {
            public override fun createRootView(): ReactRootView {
                return ReactRootView(context)
            }
        }

        reactDelegate = localDelegate
        val rootView = localDelegate.createRootView()
        setContentView(rootView)
        localDelegate.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        reactDelegate.onResume()
    }

    override fun onPause() {
        reactDelegate.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        instance = null // ✅ Cleanup reference
        reactDelegate.onDestroy()
        super.onDestroy()
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressedDispatcher.onBackPressed()
    }
}

