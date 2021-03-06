package edu.sjsu.canlog.app.frontend;

import edu.sjsu.canlog.app.R;
import edu.sjsu.canlog.app.backend.Backend;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.androidplot.xy.*;

/**
 * Created by shane on 3/11/14.
 */
public class LiveDataPage extends SensorDataListViewFragment implements HandleBack, HandleVisibilityChange {
    private static String SENSOR_GRAPH_IDX = "sensorGraphIndex";
    private static String GRAPH_VALUE_LIST = "graphValuesList";
    private static String SENSOR_NAME_LIST = "sensorNameList";
    private static String SENSOR_VALUE_LIST = "sensorValueList";
    private static String DEFAULT_SERIES_NAME = " ERR getting name";
    private XYPlot xyPlot;
    private ListView listView;
    private int sensorGraphIndex = -1;
    private ArrayList<GraphValue> graphValues;
    private Timer updateTimer;


    private class ListUpdateTask extends TimerTask {
        @Override
        public void run() {
            //Update the GUI
            Backend backend = Backend.getInstance();
            backend.fetchAvailableSensorsAndData(new Backend.ResultHandler() {
                public void gotResult(Bundle result) {
                    ArrayList<String> sensors = result.getStringArrayList("Sensors");
                    ArrayList<String> data = result.getStringArrayList("Data");
                    //First call, populate the sensor list
                    if (sensorDataListAdapter.getCount() == 0) {
                        Iterator<String> senIter = sensors.iterator();
                        Iterator<String> datIter = data.iterator();
                        while (senIter.hasNext() && datIter.hasNext()) {
                            sensorDataListAdapter.addSensor(senIter.next(), datIter.next());
                        }
                    }
                    //subsequent calls, update the gui
                    else {
                        for (int i = 0; i < sensorDataListAdapter.getCount(); i++){
                            sensorDataListAdapter.updateSensor(i,data.get(i));
                        }
                    }
                }
            });
        }
    }

    /**
     * Switch between the graph or the list
     * depending on the graph index
     */
    private void toggleGraphOrList(int newGraphIndex)
    {
        sensorGraphIndex = newGraphIndex;

        //make list view visible and graph invisible
        if (sensorGraphIndex < 0) {
            listView.setVisibility(View.VISIBLE);
            xyPlot.setVisibility(View.GONE);
        }
        //Make graph visible and list invisible
        else {
            listView.setVisibility(View.GONE);
            xyPlot.setVisibility(View.VISIBLE);
            //assume only one series
            xyPlot.setTitle(_findSeriesName());
        }
    }

    public void onBecomesVisible()
    {
        android.util.Log.d("LiveDataPage", "onBecomesVisible");
        updateTimer = new Timer("UpdateLiveDataPage",true);
        if (sensorGraphIndex < 0)
        {
            updateTimer.schedule(new ListUpdateTask(), 0, 1000);
        }
        else
        {
            //schedule graph update task
        }
    }

    public void onBecomesInvisible()
    {
        android.util.Log.d("LiveDataPage", "onBecomesInvisible");
        if (updateTimer != null) {
            //remove timer tasks
            updateTimer.cancel();
            updateTimer.purge();
            updateTimer = null;
        }
    }


    /**
     * If we're displaying the graph page,
     * switch to the list when the back button
     * is pressed
     */
    public void onBackPressed()
    {
        //make list view visible and graph invisible
        toggleGraphOrList(sensorGraphIndex = -1);
    }

    /**
     * Called when the activity is being destroyed, like
     * when the user rotates their device. We store
     * values that we want to use to populate the new
     * instance that is created when the activity is
     * rebuilt
     * @param state
     */
    @Override
    public void onSaveInstanceState(Bundle state)
    {
        android.util.Log.d("LiveDataPage", "onSaveInstanceState");
        //Timer is gonna die
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer.purge();
            updateTimer = null;
        }
        super.onSaveInstanceState(state);
        state.putInt(SENSOR_GRAPH_IDX, sensorGraphIndex);
        state.putParcelableArrayList(GRAPH_VALUE_LIST, graphValues);

        //Save the list
        ArrayList<String> sensorNames = new ArrayList<String>();
        ArrayList<String> sensorValues = new ArrayList<String>();
        for (int i = 0; i < sensorDataListAdapter.getCount(); i++) {
            sensorNames.add(sensorDataListAdapter.getSensorName(i));
            sensorValues.add(sensorDataListAdapter.getSensorValue(i));
        }
        state.putStringArrayList(SENSOR_NAME_LIST, sensorNames);
        state.putStringArrayList(SENSOR_VALUE_LIST, sensorValues);

    }

    /**
     * Activity is being created
     * Create our instance, possibly using the values that
     * we saved when the last instance was destroyed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.live_data_page, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        xyPlot = (XYPlot) rootView.findViewById(R.id.XYPlot);
        sensorDataListAdapter = new SensorDataListAdapter(getActivity());
        listView.setAdapter(sensorDataListAdapter);

        if (savedInstanceState != null)
        {
            graphValues = savedInstanceState.getParcelableArrayList(GRAPH_VALUE_LIST);
            sensorGraphIndex = savedInstanceState.getInt(SENSOR_GRAPH_IDX);

            //restore list item values
            ArrayList<String> sensors = savedInstanceState.getStringArrayList(SENSOR_NAME_LIST);
            ArrayList<String> data = savedInstanceState.getStringArrayList(SENSOR_VALUE_LIST);
            Iterator<String> senIter = sensors.iterator();
            Iterator<String> datIter = data.iterator();
            while (senIter.hasNext() && datIter.hasNext()) {
                sensorDataListAdapter.addSensor(senIter.next(), datIter.next());
            }
        }
        else
        {
            sensorGraphIndex = -1;
            graphValues = new ArrayList<GraphValue>();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                toggleGraphOrList(position);
            }
        });

        //Set up plot series
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);

        XYSeries series = new SimpleXYSeries(
                _graphValuesToNumber(graphValues),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
                _findSeriesName());

        xyPlot.addSeries(series, seriesFormat);

        // reduce the number of range labels
        xyPlot.setTicksPerRangeLabel(3);
        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);

        toggleGraphOrList(sensorGraphIndex);

        return rootView;
    }

    protected ArrayList<Number> _graphValuesToNumber(ArrayList<GraphValue> graphValues)
    {
        ArrayList<Number> values = new ArrayList<Number>();
        Iterator<GraphValue> valueIterator = graphValues.iterator();
        while (valueIterator.hasNext())
        {
            values.add(valueIterator.next().getValue());
        }
        return values;
    }

    protected String _findSeriesName()
    {
        if (sensorGraphIndex >= 0)
        {
            return sensorDataListAdapter.getSensorName(sensorGraphIndex);
        }
        return DEFAULT_SERIES_NAME;
    }
}
