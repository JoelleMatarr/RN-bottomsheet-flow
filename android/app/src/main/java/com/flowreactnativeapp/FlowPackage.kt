// android/app/src/main/java/com/yourapp/FlowPackage.kt
package com.flowreactnativeapp

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.uimanager.ViewManager

class FlowPackage : ReactPackage {
    override fun createNativeModules(context: com.facebook.react.bridge.ReactApplicationContext): List<NativeModule> {
        return emptyList() // No native modules for now
    }

    override fun createViewManagers(context: com.facebook.react.bridge.ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(FlowViewManager(context))
    }
}
