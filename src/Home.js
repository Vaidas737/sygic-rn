import React from 'react';
import { SafeAreaView, StyleSheet, Text, View, } from 'react-native';
import SygicMapNative from './SygicMap';

const Home = () => {
  return <SafeAreaView style={styles.wrapper}>
        <View style={styles.titleWrapper}>
            <Text style={styles.title}>Sygic RN</Text>
        </View>
        <SygicMapNative />
  </SafeAreaView>;
};

const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
    },
    titleWrapper: {
        height: 100,
        backgroundColor: 'rgba(252, 200, 0, 1)',
        justifyContent: 'center',
        alignItems: 'center',
    },
    title: {
        fontSize: 22,
        lineHeight: 28,
        fontWeight: 'bold',
    }
});

export default Home;
