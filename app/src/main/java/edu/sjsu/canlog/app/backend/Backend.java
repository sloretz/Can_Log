package edu.sjsu.canlog.app.backend;

import android.os.Bundle;
import java.util.ArrayList;

/**
 * Created by shane on 3/11/14.
 * GUI calls this when it wants data
 */
public class Backend {
    private static Backend _this = null;
    private Bundle lastResult;

    //This is passed in to the fetch functions
    //The backend bluetooth thread calls this
    //when a result for the last query is gotten
    public static abstract class ResultHandler {
        public abstract void gotResult(Bundle result);
    }

    private ResultHandler resultHandler;

    private Backend()
    {
        lastResult = null;
        resultHandler = null;
    }

    //Singleton class, only one backend needed
    public static Backend getInstance()
    {
        if (_this == null)
            _this = new Backend();
        return _this;
    }

    //Gets the last result when it is finished loading.
    //Returns NULL if the result hasn't finished loading
    public Bundle getResult()
    {
        return lastResult;
    }

    //Get a list of available sensors from the microcontroller
    public void fetchAvailableSensorsAndData(ResultHandler handler)
    {
        lastResult = null;
        resultHandler = handler;

        //TODO Begin Bluetooth transfer in another thread
        //That thread should call the result handler when
        //we get the data from the microcontroller
        //for now, just call the handler with some debug data
        Bundle tempResult = new Bundle();
        ArrayList<String> sensorList = new ArrayList<String>();
        ArrayList<String> dataList = new ArrayList<String>();
        sensorList.add("RPM");
        dataList.add("2200");
        sensorList.add("Oxygen (1)");
        dataList.add("15%");
        sensorList.add("Oxygen (2)");
        dataList.add("12%");
        sensorList.add("Vehicle Speed");
        dataList.add("54 mph");
        sensorList.add("Barometric Pressure");
        dataList.add("14.1 PSI");
        sensorList.add("Ambient Air Temperature");
        dataList.add("56 F");
        sensorList.add("Relative Throttle Position");
        dataList.add("0.4");
        tempResult.putStringArrayList("Sensors", sensorList);
        tempResult.putStringArrayList("Data", dataList);
        resultHandler.gotResult(tempResult);
    }
}
