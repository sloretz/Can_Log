package edu.sjsu.canlog.app.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;

import edu.sjsu.canlog.app.backend.Backend;

/**
 * Created by shane on 3/11/14.
 */
public class AboutCarPage extends SensorDataListViewFragment implements HandleVisibilityChange{
    public static boolean firstCreate = true;
    public void onBecomesVisible()
    {
        android.util.Log.d("AboutCarPage", "onBecomesVisible");
        final Backend backend = Backend.getInstance();
        backend.fetchCarInfo(new Backend.ResultHandler() {
            public void gotResult(Bundle result) {
                if (backend.wasError(result))
                    return;
                ArrayList<String> names = result.getStringArrayList("carInfoNames");
                ArrayList<String> values = result.getStringArrayList("values");
                Log.d("AboutCarPage", "Num values " + values.size());
                if (sensorDataListAdapter.getCount() == 0) {
                    Iterator<String> nameIter = names.iterator();
                    Iterator<String> valIter = values.iterator();
                    while (nameIter.hasNext() && valIter.hasNext()) {
                        sensorDataListAdapter.addSensor(nameIter.next(), valIter.next(),"");
                    }
                }
                else
                {
                    for (int i = 0; i < sensorDataListAdapter.getCount(); i++){
                        sensorDataListAdapter.updateSensor(i,values.get(i));
                    }
                }
            }
        });
    }

    public void onBecomesInvisible()
    {
        android.util.Log.d("AboutCarPage", "onBecomesInvisible");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        //Special case, we're the first page on the GUI so we become visible on create
        if (firstCreate)
            onBecomesVisible();
        firstCreate = false;

        return rootView;
    }
}
