package edu.sjsu.canlog.app.frontend;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.os.Bundle;
import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 4/19/14.
 * Dialog for connecting
 */
public class ConnectingDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.connecting)
                .setPositiveButton(null, null)
                        //.setPositiveButton(R.string.debug_continue, new DialogInterface.OnClickListener() {
                        //    public void onClick(DialogInterface dialog, int id) {
                        //        Backend.getInstance().dbg_force_continue();
                        //        dismiss();
                        //    }
                        //})

                .setNegativeButton(null, null);
        return builder.create();
    }

}
