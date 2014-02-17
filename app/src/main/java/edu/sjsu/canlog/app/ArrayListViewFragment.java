package edu.sjsu.canlog.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.Iterator;

import java.util.ArrayList;

/**
 * Created by shane on 2/17/14.
 */
public class ArrayListViewFragment extends Fragment{
        private static final String ARG_LIST_VALUES = "listValues";
        public SensorListAdapter sensorListAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ArrayListViewFragment newInstance(ArrayList<String> Values) {
            ArrayListViewFragment fragment = new ArrayListViewFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(ARG_LIST_VALUES, Values);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);

            ArrayList<String> values = getArguments().getStringArrayList(ARG_LIST_VALUES);

            sensorListAdapter = new SensorListAdapter(getActivity());


            Iterator<String> strIter = values.iterator();
            while (strIter.hasNext())
            {
                sensorListAdapter.addSensor(strIter.next(), "0.0");
            }

            listView.setAdapter(sensorListAdapter);

            sensorListAdapter.updateSensor(0, "fitfsadf");

            return rootView;
        }

}
