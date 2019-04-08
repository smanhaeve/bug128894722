package com.manhaeve.bug128894722;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public static final int DELAY_MILLIS = 1000;

    private final Handler mHandler = new Handler();
    private Button mButtonView;
    private boolean mPaused = true;
    private EventLog mEventLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonView = findViewById(R.id.btn);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            mEventLog = new EventLog();
        else
            mEventLog = new EventLog(EventLog.getOutputFile(this, "activity"));
    }

    @Override
    protected void onPause() {
        mPaused = true;
        mHandler.removeCallbacks(mToggler);
        if (mEventLog != null) mEventLog.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mToggler, DELAY_MILLIS);
        mPaused = false;
    }

    private final Runnable mToggler = new Runnable() {
        @Override
        public void run() {
            if (mPaused) return;
            final View view = mButtonView;
            if (view != null) {
                if (mEventLog != null) mEventLog.push("toggling button " + (view.getVisibility() == View.VISIBLE ? "VISIBLE -> INVISIBLE" : "INVISIBLE -> VISIBLE"));
                // note: Enabling this line will fire the expected accessibility event.
                // view.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
                view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                // note: Even when this line is enabled, no accessibility event gets through to the a11y service.
                // view.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
            }
            mHandler.postDelayed(this, DELAY_MILLIS);
        }
    };
}
