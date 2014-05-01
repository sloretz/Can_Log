package edu.sjsu.canlog.app.backend;

import android.app.ExpandableListActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by shane on 3/11/14.
 * GUI calls this when it wants data
 */
public class Backend extends BluetoothService{
    private static Backend _this = null;

    private ArrayList<Integer> loggedDataPIDs;
    private ArrayList<Integer> liveDataPIDs;
    private ArrayList<Integer> aboutCarPIDs;


    //This is passed in to the fetch functions
    //The backend bluetooth thread calls this
    //when a result for the last query is gotten


    public Backend(Context context)
    {
        super(context);
        _this = this;

        loggedDataPIDs = new ArrayList<Integer>();
        liveDataPIDs = new ArrayList<Integer>();

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

        liveDataPIDs.add(0x4);
        liveDataPIDs.add(0x5);
        liveDataPIDs.add(0x6);
        liveDataPIDs.add(0x7);
        liveDataPIDs.add(0x8);
        liveDataPIDs.add(0x9);
        liveDataPIDs.add(0xa);
        liveDataPIDs.add(0xb);
        liveDataPIDs.add(0xc);
        liveDataPIDs.add(0xd);
        liveDataPIDs.add(0xe);
        liveDataPIDs.add(0xf);
        liveDataPIDs.add(0x10);
        liveDataPIDs.add(0x11);
        liveDataPIDs.add(0x1f);
        liveDataPIDs.add(0x21);
        liveDataPIDs.add(0x22);
        liveDataPIDs.add(0x23);
        liveDataPIDs.add(0x2c);
        liveDataPIDs.add(0x2d);
        liveDataPIDs.add(0x2e);
        liveDataPIDs.add(0x2f);
        liveDataPIDs.add(0x30);
        liveDataPIDs.add(0x31);
        liveDataPIDs.add(0x32);
        liveDataPIDs.add(0x33);
        liveDataPIDs.add(0x3c);
        liveDataPIDs.add(0x3d);
        liveDataPIDs.add(0x3e);
        liveDataPIDs.add(0x3f);
        liveDataPIDs.add(0x42);
        liveDataPIDs.add(0x43);
        liveDataPIDs.add(0x44);
        liveDataPIDs.add(0x45);
        liveDataPIDs.add(0x46);
        liveDataPIDs.add(0x47);
        liveDataPIDs.add(0x48);
        liveDataPIDs.add(0x49);
        liveDataPIDs.add(0x4a);
        liveDataPIDs.add(0x4b);
        liveDataPIDs.add(0x4c);
        liveDataPIDs.add(0x4d);
        liveDataPIDs.add(0x4e);
        liveDataPIDs.add(0x50);
        liveDataPIDs.add(0x52);
        liveDataPIDs.add(0x53);
        liveDataPIDs.add(0x54);
        //liveDataPIDs.add(0x55);
        //liveDataPIDs.add(0x56);
        //liveDataPIDs.add(0x57);
        //liveDataPIDs.add(0x58);
        liveDataPIDs.add(0x59);
        liveDataPIDs.add(0x5a);
        liveDataPIDs.add(0x5b);
        liveDataPIDs.add(0x5c);
        liveDataPIDs.add(0x5d);
        liveDataPIDs.add(0x5e);

    }

    protected void bt_writeln(String cmd) throws IOException
    {
        btWriter.write(cmd + "\r\n");
        btWriter.flush();
        //Log.d("Backend", "Wrote " + cmd);
    }

    protected String bt_readln() throws IOException
    {
        //readline, ignoring empty lines
        String nextLine = "";
        while (nextLine.equals(""))
            if (btReader.ready())
            {
                //Log.d("Backend", "r");
                nextLine = btReader.readLine();
            }
        //Log.d("Backend", "Read " + nextLine);
        return nextLine;
    }

    public boolean wasError(Bundle result)
    {
        String error = result.getString("error");
        if (error == null)
            return false;

        //Toast if an error happens
        Toast toast = Toast.makeText(mContext, error, Toast.LENGTH_LONG);
        toast.show();
        return true;
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
                    Iterator<Integer> pidIter = liveDataPIDs.iterator();
                    while (pidIter.hasNext()) {
                        Integer pid = pidIter.next();
                        pidList.add("x" + Integer.toHexString(pid));
                        prettyList.add(PrettyPID.getDescription(pid));
                        bt_writeln("pid " + pid);
                        dataList.add(bt_readln());
                    }
                    Log.d("Backend", "Finished running PID commands");
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


    public void setBoardTime(ResultHandler handler)
    {
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Bundle result = new Bundle();
                try {
                    Time now = new Time();
                    now.setToNow();
                    long milliSeconds = now.toMillis(false) / 1000;
                    bt_writeln("set " + Long.toString(milliSeconds));
                    bt_readln(); //Read but, we don't care
                } catch (IOException e)
                {
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
                //variable length return
                //End of data is ~/n
                Bundle result = new Bundle();
                ArrayList<String> DTCList = new ArrayList<String>();
                ArrayList<String> descList = new ArrayList<String>();
                try {
                    bt_writeln("dtc");
                    String nextLine = "";
                    while (true)
                    {
                        Log.d("Backend", "fetch DTC about readline");
                        nextLine = bt_readln();
                        Log.d("Backend", "Got dtc " + nextLine);
                        if (nextLine.equals("~"))
                            break;
                        DTCList.add(nextLine);
                        descList.add("TODO explanation");
                    }

                    Log.d("Backend", "fetch dtc post readlines");
                } catch (IOException e)
                {
                    result.putString("error", e.getLocalizedMessage());
                }
                result.putStringArrayList("DTCs", DTCList);
                result.putStringArrayList("short_descriptions", descList);
                return result;
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
                //tempResult.putString("VIN", "LJCPCBLCX11000237");
                //tempResult.putString("Fuel Type", "Diesel");
                ArrayList<String> carInfo = new ArrayList<String>();
                ArrayList<String> dataList = new ArrayList<String>();
                try {
                    carInfo.add("VIN");
                    bt_writeln("vin");
                    dataList.add(bt_readln());
                } catch (Exception e) {}
                try {
                    carInfo.add("Fuel Type");
                    bt_writeln("pid 51");
                    int fuelTypeInt=Integer.valueOf(bt_readln());
                    String fuelType;
                    switch (fuelTypeInt)
                    {
                        case 1:
                            fuelType = "Gasoline";
                            break;
                        case 2:
                            fuelType = "Methanol";
                            break;
                        case 3:
                            fuelType = "Ethanol";
                            break;
                        case 4:
                            fuelType = "Diesel";
                            break;
                        case 5:
                            fuelType = "LPG";
                            break;
                        case 6:
                            fuelType = "CNG";
                            break;
                        case 7:
                            fuelType = "Propane";
                            break;
                        case 8:
                            fuelType = "Electric";
                            break;
                        case 9:
                            fuelType = "Bifuel running Gasoline";
                            break;
                        case 10:
                            fuelType = "Bifuel running Methanol";
                            break;
                        case 11:
                            fuelType = "Bifuel running Ethanol";
                            break;
                        case 12:
                            fuelType = "Bifuel running LPG";
                            break;
                        case 13:
                            fuelType = "Bifuel running CNG";
                            break;
                        case 14:
                            fuelType = "Bifuel running Propane";
                            break;
                        case 15:
                            fuelType = "Bifuel running Electricity";
                            break;
                        case 16:
                            fuelType = "Bifuel running electric and combustion engine";
                            break;
                        case 17:
                            fuelType = "Hybrid gasoline";
                            break;
                        case 18:
                            fuelType = "Hybrid Ethanol";
                            break;
                        case 19:
                            fuelType = "Hybrid Diesel";
                            break;
                        case 20:
                            fuelType = "Hybrid Electric";
                            break;
                        case 21:
                            fuelType = "Hybrid running electric and combustion engine";
                            break;
                        case 22:
                            fuelType = "Hybrid Regenerative";
                            break;
                        case 23:
                            fuelType = "Bifuel running diesel";
                            break;
                        default:
                            fuelType = "idk";
                            break;
                    }
                    dataList.add(fuelType);
                } catch (Exception e) {}
                try {
                    carInfo.add("");
                } catch (Exception e) {}
                //dataList.add("Diesel");
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
                Bundle result = new Bundle();
                String data = "";
                String type = "";
                try {
                    bt_writeln("pid " + PrettyPID.toInteger(sensorHandle));
                    data = bt_readln();
                    type = PrettyPID.getType(sensorHandle);
                    if (type.equals("int"))
                        result.putInt(sensorHandle, Integer.valueOf(data));
                    else if (type.equals("double"))
                        result.putDouble(sensorHandle, Double.valueOf(data));
                } catch (IOException e)
                {
                    result.putString("error", e.getLocalizedMessage());
                }
                result.putString("type", type);
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
                    Log.d("Backend", "About to clear dtc");
                    bt_writeln("cdtc");
                    Log.d("Backend", "Cleared dtcs");
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
