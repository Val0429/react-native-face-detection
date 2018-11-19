package com.facedetection;

import android.app.Activity;

/**
 * Created by Neo on 2018/11/9.
 */

public class MainApplication {
    private static Activity mActivity;

    public static void setCurrentActivity(Activity activity) {
        mActivity = activity;
    }

    public static Activity getCurrentActivity() {
        return mActivity;
    }
}
