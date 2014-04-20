package edu.sjsu.canlog.app.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Iterator;

import edu.sjsu.canlog.app.R;
import edu.sjsu.canlog.app.backend.Backend;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by shane on 3/11/14.
 */
public class DTCPage extends Fragment implements HandleVisibilityChange {
    public SensorDataListAdapter sensorDataListAdapter;

    public void onBecomesVisible()
    {
        android.util.Log.d("DTCPage", "onBecomesVisible");
        Backend backend = Backend.getInstance();
        backend.fetchDTCs(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                android.util.Log.d("DTCPage", "Got results from backend");
                ArrayList<String> DTCs = result.getStringArrayList("DTCs");
                ArrayList<String> descriptions = result.getStringArrayList("short_descriptions");
                if (sensorDataListAdapter.getCount() == 0) {
                    Iterator<String> dtcIter = DTCs.iterator();
                    Iterator<String> descIter = descriptions.iterator();
                    while (dtcIter.hasNext() && descIter.hasNext()) {
                        sensorDataListAdapter.addSensor(dtcIter.next(), descIter.next());
                    }
                }
                else{
                    for (int i = 0; i < sensorDataListAdapter.getCount(); i++){
                        sensorDataListAdapter.updateSensor(i,descriptions.get(i));
                    }
                }
            }
        } );
    }

    public void onBecomesInvisible()
    {
        android.util.Log.d("DTCPage", "onBecomesInvisible");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dtc_page, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        Button button = (Button) rootView.findViewById(R.id.button);
        final Backend backend = Backend.getInstance();

        sensorDataListAdapter = new SensorDataListAdapter(getActivity());

        listView.setAdapter(sensorDataListAdapter);
        final DTCPage _this = this;

        //On button tap, have the backend clear DTCs
        button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v){
            backend.sendClearDTCs(new Backend.ResultHandler() {
                public void gotResult(Bundle result) {
                    //Lets toast to that
                    //TODO put these in strings.xml
                    String strResult = "DTCs successfully cleared";
                    if (!result.getBoolean("did_clear"))
                    {
                        strResult = "Failed to clear DTCs";
                    }
                    Toast toast = Toast.makeText(_this.getActivity().getApplicationContext(), strResult, Toast.LENGTH_SHORT);
                    toast.show();
                }
                });
        }});

        return rootView;
    }
}
