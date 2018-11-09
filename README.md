
# react-native-face-detection

## Getting started

`$ npm install react-native-face-detection --save`

### Mostly automatic installation

`$ react-native link react-native-face-detection`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-face-detection` and add `RNFaceDetection.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFaceDetection.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.facedetection.RNFaceDetectionPackage;` to the imports at the top of the file
  - Add `new RNFaceDetectionPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-face-detection'
  	project(':react-native-face-detection').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-face-detection/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-face-detection')
  	```


## Usage
```javascript
import RNFaceDetection from 'react-native-face-detection';

// TODO: What to do with the module?
RNFaceDetection;
```
  