package edu.sjsu.canlog.app;

import android.app.Application;

import edu.sjsu.canlog.app.backend.Backend;

/**
 * Created by shane on 5/4/14.
 */
public class MainApplication extends Application {
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Backend backend = Backend.getInstance();
        backend.stop();
    }

}
