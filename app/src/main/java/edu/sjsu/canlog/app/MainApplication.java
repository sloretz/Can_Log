package edu.sjsu.canlog.app;

import android.app.Application;
import android.util.Log;

import edu.sjsu.canlog.app.backend.Backend;

/**
 * Created by shane on 5/4/14.
 * Main Application
 */
public class MainApplication extends Application {
    @Override
    public void onTerminate()
    {
        Log.d("Application", "Main application terminating");

        super.onTerminate();

    }

}
