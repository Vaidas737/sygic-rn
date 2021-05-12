import React from 'react';
import { SafeAreaView, StyleSheet, Text, } from 'react-native';
import Home from './src/Home';

const App = () => {
    return <SafeAreaView style={styles.app}>
        <Home />
    </SafeAreaView>;
};

const styles = StyleSheet.create({
    app: {
        flex: 1,
        backgroundColor: 'white'
    }
});

export default App;
