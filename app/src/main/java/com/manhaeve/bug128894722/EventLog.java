package com.manhaeve.bug128894722;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class EventLog {
    private static final String TAG = "EventLog";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.US);


    private BufferedWriter mOut;
    private final File mOutFile;


    EventLog(@NonNull File outFile) {
        mOutFile = outFile;
    }

    EventLog() {
        mOutFile = null;
    }

    private void open() {
        if (mOut != null || mOutFile == null) return;
        try {
            mOut = new BufferedWriter(new FileWriter(mOutFile, true));
        } catch (IOException e) {
            Log.e(TAG, "Failed to open event log file '" + mOutFile + ": " + e.getMessage());
        }
    }

    void push(AccessibilityEvent event) {
        if (event == null) return;
        push(event.toString());
    }

    void push(String string) {
        if (string == null) return;
        Log.d(TAG, "Accessibility event: " + string);
        if (mOut == null) open();
        if (mOut == null) return;
        try {
            mOut.write(getEventTimestamp());
            mOut.write(" - ");
            mOut.write(string);
            mOut.write('\n');
        } catch (IOException e) {
            Log.e(TAG, "Failed to write event to log", e);
        }
    }

    void close() {
        if (mOut != null) {
            try {
                mOut.flush();
                mOut.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close output file '" + mOutFile + "'");
            }
        }
    }

    private static File getOutputFile(@NonNull Context context, @Nullable String baseFolder, @NonNull String fileRoot, @NonNull String extension, boolean overwrite) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        final File mediaStorageDir;
        //noinspection StringEquality
        if (Environment.DIRECTORY_PICTURES == baseFolder ||
                Environment.DIRECTORY_MOVIES == baseFolder ||
                Environment.DIRECTORY_MUSIC == baseFolder ||
                Environment.DIRECTORY_PODCASTS == baseFolder ||
                Environment.DIRECTORY_RINGTONES == baseFolder ||
                Environment.DIRECTORY_ALARMS == baseFolder ||
                Environment.DIRECTORY_NOTIFICATIONS == baseFolder ||
                Environment.DIRECTORY_DOWNLOADS == baseFolder ||
                Environment.DIRECTORY_DCIM == baseFolder ||
                Environment.DIRECTORY_DOCUMENTS == baseFolder) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(baseFolder),
                        "bug128894722" + File.separator + "eventlogs");
            } else {
                Log.e(TAG, "no external storage available");
                return null;
            }
        } else if (baseFolder != null){
            mediaStorageDir = new File(context.getFilesDir(), "bug128894722" + File.separator + baseFolder);
        } else {
            mediaStorageDir = new File(context.getFilesDir(), "bug128894722");
        }
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name that doesn't exist yet, only try a few times.
        File mediaFile;
        final int maxTries = 100;
        int counter = 0;
        do {
            final String filename = fileRoot + (counter == 0 ? "" : "_" + counter)  + extension;
            mediaFile = new File(mediaStorageDir, filename);
            if (! mediaFile.exists() || overwrite) return mediaFile;
        } while ((counter++) < maxTries);

        return null;
    }

    static File getOutputFile(@NonNull Context context, String detail) {
        return getOutputFile(context, Environment.DIRECTORY_DOCUMENTS, getSortableTimestamp() + "_" + detail, ".log", false);
    }

    private static String getSortableTimestamp() {
        return DATE_FORMAT.format(new Date());
    }

    private static String getEventTimestamp() {
        return TIME_FORMAT.format(new Date());
    }
}
