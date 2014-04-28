package edu.sjsu.canlog.app.backend;

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import android.content.Context;
import android.util.Log;

/**
 * Created by shane on 3/11/14.
 * GUI calls this when it wants data
 */
public class Backend extends BluetoothService{
    private static Backend _this = null;

    private ArrayList<Integer> loggedDataPIDs;


    //This is passed in to the fetch functions
    //The backend bluetooth thread calls this
    //when a result for the last query is gotten


    public Backend(Context context)
    {
        super(context);
        _this = this;

        loggedDataPIDs = new ArrayList<Integer>();
        loggedDataPIDs.add(0x3);
        loggedDataPIDs.add(0x4);
        loggedDataPIDs.add(0x5);
        loggedDataPIDs.add(0xc);
        loggedDataPIDs.add(0xd);
        loggedDataPIDs.add(0x11);
        loggedDataPIDs.add(0x1f);
        loggedDataPIDs.add(0x21);
        loggedDataPIDs.add(0x2f);
        loggedDataPIDs.add(0x30);
        loggedDataPIDs.add(0x31);
        loggedDataPIDs.add(0x4d);
        loggedDataPIDs.add(0x5c);
        loggedDataPIDs.add(0x5e);

    }

    protected void bt_writeln(String cmd) throws IOException
    {
        btWriter.write(cmd + "\r\n");
        btWriter.flush();
    }

    //Singleton class, only one backend needed
    public static Backend getInstance()
    {
        return _this;
    }

    //Get a list of available sensors from the microcontroller
    public void fetchAvailableSensorsAndData(ResultHandler handler)
    {
        Log.d("Backend", "Fetch available sensors and data called");
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Log.d("Backend", "Fetch avail sen and dat sock tran run");
                Bundle result = new Bundle();
                try {
                    ArrayList<String> pidList = new ArrayList<String>();
                    ArrayList<String> prettyList = new ArrayList<String>();
                    ArrayList<String> dataList = new ArrayList<String>();


                    Log.d("Backend", "Starting to run PID commands");
                    //Get the live data for supported PIDs
                    Iterator<Integer> pidIter = loggedDataPIDs.iterator();
                    while (pidIter.hasNext()) {
                        Integer pid = pidIter.next();
                        pidList.add("x" + Integer.toHexString(pid));
                        prettyList.add(PrettyPID.getDescription(pid));
                        bt_writeln("pid " + pid);
                        dataList.add(btReader.readLine());
                    }
                    Log.d("Backend", "got sen and dat res");
                    result.putStringArrayList("Sensors", prettyList);
                    result.putStringArrayList("PIDs", pidList);
                    result.putStringArrayList("Data", dataList);

                } catch (IOException e)
                {
                    Log.d("Backend", "Fetch sensors and data exception " + e.getLocalizedMessage());
                    result.putString("error", e.getLocalizedMessage());
                }
                return result;
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
                Log.d("Backend", "Wait for connection called");
                //No socket transfer,we're connected
                //when this function is called
                Bundle tempResult = new Bundle();
                tempResult.putBoolean("connected", true);
                Log.d("Backend", "wait for connection returned");
                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void fetchDTCs(ResultHandler handler)
    {
        Log.d("Backend", "Fetch dtcs called");
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {

                //TODO variable length return
                //End of data is ~/n
                Log.d("Backend", "fetch dtcs running");
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
                Log.d("Backend", "fetch dtcs returning");
                return tempResult;
            }
        };
        task.execute(handler);
    }

    //Get car info from the microcontroller
    public void fetchCarInfo(ResultHandler handler)
    {
        Log.d("Backend", "Fetch car info called");
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                //TODO fetch car info PIDs
                Log.d("Backend", "Fetch car info running");
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
                Log.d("Backend", "fetch car info returning");
                return tempResult;
            }
        };
        task.execute(handler);

    }

    public void fetchSensorData(final String sensorHandle, ResultHandler handler)
    {
        Log.d("Backend", "Fetch sensor data called");
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Log.d("Backend", "Fetch sensor data running");
                /*
                Random r = new Random();
                //debug logic
                Bundle tempResult = new Bundle();
                //SensorName should be enough to tell what type to return
                //here assume sensor name is a float
                tempResult.putString("type", "float");
                tempResult.putFloat(sensorName, r.nextFloat()*5f + 10f);
                return tempResult;
                */
                Bundle result = new Bundle();
                try {

                    bt_writeln("pid " + PrettyPID.toInteger(sensorHandle));
                    String data = btReader.readLine();
                    String type = PrettyPID.getType(sensorHandle);
                    result.putString("type", type);
                    if (type == "int")
                        result.putInt(sensorHandle, Integer.valueOf(data));
                    else if (type == "double")
                        result.putDouble(sensorHandle, Double.valueOf(data));
                    else if (type == "float")
                        result.putFloat(sensorHandle, Float.valueOf(data));

                } catch (IOException e)
                {
                    result.putString("error", e.getLocalizedMessage());
                }
                Log.d("Backend", "fetch sensor data returning");
                return result;
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
                Bundle result = new Bundle();
                try {
                    bt_writeln("cdtc");
                    //No response from the car, just assume it worked
                    result.putBoolean("did_clear", true);
                } catch (IOException ioe) {
                    Log.e("Backend task", "connect but got exception " + ioe.getMessage());
                    result.putBoolean("did_clear", false);
                }
                Log.d("Backend", "send clear dtcs returning");
                return result;
            }
        };
        Log.d("Backend", "Executing BluetoothTask for clear dtcs");
        task.execute(handler);
    }

    public void fetchLoggedVINs(ResultHandler handler)
    {
        //
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Log.d("Backend", "fetch logged VINs running");
                Random r = new Random();
                //debug logic
                Bundle tempResult = new Bundle();
                ArrayList<String> vinList = new ArrayList<String>();
                vinList.add("LJCPCBLCX11000237");
                vinList.add("JBMHSRLCX11999567");
                tempResult.putStringArrayList("VIN", vinList);
                Log.d("Backend", "fetch logged VINs returning");
                return tempResult;
            }
        };
        task.execute(handler);
    }

    public void fetchLoggedPIDs(String vin, ResultHandler handler)
    {
        //Don't need to ask the microcontroller about this one
        //it's hard coded here
        //values taken from http://en.wikipedia.org/wiki/OBD-II_PIDs
        Log.d("Backend", "fetch logged PIDs called");
        Random r = new Random();
        Bundle tempResult = new Bundle();
        ArrayList<String> pidList = new ArrayList<String>();
        ArrayList<String> prettyList = new ArrayList<String>();

        Iterator<Integer> pidIter = loggedDataPIDs.iterator();
        while (pidIter.hasNext()) {
            Integer pid = pidIter.next();
            pidList.add("x" + Integer.toHexString(pid));
            prettyList.add(PrettyPID.getDescription(pid));
        }
        tempResult.putStringArrayList("PID", pidList);
        tempResult.putStringArrayList("Desc", prettyList);
        handler.gotResult(tempResult);
        Log.d("Backend", "fetch logged PIDs returning");
    }

    public void beginHistoryDownload(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Log.d("Backend", "begin history download running");
                Random r = new Random();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    //uh yeah, this is debug code
                }
                //TODO there is a way to do
                //progress with AsyncTask, It's what
                //they're designed for. Figure it out.
                //debug logic
                Bundle tempResult = new Bundle();
                tempResult.putBoolean("done", true);
                Log.d("Backend", "begin history download returning");
                return tempResult;
            }
        };
        task.execute(handler);
    }
}
