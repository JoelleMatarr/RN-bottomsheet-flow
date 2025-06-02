#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(FlowView, RCTViewManager)
RCT_EXPORT_VIEW_PROPERTY(paymentSessionID, NSString)
RCT_EXPORT_VIEW_PROPERTY(paymentSessionSecret, NSString)
RCT_EXPORT_VIEW_PROPERTY(publicKey, NSString)
@end