package edu.sjsu.canlog.app.backend;

import android.os.Bundle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import edu.sjsu.canlog.app.frontend.GraphValue;

/**
 * Created by shane on 3/11/14.
 * GUI calls this when it wants data
 */
public class Backend extends BluetoothService{
    private static Backend _this = null;

    private ArrayList<Integer> loggedDataPIDs;
    private ArrayList<Integer> liveDataPIDs;
    private ArrayList<Integer> aboutCarPIDs;
    private ArrayList<Integer> supportedPIDs;


    //This is passed in to the fetch functions
    //The backend bluetooth thread calls this
    //when a result for the last query is gotten


    public Backend(Context context)
    {
        super(context);
        _this = this;

        loggedDataPIDs = new ArrayList<Integer>();
        liveDataPIDs = new ArrayList<Integer>();
        supportedPIDs = new ArrayList<Integer>();

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

        //pids supported 1-20
        //190,63,168,19
        //10111110 00111111 10101000 00010011

        //pids supported 21-40
        //144,21,176,21
        //10010000 00010101 10110000 00010101

        //pids supported 41-60
        //250,28,32,0
        //11111010 00011100 00100000 00000000

        liveDataPIDs.add(0x4); //Calculated engine load value
        liveDataPIDs.add(0x5); //Engine coolant temperature
        liveDataPIDs.add(0x6);
        liveDataPIDs.add(0x7); //Long term fuel % trim—Bank 1
        liveDataPIDs.add(0x8);
        liveDataPIDs.add(0x9);
        liveDataPIDs.add(0xa);
        liveDataPIDs.add(0xb);
        liveDataPIDs.add(0xc); //Engine RPM
        liveDataPIDs.add(0xd); //Vehicle speed
        liveDataPIDs.add(0xe); //Timing advance
        liveDataPIDs.add(0xf);
        liveDataPIDs.add(0x10);
        liveDataPIDs.add(0x11);
        liveDataPIDs.add(0x1f);
        liveDataPIDs.add(0x21); //Distance traveled with malfunction indicator lamp (MIL) on
        liveDataPIDs.add(0x22);
        liveDataPIDs.add(0x23);
        liveDataPIDs.add(0x2c); //Commanded EGR
        liveDataPIDs.add(0x2d);
        liveDataPIDs.add(0x2e); //Commanded evaporative purge
        liveDataPIDs.add(0x2f);
        liveDataPIDs.add(0x30); //# of warm-ups since codes cleared
        liveDataPIDs.add(0x31); //Distance traveled since codes cleared
        liveDataPIDs.add(0x32);
        liveDataPIDs.add(0x33); //Barometric pressure
        liveDataPIDs.add(0x3c); //Catalyst Temperature
        liveDataPIDs.add(0x3d);
        liveDataPIDs.add(0x3e); //Catalyst Temperature
        liveDataPIDs.add(0x3f);
        liveDataPIDs.add(0x42); //Control module voltage
        liveDataPIDs.add(0x43); //Absolute load value
        liveDataPIDs.add(0x44); //Command equivalence ratio
        liveDataPIDs.add(0x45); //Relative throttle position
        liveDataPIDs.add(0x46);
        liveDataPIDs.add(0x47); //Absolute throttle position B
        liveDataPIDs.add(0x48);
        liveDataPIDs.add(0x49);
        liveDataPIDs.add(0x4a);
        liveDataPIDs.add(0x4b);
        liveDataPIDs.add(0x4c); //Commanded throttle actuator
        liveDataPIDs.add(0x4d); //Time run with MIL on
        liveDataPIDs.add(0x4e); //Time since trouble codes cleared
        liveDataPIDs.add(0x50);
        liveDataPIDs.add(0x52);
        liveDataPIDs.add(0x53); //Absolute Evap system Vapor Pressure
        liveDataPIDs.add(0x54);
        liveDataPIDs.add(0x55);
        liveDataPIDs.add(0x56);
        liveDataPIDs.add(0x57);
        liveDataPIDs.add(0x58);
        liveDataPIDs.add(0x59);
        liveDataPIDs.add(0x5a);
        liveDataPIDs.add(0x5b);
        liveDataPIDs.add(0x5c);
        liveDataPIDs.add(0x5d);
        liveDataPIDs.add(0x5e);
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Log.d("Backend", "Fetch avail sen and dat sock tran run");
                Bundle result = new Bundle();
                try {
                    bt_writeln("pid 00");
                    long PIDs= Long.valueOf(bt_readln(),16);
                    //start finding them from 19 to 1, because its easier to declare to 2 and bit shift that way
                    int PIDsComparator =2;
                    int PIDadder=0x1F;
                    while(PIDsComparator != 0)
                    {
                        if((PIDs & PIDsComparator) != 0)
                        {
                            liveDataPIDs.add(PIDadder);
                        }
                        --PIDadder;
                        PIDsComparator=PIDsComparator << 1;
                    }
                    bt_writeln("pid 32");
                    PIDs= Long.valueOf(bt_readln(),16);
                    PIDsComparator =2;
                    PIDadder=0x3F;
                    while(PIDsComparator != 0)
                    {
                        if((PIDs & PIDsComparator) != 0)
                        {
                            liveDataPIDs.add(PIDadder);
                        }
                        --PIDadder;
                        PIDsComparator=PIDsComparator << 1;
                    }
                    bt_writeln("pid 64");
                    PIDs= Long.valueOf(bt_readln(),16);
                    PIDsComparator =2;
                    PIDadder=0x5F;
                    while(PIDsComparator != 0)
                    {
                        if((PIDs & PIDsComparator) != 0)
                        {
                            liveDataPIDs.add(PIDadder);
                        }
                        --PIDadder;
                        PIDsComparator=PIDsComparator << 1;
                    }

                } catch (IOException e)
                {
                    Log.d("Backend", "Fetch sensors and data exception " + e.getLocalizedMessage());
                    result.putString("error", e.getLocalizedMessage());
                }
                return null;
            }
        };
        task.execute((ResultHandler)null);

    }

    protected void bt_writeln(String cmd) throws IOException
    {
        btWriter.write(cmd + "\r\n");
        btWriter.flush();
        Log.d("Backend", "->> " + cmd);
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
        Log.d("Backend", "<<- " + nextLine);
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
                        boolean isSupported = false;
                        Iterator<Integer> supportedIterator = supportedPIDs.iterator();
                        while(supportedIterator.hasNext())
                        {
                            if(pid == supportedIterator.next())
                            {
                                isSupported=true;
                                break;
                            }
                        }
                        if(isSupported == false)
                        {
                            continue;
                        }
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
                    bt_writeln("pid 81");
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
                            fuelType = "IDK";
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

    public void queryHistoryForPID(final String VIN, final String PID, final long startDate, final long endDate, ResultHandler handler)
    {
        //Yes this uses a bluetooth task, but no
        //it does not use bluetooth. The reason for
        //this is BluetoothTask handles posting the
        //result onto the UI thread as well.
        BluetoothTask task = new BluetoothTask() {
            @Override
            protected Bundle doSocketTransfer()
            {
                Bundle result = new Bundle();
                //Use DatabaseHandler
                DatabaseHandler h = new DatabaseHandler(mContext, VIN);
                ArrayList<SQLdata> data = (ArrayList<SQLdata>) h.getAllDataRange(PID, startDate, endDate);
                ArrayList<GraphValue> retValues = new ArrayList<GraphValue>();
                /*
                Iterator<SQLdata> rowIter = data.iterator();
                while (rowIter.hasNext())
                {
                    SQLdata row = rowIter.next();
                    retValues.add(new GraphValue(row.time));
                    retValues.add(new GraphValue(row.data));
                }*/
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(1));
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(2));
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(3));
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(2));
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(1));
                retValues.add(new GraphValue(1399155529));
                retValues.add(new GraphValue(0));
                result.putParcelableArrayList("values", retValues);
                return result;
            }
        };
        task.execute(handler);
    }
}
