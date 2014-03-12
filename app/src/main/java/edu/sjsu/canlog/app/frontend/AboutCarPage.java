package edu.sjsu.canlog.app.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;

import edu.sjsu.canlog.app.backend.Backend;

/**
 * Created by shane on 3/11/14.
 */
public class AboutCarPage extends SensorDataListViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        Backend backend = Backend.getInstance();

        backend.fetchCarInfo(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                ArrayList<String> names = result.getStringArrayList("carInfoNames");
                ArrayList<String> values = result.getStringArrayList("values");
                Iterator<String> nameIter = names.iterator();
                Iterator<String> valIter = values.iterator();
                while (nameIter.hasNext() && valIter.hasNext()) {
                    sensorDataListAdapter.addSensor(nameIter.next(), valIter.next());
                }
            }
        });

        return rootView;
    }
}
