package edu.sjsu.canlog.app.frontend;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.os.Bundle;
import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 4/19/14.
 */
public class PairDeviceDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.pair_bluetooth_demand)
                .setPositiveButton(null,null)
                .setNegativeButton(null, null);
        return builder.create();
    }

}
