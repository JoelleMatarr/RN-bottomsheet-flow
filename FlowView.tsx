import { requireNativeComponent } from 'react-native';
import React from 'react';
import { ViewStyle } from 'react-native';

const NativeFlowView = requireNativeComponent<any>('FlowView');

type Props = {
  style?: ViewStyle;
  paymentSessionID: string;
  paymentSessionSecret: string;
  publicKey: string;
};

const FlowView = (props: Props) => {
  return <NativeFlowView {...props} />;
};

export default FlowView;
