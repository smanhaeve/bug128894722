package com.manhaeve.bug128894722;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class A11yService extends AccessibilityService {
    private static final String TAG = "a11yService";
    private EventLog mEventLog = null;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "Accessibility service connected");
        if (mEventLog == null) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                mEventLog = new EventLog();
            else
                mEventLog = new EventLog(EventLog.getOutputFile(this, "a11yservice"));
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Accessibility service disconnected");
        if (mEventLog != null) {
            mEventLog.close();
            mEventLog = null;
        }
        return false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (mEventLog != null) mEventLog.push(event);
        else Log.d(TAG, "accessibility event: " + event);
    }

    @Override
    public void onInterrupt() {
    }
}
