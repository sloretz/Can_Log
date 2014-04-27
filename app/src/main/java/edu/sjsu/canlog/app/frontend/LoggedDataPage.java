package edu.sjsu.canlog.app.frontend;

import edu.sjsu.canlog.app.R;
import edu.sjsu.canlog.app.backend.Backend;
import edu.sjsu.canlog.app.backend.BluetoothService;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.androidplot.xy.*;


/**
 * Logged data page:
 * First show VINs
 * Then show logged PIDs for VINs
 * Then show graph of VIN where
 * context menu allows specifying a
 * start and end date using the
 * date picker fragment
 */


public class LoggedDataPage extends SensorDataListViewFragment implements HandleBack, HandleVisibilityChange {
    private static String GRAPH_VALUE_LIST = "graphValuesList";
    private static String CURRENT_PAGE = "currentlyDisplayedPage";
    private static String DEFAULT_SERIES_NAME = " ERR getting name";
    private static String VIN_NUMBER = "vinNumber";
    private static String PID_NUMBER = "pidNumber";
    private XYPlot xyPlot;
    private ListView listView;
    private ArrayList<GraphValue> graphValues;
    private Timer updateTimer;
    private page_t displayed_page = page_t.NO_PAGE;
    private String currentVIN = null;
    private String currentPID = null;
    private int startDate = 0;
    private int endDate = 0;

    private enum page_t {NO_PAGE, DOWNLOAD_PAGE, VIN_PICKER, PID_PICKER, PID_GRAPH};

    private class DynamicRollingSeries implements XYSeries {
        ArrayList<GraphValue> valuesReference;

        public DynamicRollingSeries(ArrayList<GraphValue> values)
        {
            valuesReference = values;
        }

        @Override
        public int size() {
            return valuesReference.size()/2;
        }

        @Override
        public Number getX(int index) {
            //return valuesReference.get(index*2).getValue();
            return index;
        }

        @Override
        public Number getY(int index) {
            return valuesReference.get(index*2 + 1).getValue();
        }

        @Override
        public String getTitle() {
            return "Err:Should not display";
        }
    }

    public void set_visible(page_t nextPage)
    {
        if (nextPage == displayed_page)
        {
            //nothing to do, go away
            return;
        }

        if (nextPage == page_t.PID_GRAPH)
        {
            //hide the list, show the graph
            listView.setVisibility(View.GONE);
            xyPlot.setVisibility(View.VISIBLE);

            //get the graph data from the backend
        }
        else if (nextPage == page_t.VIN_PICKER)
        {
            //show the list, hide the graph
            listView.setVisibility(View.VISIBLE);
            xyPlot.setVisibility(View.GONE);

            //get Fresh data
            sensorDataListAdapter = new SensorDataListAdapter(getActivity());
            listView.setAdapter(sensorDataListAdapter);
            Backend backend = Backend.getInstance();
            backend.fetchLoggedVINs(new Backend.ResultHandler() {
                public void gotResult(Bundle result) {
                    ArrayList<String> VINs = result.getStringArrayList("VIN");
                    Iterator<String> vinIter = VINs.iterator();
                    while (vinIter.hasNext()) {
                        sensorDataListAdapter.addSensor("Vehicle", vinIter.next());
                    }
                }
            });

            //Set a click listener that brings us to the PID page
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                    currentVIN = sensorDataListAdapter.getSensorValue(position);
                    Log.d("LoggedDataPage", "VIN clicked " + currentVIN);
                    set_visible(page_t.PID_PICKER);
                }
            });
        }
        else if (nextPage == page_t.PID_PICKER)
        {
            //show the list, hide the graph
            listView.setVisibility(View.VISIBLE);
            xyPlot.setVisibility(View.GONE);

            sensorDataListAdapter = new SensorDataListAdapter(getActivity());
            listView.setAdapter(sensorDataListAdapter);

            //get Fresh data
            sensorDataListAdapter = new SensorDataListAdapter(getActivity());
            listView.setAdapter(sensorDataListAdapter);
            Backend backend = Backend.getInstance();
            backend.fetchLoggedPIDs(currentVIN, new Backend.ResultHandler() {
                public void gotResult(Bundle result) {
                    ArrayList<String> PIDs = result.getStringArrayList("PID");
                    ArrayList<String> descriptions = result.getStringArrayList("Desc");
                    Iterator<String> pidIter = PIDs.iterator();
                    Iterator<String> descIter = descriptions.iterator();
                    while (pidIter.hasNext() && descIter.hasNext()) {
                        sensorDataListAdapter.addSensor(descIter.next(), pidIter.next());
                    }
                }
            });

            //Set a click listener that brings us to the PID page
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                    currentPID = sensorDataListAdapter.getSensorValue(position);
                    Log.d("LoggedDataPage", "PID clicked " + currentPID);
                    set_visible(page_t.PID_GRAPH);
                }
            });
        }
        else if (nextPage == page_t.DOWNLOAD_PAGE)
        {
            //hide everything
            listView.setVisibility(View.GONE);
            xyPlot.setVisibility(View.GONE);
            DownloadHistoryDialog dl_dialog = new DownloadHistoryDialog();
            dl_dialog.show(getFragmentManager(), "dialog");
        }

        displayed_page = nextPage;
    }

    public void onBecomesVisible()
    {
        android.util.Log.d("LiveDataPage", "onBecomesVisible");
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
        super.onSaveInstanceState(state);
        state.putParcelableArrayList(GRAPH_VALUE_LIST, graphValues);
        state.putSerializable(CURRENT_PAGE, displayed_page);
        state.putString(VIN_NUMBER,currentVIN);
    }

    private void restore_state(Bundle savedState)
    {
        graphValues = savedState.getParcelableArrayList(GRAPH_VALUE_LIST);
        currentVIN = savedState.getString(VIN_NUMBER);
        set_visible((page_t)savedState.getSerializable(CURRENT_PAGE));
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

        if (savedInstanceState != null)
        {
            restore_state(savedInstanceState);
        }
        else {
            set_visible(page_t.DOWNLOAD_PAGE);
        }

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

    public class DownloadHistoryDialog extends DialogFragment {
        @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.download_data, container, false);
            getDialog().setTitle(R.string.dl_hist_title);

            final Button dl_button = (Button) v.findViewById(R.id.dl_button);
            final Button cont_button = (Button) v.findViewById(R.id.cont_button);
            final ProgressBar pb = (ProgressBar) v.findViewById(R.id.dl_progress);
            pb.setVisibility(View.GONE);

            dl_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("LoggedDataPage", "Beginning Download");
                    pb.setVisibility(View.VISIBLE);
                    dl_button.setVisibility(View.GONE);
                    cont_button.setVisibility(View.GONE);

                    Backend backend = Backend.getInstance();
                    backend.beginHistoryDownload(new Backend.ResultHandler() {
                        public void gotResult(Bundle result) {
                            set_visible(page_t.VIN_PICKER);
                            dismiss();
                        }
                    });
                }
            });

            cont_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("LoggedDataPage", "Dismissing download of new data");
                    set_visible(page_t.VIN_PICKER);
                    dismiss();
                }
            });

            return v;
        }
    }

    protected String _findSeriesName()
    {
        return DEFAULT_SERIES_NAME;
    }
}
