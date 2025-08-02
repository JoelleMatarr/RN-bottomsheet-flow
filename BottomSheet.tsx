import React from 'react';
import {
  Modal,
  View,
  Text,
  Dimensions,
  StyleSheet,
  Pressable,
} from 'react-native';

const SCREEN_HEIGHT = Dimensions.get('window').height;

type Props = {
  visible: boolean;
  onClose: () => void;
  children?: React.ReactNode;
};

const BottomSheet = ({ visible, onClose }: Props) => {
  return (
    <Modal visible={true} transparent animationType="none">
  <View style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.4)' }}>
    <View style={{
      position: 'absolute',
      bottom: 0,
      height: 200,
      backgroundColor: 'red',
      width: '100%',
      justifyContent: 'center',
      alignItems: 'center',
    }}>
      <Text style={{ color: 'white', fontSize: 24 }}>IT WORKS</Text>
    </View>
  </View>
</Modal>

  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  backdrop: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: '#00000066',
  },
  sheet: {
    backgroundColor: 'red',
    height: SCREEN_HEIGHT * 0.4,
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    padding: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default BottomSheet;
