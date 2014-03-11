package edu.sjsu.canlog.app.frontend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.Iterator;

import java.util.ArrayList;

import edu.sjsu.canlog.app.R;

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



            sensorListAdapter = new SensorListAdapter(getActivity());

            Bundle args = getArguments();
            ArrayList<String> values = null;
            if (args != null)
            {
                values = args.getStringArrayList(ARG_LIST_VALUES);
            }

            if (values != null)
            {
                Iterator<String> strIter = values.iterator();
                while (strIter.hasNext())
                {
                    sensorListAdapter.addSensor(strIter.next(), "0.0");
                }
            }
            listView.setAdapter(sensorListAdapter);

            return rootView;
        }

}
