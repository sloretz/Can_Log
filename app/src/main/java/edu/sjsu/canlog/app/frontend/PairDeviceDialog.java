package edu.sjsu.canlog.app.frontend;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import edu.sjsu.canlog.app.backend.Backend;

import java.util.Set;
import java.util.ArrayList;

import edu.sjsu.canlog.app.R;

/**
 * Created by shane on 4/19/14.
 */
public class PairDeviceDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Set<BluetoothDevice> pairedDevices= BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        final ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        ArrayList<CharSequence> deviceNames = new ArrayList<CharSequence>();
        for (BluetoothDevice device : pairedDevices) {
            Log.i("Paired device", device.getName());
            devices.add(device);
            deviceNames.add(device.getName() + "\n" + device.getAddress());
        }
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pair_bluetooth_demand)
                .setItems((CharSequence[]) deviceNames.toArray(new CharSequence[devices.size()]), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        Backend.getInstance().connect(devices.get(id));
                    }
                })
                .setPositiveButton(null, null)
                .setPositiveButton(R.string.debug_continue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Backend.getInstance().dbg_force_continue();
                        dismiss();
                    }
                })

                .setNegativeButton(null, null);
        return builder.create();
    }

}
