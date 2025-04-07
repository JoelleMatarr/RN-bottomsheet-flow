import React from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import FlowView from './FlowView';

const App = () => {
  return (
    <SafeAreaView style={styles.container}>
      <FlowView
        style={styles.flow}
        paymentSessionID="ps_2vObsNsx8L81kMGqFRSQfeOpWQ4"
        paymentSessionSecret="pss_2f6d03ed-98aa-4617-ae20-7c85bccf94d8"
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
