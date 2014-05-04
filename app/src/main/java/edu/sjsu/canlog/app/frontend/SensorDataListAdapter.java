package edu.sjsu.canlog.app.frontend;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 2/17/14.
 * Adapter for sensors
 */
public class SensorDataListAdapter implements ListAdapter {

    public class SensorStruct {
        public SensorStruct(String n, String d, String ud) {sensorName = n; sensorData = d; userData = ud;}
        public String sensorName;
        public String sensorData;
        public String userData;
    }

    private ArrayList<DataSetObserver> observers;
    private ArrayList<SensorStruct> sensors;
    private Context context;

    public SensorDataListAdapter(Context ctx)
    {
        context = ctx;
        observers = new ArrayList<DataSetObserver>();
        sensors = new ArrayList<SensorStruct>();
    }

    public int addSensor(String name, String data, String userData)
    {
        sensors.add(new SensorStruct(name,data,userData));
        notifyObservers();
        return sensors.size()-1;
    }

    public void updateSensor(int idx, String data)
    {
        sensors.get(idx).sensorData = data;
        notifyObservers();
    }

    @Override
    public boolean areAllItemsEnabled() {
        //All items in the list are selectable
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        android.util.Log.d("DataListAdapter DTCPage", "Adding observer");
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        android.util.Log.d("DataListAdapter DTCPage", "Removing observer");
        observers.remove(observer);
    }

    public void notifyObservers()
    {
        //Iterator<DataSetObserver> obsIter = observers.iterator();
        for (DataSetObserver obsIter : observers)
        {
            obsIter.onChanged();
        }
    }

    public String getSensorName( int position)
    {
        return ((SensorStruct) getItem(position)).sensorName;
    }

    public String getSensorValue( int position)
    {
        return ((SensorStruct) getItem(position)).sensorData;
    }

    public String getUserData( int position)
    {
        return ((SensorStruct) getItem(position)).userData;
    }

    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Object getItem(int position) {
        return sensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.sensor_data_list_item, null);
        TextView nameView = (TextView) rootView.findViewById(R.id.sensor_name);
        TextView dataView = (TextView) rootView.findViewById(R.id.sensor_data);

        SensorStruct sen = sensors.get(position);
        nameView.setText(sen.sensorName);
        dataView.setText(sen.sensorData);
        return rootView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return sensors.isEmpty();
    }
}
