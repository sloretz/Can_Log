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

import com.androidplot.xy.*;

/**
 * Created by shane on 3/11/14.
 */
public class LiveDataPage extends SensorDataListViewFragment implements HandleBack {
    private static String SENSOR_GRAPH_IDX = "sensorGraphIndex";
    private XYPlot xyPlot;
    private int sensorGraphIndex = -1;


    public void onBackPressed()
    {
        //make list view visible and graph invisible
        if (sensorGraphIndex >= 0) {
            View view = getView();
            View list = view.findViewById(R.id.listView);
            list.setVisibility(View.VISIBLE);
            View plot = view.findViewById(R.id.XYPlot);
            plot.setVisibility(View.GONE);
            sensorGraphIndex = -1;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putInt(SENSOR_GRAPH_IDX, sensorGraphIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.live_data_page, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        sensorDataListAdapter = new SensorDataListAdapter(getActivity());
        listView.setAdapter(sensorDataListAdapter);
        Backend backend = Backend.getInstance();

        if (savedInstanceState != null)
        {
            sensorGraphIndex = savedInstanceState.getInt(SENSOR_GRAPH_IDX);
        }

        backend.fetchAvailableSensorsAndData(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                ArrayList<String> sensors = result.getStringArrayList("Sensors");
                ArrayList<String> data = result.getStringArrayList("Data");
                Iterator<String> senIter = sensors.iterator();
                Iterator<String> datIter = data.iterator();
                while (senIter.hasNext() && datIter.hasNext()) {
                    sensorDataListAdapter.addSensor(senIter.next(), datIter.next());
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                getView().findViewById(R.id.listView).setVisibility(View.GONE);
                getView().findViewById(R.id.XYPlot).setVisibility(View.VISIBLE);
                sensorGraphIndex = position;
                //Toast toast = Toast.makeText(getActivity().getApplicationContext(), "TODO show graph of data", Toast.LENGTH_SHORT);
                //        toast.show();
            }
        });

        xyPlot = (XYPlot) rootView.findViewById(R.id.XYPlot);

        if (sensorGraphIndex >= 0)
        {
            //make graph visible
            xyPlot.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else {
            //make list visible
            xyPlot.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        /*
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);

        String seriesName = getArguments().getString(ARG_SERIES_NAME);
        ArrayList<GraphValue> GraphValues = getArguments().getParcelableArrayList(ARG_VALUES);
        ArrayList<Number> values = new ArrayList<Number>();
        Iterator<GraphValue> valueIterator = GraphValues.iterator();
        while (valueIterator.hasNext())
        {
            values.add(valueIterator.next().getValue());
        }

        XYSeries series = new SimpleXYSeries(
                values,
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
                seriesName);

        xyPlot.addSeries(series, seriesFormat);

        // reduce the number of range labels
        xyPlot.setTicksPerRangeLabel(3);
        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);
        */

        return rootView;
    }
}
