import React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import FlowView from './FlowView';

const App = () => {
  return (
    <SafeAreaView style={styles.container}>
      <FlowView
        style={styles.flow}
        paymentSessionID="ps_2vN0SHF9Air1PpPmCaq4S6oSvZ4"
        paymentSessionSecret="pss_6386e01a-08ec-4421-baf4-61188c84320d"
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
