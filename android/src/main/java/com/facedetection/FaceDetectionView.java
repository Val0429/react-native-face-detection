package com.facedetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Base64;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
//import com.facedetection.MainApplication;
import com.isap.libisapcamera.AutoFitTextureView;
import com.isap.libisapcamera.CameraCapture;
import com.isap.libisapcamera.CameraCaptureCallback;
import com.isap.libisapfacedetector.FaceDetectManager;
import com.isap.libisapfacedetector.FaceDetectorPoolCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Neo on 2018/1/29.
 */

public class FaceDetectionView extends FrameLayout implements CameraCaptureCallback, FaceDetectorPoolCallback {
    private static final String TAG = "FaceDetectionView";

    private static final String SNAPSHOT_FOLDER = /*FileUtils.getDiskCacheDir(MainApplication.getAppContext()) +*/ "/snapshot/";

    private static int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Context mContext;
    private Activity mActivity;
    private boolean mSaveDetectedInFile = false;
    private boolean mShowDebugInfo = false;
    private int mCheckTime = 1000;
    private long mLastUpdateTime = 0;

    private CameraCapture m_capture;
    private FaceDetectManager m_pool = null;
    private AutoFitTextureView m_textureView;

    private static final Object m_locker = new Object();

    public void sentNativeEvent(WritableMap event, String caller)
    {
        event.putString("caller", caller);
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onNativeCallback",
                event);
    }

    public FaceDetectionView(Context context) {
        super(context);

        mContext = context;
        mActivity = /*RNFaceDetectionPackage.mActivity;*/MainApplication.getCurrentActivity();
        initView();
        initCamera();
        initDetector();
    }

    private void initView() {
        m_textureView = new AutoFitTextureView(mContext);
        addView(m_textureView);
    }

    private void initCamera() {
        m_capture = new CameraCapture(mActivity, this, m_textureView);
    }

    private void initDetector() {
        m_pool = new FaceDetectManager(/*FileUtils.getDiskCacheDir(MainApplication.getAppContext()) + File.separator + */"shape_predictor_68_face_landmarks.dat", this);
    }

    @Override
    public void OnImageAvailableListener(Bitmap bmp) {
        m_pool.add(bmp);
    }

    @Override
    public void FoundFace(ArrayList<Bitmap> faces) {
        if (faces.size() == 0)
            return;

        if (System.currentTimeMillis() - mLastUpdateTime < mCheckTime)
            return;

        NotifyFaceDetected(faces.get(0));

        mLastUpdateTime = System.currentTimeMillis();
    }

    public void startCameraSource(boolean facingFront) {
        m_pool.startDetect();
        m_capture.startCapture(facingFront);
    }

    public void startCameraSource() {
        m_pool.startDetect();
        m_capture.startCapture();
    }

    public void stopCameraSource() {
        m_capture.stopCapture();
        m_pool.stopDetect();
    }

    public void setCameraFacing(boolean facingFront) {
        synchronized (m_locker) {
            m_capture.stopCapture();
            m_capture.startCapture(facingFront);
        }
    }

    public void setCheckTime(int checkTime) {
        mCheckTime = checkTime;
    }

    public void EnableDebugInfo(boolean enable) {
        mShowDebugInfo = enable;
        m_pool.enableDebug(mShowDebugInfo);
    }

//    private boolean NotifyOriginalImage(Bitmap bmp) {
//        boolean ret = false;
//
//        if (mSaveDetectedInFile) {
//
//        }
//        else {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            WritableMap event2 = Arguments.createMap();
//            event2.putString("image2", Base64.encodeToString(byteArray, Base64.DEFAULT));
//            sentNativeEvent(event2, "FaceDetected");
//            ret = true;
//        }
//
//        return ret;
//    }

    private boolean NotifyFaceDetected(Bitmap bmp) {
        boolean ret = false;

        if (mSaveDetectedInFile) {
            try {
                File filePath = new File(SNAPSHOT_FOLDER);
                if (!filePath.exists()) {
                    filePath.mkdir();
                }
                java.util.Date date = new java.util.Date();
                String fileName = new Timestamp(date.getTime()).toString();
                fileName = fileName.replace(":", "").replace("-", "").replace(" ", "").replace(".", "") + ".jpg";
                File file = new File(SNAPSHOT_FOLDER, fileName);

                FileOutputStream stream = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
                stream = null;

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                mContext.sendBroadcast(mediaScanIntent);

                WritableMap event = Arguments.createMap();
                event.putString("uri", SNAPSHOT_FOLDER + "/" + fileName);
                sentNativeEvent(event, "FaceDetected");

                ret = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            WritableMap event2 = Arguments.createMap();
            event2.putString("image", Base64.encodeToString(byteArray, Base64.DEFAULT));
            sentNativeEvent(event2, "FaceDetected");
            ret = true;
        }

        return ret;
    }
}
