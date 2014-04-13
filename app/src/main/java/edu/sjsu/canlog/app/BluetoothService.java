package edu.sjsu.canlog.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.os.Bundle;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;

/**
 * Created by Brian on 3/29/2014.
 */
public class BluetoothService {

    private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Context mContext;

    public BluetoothService(Context context, Handler handler)
    {
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mHandler = handler;
    }

    public synchronized void connect(BluetoothDevice device, boolean secure)
    {
        mConnectThread = new ConnectThread(device ,secure);
        mConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }



    public class ConnectThread extends Thread {


        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device, boolean secure) {
            BluetoothSocket tmp = null;
            TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(tManager.getDeviceId()));
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
    public class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket=socket;
            InputStream tmpIn=null;
            OutputStream tmpOut=null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}