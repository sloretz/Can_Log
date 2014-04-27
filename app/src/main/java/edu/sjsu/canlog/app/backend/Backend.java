package edu.sjsu.canlog.app.backend;

import android.os.Bundle;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import android.content.Context;
import android.util.Log;

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
                Bundle tempResult = new Bundle();
                try {
                    Log.d("mConnectedSocket", (mConnectedSocket == null) ? "is null" : "is not null");
                    InputStream is = mConnectedSocket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    OutputStream os = mConnectedSocket.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                    Log.i("DTC", "about to send ping");
                    bw.write("ping");
                    bw.flush();
                    Log.i("dtc", "about to read line");
                    String input = br.readLine();
                    Log.i("dtc Got BT data", input);

                    tempResult.putBoolean("did_clear",true);
                } catch (IOException ioe) {
                    Log.e("DTC task", "connect but got exception " + ioe.getMessage());
                    tempResult.putBoolean("did_clear",false);
                }

                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void fetchLoggedVINs(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Random r = new Random();
                //debug logic
                Bundle tempResult = new Bundle();
                ArrayList<String> vinList = new ArrayList<String>();
                vinList.add("LJCPCBLCX11000237");
                vinList.add("JBMHSRLCX11999567");
                tempResult.putStringArrayList("VIN", vinList);
                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void beginHistoryDownload(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Random r = new Random();

                //TODO there is a way to do
                //progress with AsyncTask, It's what
                //they're designed for. Figure it out.
                //debug logic
                Bundle tempResult = new Bundle();
                tempResult.putBoolean("done", true);
                return tempResult;
            }
        };
        task.execute(handler);
    }
}
