//
//  FlowViewManager.swift
//  FlowReactNativeApp
//
//  Created by Joelle Matar on 06/05/2025.
//



import Foundation
import UIKit
import SwiftUI
import React
import CheckoutComponentsSDK

@objc(FlowView)
class FlowViewManager: RCTViewManager {
  override static func requiresMainQueueSetup() -> Bool { true }

  override func view() -> UIView! {
    return CheckoutFlowNativeView()
  }

  @objc func setPaymentSessionID(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, viewRegistry in
      if let view = viewRegistry?[reactTag] as? CheckoutFlowNativeView {
        view.paymentSessionID = value as String
      }
    }
  }

  @objc func setPaymentSessionSecret(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, viewRegistry in
      if let view = viewRegistry?[reactTag] as? CheckoutFlowNativeView {
        view.paymentSessionSecret = value as String
      }
    }
  }

  @objc func setPublicKey(_ reactTag: NSNumber, value: NSString) {
    bridge.uiManager.addUIBlock { _, viewRegistry in
      if let view = viewRegistry?[reactTag] as? CheckoutFlowNativeView {
        view.publicKey = value as String
      }
    }
  }
}

@available(iOS 13.0, *)
class CheckoutFlowNativeView: UIView {
  var paymentSessionID: String? { didSet { renderFlowIfNeeded() } }
  var paymentSessionSecret: String? { didSet { renderFlowIfNeeded() } }
  var publicKey: String? { didSet { renderFlowIfNeeded() } }

  private var hostingController: UIHostingController<AnyView>?

  private func renderFlowIfNeeded() {
    guard let id = paymentSessionID,
          let secret = paymentSessionSecret,
          let pubKey = publicKey else {
      print("❌ Missing Flow parameters")
      return
    }

    let paymentSession = PaymentSession(id: id, paymentSessionSecret: secret)

    Task {
      do {
        let config = try await CheckoutComponents.Configuration(
          paymentSession: paymentSession,
          publicKey: pubKey,
          environment: .sandbox,
          callbacks: .init(
            onSuccess: { _, paymentID in
              print("✅ Payment success: \(paymentID)")
            },
            onError: { error in
              print("❌ Payment error: \(error.localizedDescription)")
            }
          )
        )

        let checkout = CheckoutComponents(configuration: config)
        let component = try await checkout.create(.flow(options: [
          .applePay(merchantIdentifier: "merchant.com.flowmobile.checkout")
        ]))

        if component.isAvailable {
          let controller = await UIHostingController(rootView: component.render())
          DispatchQueue.main.async {
            self.hostingController?.view.removeFromSuperview()
            controller.view.frame = self.bounds
            controller.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            self.addSubview(controller.view)
            self.hostingController = controller
          }
        } else {
          print("❌ Flow component not available")
        }

      } catch {
        print("❌ Flow render failed: \(error)")
      }
    }
  }
}
