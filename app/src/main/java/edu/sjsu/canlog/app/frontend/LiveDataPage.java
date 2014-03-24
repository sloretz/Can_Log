package edu.sjsu.canlog.app.frontend;

import edu.sjsu.canlog.app.R;
import edu.sjsu.canlog.app.backend.Backend;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by shane on 3/11/14.
 */
public class LiveDataPage extends SensorDataListViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        Backend backend = Backend.getInstance();

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
                //TODO show graph here
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "TODO show graph of data", Toast.LENGTH_SHORT);
                        toast.show();
            }
        });

        return rootView;
    }
}
