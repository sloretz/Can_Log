package edu.sjsu.canlog.app.backend;

import android.os.Bundle;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Random;
import android.content.Context;

/**
 * Created by shane on 3/11/14.
 * GUI calls this when it wants data
 */
public class Backend extends BluetoothService{
    private static Backend _this = null;


    //This is passed in to the fetch functions
    //The backend bluetooth thread calls this
    //when a result for the last query is gotten


    public Backend(Context context)
    {
        super(context);
        _this = this;
    }

    //Singleton class, only one backend needed
    public static Backend getInstance()
    {
        return _this;
    }

    //Get a list of available sensors from the microcontroller
    public void fetchAvailableSensorsAndData(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Random r = new Random();
                //debug logic
                Bundle tempResult = new Bundle();
                ArrayList<String> sensorList = new ArrayList<String>();
                ArrayList<String> dataList = new ArrayList<String>();
                sensorList.add("RPM");
                dataList.add(Integer.toString(r.nextInt(5000) + 500));
                sensorList.add("Oxygen (1)");
                dataList.add(Integer.toString(r.nextInt(5) + 13) + "%");
                sensorList.add("Oxygen (2)");
                dataList.add(Integer.toString(r.nextInt(5) + 13) + "%");
                sensorList.add("Vehicle Speed");
                dataList.add(Integer.toString(r.nextInt(75)) + "mph");
                sensorList.add("Barometric Pressure");
                dataList.add(Double.toString(r.nextDouble() + 14) + "PSI");
                sensorList.add("Ambient Air Temperature");
                dataList.add(Integer.toString(r.nextInt(75) + 32) + "F");
                sensorList.add("Relative Throttle Position");
                dataList.add(Double.toString(r.nextDouble()));

                tempResult.putStringArrayList("Sensors", sensorList);
                tempResult.putStringArrayList("Data", dataList);
                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void waitForConnection(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                //No socket transfer,we're connected
                //when this function is called
                Bundle tempResult = new Bundle();
                tempResult.putBoolean("connected", true);
                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void fetchDTCs(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                //debug logic
                Bundle tempResult = new Bundle();
                ArrayList<String> DTCList = new ArrayList<String>();
                ArrayList<String> descList = new ArrayList<String>();
                DTCList.add("P0638");
                descList.add("Throttle Actuator Control Range");
                DTCList.add("P0720");
                descList.add("Output Speed Sensor Circuit Malfunction");
                tempResult.putStringArrayList("DTCs", DTCList);
                tempResult.putStringArrayList("short_descriptions", descList);
                return tempResult;
            }
        };
        task.execute(handler);
    }

    //Get car info from the microcontroller
    public void fetchCarInfo(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                //debug logic
                Bundle tempResult = new Bundle();
                tempResult.putString("VIN", "LJCPCBLCX11000237");
                tempResult.putString("Fuel Type", "Diesel");
                ArrayList<String> carInfo = new ArrayList<String>();
                ArrayList<String> dataList = new ArrayList<String>();
                carInfo.add("VIN");
                dataList.add("LJCPCBLCX11000237");
                carInfo.add("Fuel Type");
                dataList.add("Diesel");
                tempResult.putStringArrayList("carInfoNames", carInfo);
                tempResult.putStringArrayList("values", dataList);
                return tempResult;
            }
        };
        task.execute(handler);

    }

    public void fetchSensorData(final String sensorName, ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Random r = new Random();
                //debug logic
                Bundle tempResult = new Bundle();
                //SensorName should be enough to tell what type to return
                //here assume sensor name is a float
                tempResult.putString("type", "float");
                tempResult.putFloat(sensorName, r.nextFloat()*5f + 10f);
                return tempResult;
            }
        };
        task.execute(handler);

    }

    //Get a list of available sensors from the microcontroller
    public void sendClearDTCs(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                //debug logic
                Bundle tempResult = new Bundle();
                tempResult.putBoolean("did_clear",true);
                return tempResult;
            }
        };
        task.execute(handler);
    }
}
