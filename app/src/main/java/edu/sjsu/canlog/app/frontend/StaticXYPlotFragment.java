package edu.sjsu.canlog.app.frontend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.*;
import java.util.ArrayList;
import java.util.Iterator;

import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 2/22/14.
 */
public class StaticXYPlotFragment extends Fragment {
    private static String ARG_SERIES_NAME = "seriesName";
    private static String ARG_VALUES = "seriesValues";
    private XYPlot xyPlot;

    public static StaticXYPlotFragment newInstance(String name, ArrayList<Number> xValues, ArrayList<Number> yValues) {
        StaticXYPlotFragment fragment = new StaticXYPlotFragment();
        Bundle args = new Bundle();

        args.putString(ARG_SERIES_NAME, name);
        ArrayList<GraphValue> graphedValues = new ArrayList<GraphValue>();
        Iterator<Number> xIter = xValues.iterator();
        Iterator<Number> yIter = yValues.iterator();
        while (xIter.hasNext() && yIter.hasNext())
        {
            graphedValues.add(new GraphValue(xIter.next()));
            graphedValues.add(new GraphValue(yIter.next()));
        }

        args.putParcelableArrayList(ARG_VALUES, graphedValues);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.static_xy_plot, container, false);
        xyPlot = (XYPlot) rootView.findViewById(R.id.StaticXYPlot);

        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);

        String seriesName = getArguments().getString(ARG_SERIES_NAME);
        ArrayList<GraphValue> GraphValues = getArguments().getParcelableArrayList(ARG_VALUES);
        ArrayList<Number> values = new ArrayList<Number>();
        Iterator<GraphValue> valueIterator = GraphValues.iterator();
        while (valueIterator.hasNext())
        {
            values.add(valueIterator.next().getValue());
        }

        XYSeries series = new SimpleXYSeries(
                values,
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
                seriesName);

        xyPlot.addSeries(series, seriesFormat);

        // reduce the number of range labels
        xyPlot.setTicksPerRangeLabel(3);
        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);

        return rootView;
    }
}
