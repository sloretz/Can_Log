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
public class DTCPage extends ArrayListViewFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);
        Backend backend = Backend.getInstance();

        backend.fetchDTCs(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                ArrayList<String> DTCs = result.getStringArrayList("DTCs");
                ArrayList<String> descriptions = result.getStringArrayList("short_descriptions");
                Iterator<String> dtcIter = DTCs.iterator();
                Iterator<String> descIter = descriptions.iterator();
                while (dtcIter.hasNext() && descIter.hasNext())
                {
                    sensorListAdapter.addSensor(dtcIter.next(),descIter.next());
                }
            }
        } );

        return rootView;
    }
}
