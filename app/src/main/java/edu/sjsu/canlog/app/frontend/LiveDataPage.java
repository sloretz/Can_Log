package edu.sjsu.canlog.app.frontend;

import edu.sjsu.canlog.app.backend.Backend;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 3/11/14.
 */
public class LiveDataPage extends ArrayListViewFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);
        Backend backend = Backend.getInstance();

        backend.fetchAvailableSensorsAndData(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                ArrayList<String> sensors = result.getStringArrayList("Sensors");
                ArrayList<String> data = result.getStringArrayList("Data");
                Iterator<String> senIter = sensors.iterator();
                Iterator<String> datIter = data.iterator();
                while (senIter.hasNext() && datIter.hasNext())
                {
                    sensorListAdapter.addSensor(senIter.next(),datIter.next());
                }
            }
        } );

        return rootView;
    }
}