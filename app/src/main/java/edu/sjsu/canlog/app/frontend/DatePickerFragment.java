package edu.sjsu.canlog.app.frontend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by shane on 4/26/14.
 */
public abstract class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    abstract public void onDateSet(long unixTime);

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        GregorianCalendar c = new GregorianCalendar();
        c.set(year, month, day);
        onDateSet(c.getTimeInMillis()/1000);
    }
}
