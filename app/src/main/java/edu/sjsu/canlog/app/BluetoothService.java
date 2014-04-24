package edu.sjsu.canlog.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;
import android.util.Log;


/**
 * Created by Brian on 3/29/2014.
 */
public class BluetoothService {

    private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Context mContext;
    private int mState;
    private static final int STATE_NONE=0;
    private static final int STATE_LISTEN=1;
    private static final int STATE_CONNECTING=2;
    private static final int STATE_CONNECTED=3;
    public BluetoothService(Context context, Handler handler)
    {
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mHandler = handler;
        mState = STATE_NONE;
    }
    private synchronized void setState(int state)
    {
        Log.d("State Change", "setState() " + mState + " -> " + state);
        mState=state;
        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state,-1).sendToTarget();
    }

    public synchronized void connect(BluetoothDevice device)
    {
        Log.d("dontcARE", "connect to: " + device);
        if(mState==STATE_CONNECTING)
        {
            if(mConnectThread!=null)
            {
                mConnectThread.cancel();
                mConnectThread=null;
            }
            if(mConnectedThread != null)
            {
                mConnectedThread.cancel();
                mConnectedThread=null;
            }
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectedThread=null;
        }
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        //Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        setState(STATE_CONNECTED);
    }
    public synchronized void start()
    {
        Log.d("STARTING SERVICE", "START");
        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread=null;
        }
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }
    public synchronized void stop()
    {
        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }


    public class ConnectThread extends Thread {


        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            //TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            mmDevice = device;
            try {
                Log.e("Device being connected to", device.toString());
                tmp = device.createRfcommSocketToServiceRecord((UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")));
            } catch (Exception e) {
                Log.e("FAILED IN CREATING RFCOMMSOCKET","ERROR");
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i("Thread","Begin ConnectThread");
            setName("ConnectThread");
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.e("AFTER CONNECT","AFTER");
            } catch (IOException connectException) {
                try {
                    Log.e("Device is:", mmSocket.getRemoteDevice().toString());
                    Log.e("Connection Exception",connectException.toString());
                    Log.e("Pointer is",Integer.toString(this.hashCode()));
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            synchronized (BluetoothService.this){
                mConnectThread=null;
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
            Log.e("Thread","Creating Connected Thread");
            InputStream tmpIn=null;
            OutputStream tmpOut=null;
            mmSocket = socket;
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
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, buffer.length, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e("Write Exception", "Exception during write", e);
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("EXCEPTION CANCELING", "ERROR");
            }
        }
    }
}