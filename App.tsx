import React from 'react';
import { SafeAreaView, StyleSheet, Button, NativeModules, View } from 'react-native';

// You need to import the native module by the name you give it in Kotlin.
const { CheckoutModule } = NativeModules;

const App = () => {
  const handleCheckoutPress = () => {
    CheckoutModule.showCheckoutBottomSheet(
      "ps_30jAfHVW2WAexjUrmTeuuwxEvQ6",
      "pss_d5e102b5-3eb5-4725-b039-b009925cf7e5",
      "pk_sbox_cwlkrqiyfrfceqz2ggxodhda2yh"
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.buttonContainer}>
        <Button
          title="Start Checkout"
          onPress={handleCheckoutPress}
          color="#EA5D29" // A touch of branding
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center', // Center the button horizontally
    backgroundColor: '#eee',
  },
  buttonContainer: {
    width: 200,
  },
});

export default App;