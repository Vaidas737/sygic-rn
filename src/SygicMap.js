import React, { PureComponent } from 'react';
import { requireNativeComponent, findNodeHandle, UIManager, StyleSheet, } from 'react-native';

const SygicMapNativeView = requireNativeComponent('SygicMapViewManager');

export default class SygicMapNative extends PureComponent {

    nativeComponentRef = null;

	componentDidMount() {
		const androidViewId = findNodeHandle(this.nativeComponentRef);

		UIManager.dispatchViewManagerCommand(
			androidViewId,
			UIManager.SygicMapViewManager.Commands.create.toString(),
			[
				androidViewId,
                Boolean(this.props.onStarted),
			]
		);
	}

	componentWillUnmount() {
	}

	render() {
		return <SygicMapNativeView 
			style={styles.wrapper}
			ref={(nativeRef) => this.nativeComponentRef = nativeRef}
			onStarted={this.props.onStarted}
		/>
	}
}

const styles = StyleSheet.create({
	wrapper: {
		flex: 1,
	}
})
