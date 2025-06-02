import React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import FlowView from './FlowView';

const App = () => {
  return (
    <SafeAreaView style={styles.container}>
      <FlowView
        style={styles.flow}
        paymentSessionID="ps_2xwINLAMMgCus8w9UEwqHsaRWEe"
        paymentSessionSecret="pss_89eed584-6525-435d-82c1-d2011960b372"
        publicKey="pk_sbox_cwlkrqiyfrfceqz2ggxodhda2yh"
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#eee',
  },
  flow: {
    width: '100%',
    height: 600,
    borderWidth: 2,
    borderColor: 'red',
  },
});

export default App;
