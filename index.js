
import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  Button,
  Switch,
  Image,
  Dimensions,
  View,
  Alert,
  NativeModules,
  requireNativeComponent,
  findNodeHandle,
  AppRegistry
} from 'react-native';

const UIManager = NativeModules.UIManager;
const DispatchManager = UIManager.dispatchViewManagerCommand;
const Commands = UIManager.FaceDetectionView.Commands;

let NativeVideoView = requireNativeComponent('FaceDetectionView', FaceDetectionView);

export class FaceDetectionView extends React.Component {
    constructor(props) {
        console.log('view create');
        super(props);
    }

    shouldComponentUpdate() {
        return false;
    }

    start(front) {
        /// 1: front
        /// 0: back
        DispatchManager(findNodeHandle(this.refs.native), Commands.what, ['Start', front ? true : false]);
    }

    stop() {
        DispatchManager(findNodeHandle(this.refs.native), Commands.what, ['Stop']);
    }

    setFacingFront(front) {
        DispatchManager(findNodeHandle(this.refs.native), Commands.what, ['SetFacingFront', front ? true : false])
    }
    
    enableDebugInfo(enable) {
        DispatchManager(findNodeHandle(this.refs.native), Commands.what, ['EnableDebugInfo', enable])
    }

    render() {
        console.log('view render');
        return (
            <NativeVideoView
                ref="native"
                {...this.props}
                />
        );
    }
}

AppRegistry.registerComponent('FaceDetectionView', () => FaceDetectionView);