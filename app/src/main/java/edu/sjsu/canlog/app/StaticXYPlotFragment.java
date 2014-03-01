package edu.sjsu.canlog.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by shane on 2/22/14.
 */
public class StaticXYPlotFragment extends Fragment {
    private static final String ARG_VERT_VALUES = "vertical_values";
    private static final String ARG_HORI_VALUES = "horizontal_values";

    public static ArrayListViewFragment newInstance(ArrayList<String> verticalPoints, ArrayList<String> horizontalPoints) {
        ArrayListViewFragment fragment = new ArrayListViewFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_VERT_VALUES, verticalPoints);
        args.putStringArrayList(ARG_HORI_VALUES, horizontalPoints);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        //ArrayList<String> values = getArguments().getStringArrayList(ARG_LIST_VALUES);


        return rootView;
    }
}
