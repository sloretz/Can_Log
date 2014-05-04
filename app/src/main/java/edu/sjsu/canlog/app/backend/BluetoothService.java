package edu.sjsu.canlog.app.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


/**
 * Created by Brian on 3/29/2014.
 * Bluetooth Service, does all the connecting work
 */
public class BluetoothService {

    //private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    protected BluetoothSocket mConnectedSocket;
    protected BufferedWriter btWriter;
    protected BufferedReader btReader;
    protected Lock mSocketLock;
    protected Condition isConnected;
    //private ConnectedThread mConnectedThread;
    protected Context mContext;
    private int mState;
    private static final int STATE_NONE=0;
    //private static final int STATE_LISTEN=1;
    private static final int STATE_CONNECTING=2;
    private static final int STATE_CONNECTED=3;


    public static abstract class ResultHandler {
        public abstract void gotResult(Bundle result);
    }

    protected abstract class BluetoothTask extends AsyncTask<ResultHandler, Void, Bundle> {
        ResultHandler handler;
        @Override
        protected Bundle doInBackground(ResultHandler ... handlers){
            this.handler = handlers[0];
            /**
             * acquire lock on bluetooth socket
             * transfer data to socket
             * get results from socket
             * put results into bundle
             * release lock
             * return bundle
             */
            Bundle result = null;
            android.util.Log.d("Backend", "About to acquire lock");
            mSocketLock.lock();
            Log.d("Backend", "Acquired lock");
            try {
                while (mConnectedSocket == null)
                {
                    try {
                        Log.d("Backend", "Waiting for isConnected");
                        isConnected.await();
                    } catch (InterruptedException ie) {
                        //I don't think this should happen on android...
                        Log.e("Backend", "The threading environment is unsupported");
                        return null;
                    }
                }
                Log.d("Backend", "Calling socket transfer function");
                result = doSocketTransfer();
                Log.d("Backend", "Returned from socket transfer function");
            } finally {
                Log.d("Backend", "Releasing lock");
                mSocketLock.unlock();

            }
            return result;
        }

        //This runs on a background thread
        //As long as we're in this function
        //we have the bluetooth socket
        //all to ourselves.
        protected abstract Bundle doSocketTransfer();

        //This runs in the UI thread
        protected void onPostExecute(Bundle result)
        {
            if (this.handler != null)
                this.handler.gotResult(result);
        }
    }

    protected BluetoothService(Context context)
    {
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mSocketLock = new ReentrantLock();
        isConnected = mSocketLock.newCondition();
        //mHandler = handler;
        mState = STATE_NONE;
    }
    private synchronized void setState(int state)
    {
        mState=state;
        //mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state,-1).sendToTarget();
    }

    public synchronized void connect(BluetoothDevice device)
    {
        Log.d("ConnectThread", "connect to: " + device);
        if(mState==STATE_CONNECTING)
        {
            if(mConnectThread!=null)
            {
                mConnectThread.cancel();
                mConnectThread=null;
            }
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public boolean isConnected()
    {
        return mConnectedSocket != null;
    }


    public synchronized void connected(BluetoothSocket socket)
    {
        Log.d("Connected", "We are connected");
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
        }

        mConnectedSocket = socket;
        try {
            InputStream is = mConnectedSocket.getInputStream();
            btReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            OutputStream os = mConnectedSocket.getOutputStream();
            btWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        } catch (IOException e)
        {
            //uuh yeah, this shouldn't happen
        }

        //Wake everyone up, we're connected!
        Log.d("Backend", "Locking to signal connected");
        mSocketLock.lock();
        try {
            isConnected.signalAll();
        } finally {
            Log.d("Backend", "Unlocking after signalling connected");
            mSocketLock.unlock();
        }
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
        setState(STATE_NONE);
    }
    public synchronized void stop()
    {
        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedSocket != null) {
            try {
                mConnectedSocket.close();
            } catch (IOException e){
                //...
            }
        }
        setState(STATE_NONE);
    }


    public class ConnectThread extends Thread {


        private final BluetoothSocket mmSocket;
        //private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            //TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            //mmDevice = device;
            try {
                Log.e("Device being connected to", device.toString());
                tmp = device.createRfcommSocketToServiceRecord((UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")));
            } catch (Exception e) {
                Log.e("FAILED IN CREATING RFCOMMSOCKET","ERROR");
            }
            mmSocket = tmp;
        }

        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    //Log.e("Device is:", mmSocket.getRemoteDevice().toString());
                    Log.e("Connection Exception",connectException.toString());
                    Log.e("Pointer is",Integer.toString(this.hashCode()));
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("BTerror","Connect Error");
                }
                return;
            }
            synchronized (BluetoothService.this){
                mConnectThread=null;
            }

            connected(mmSocket);
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BTerror","Error closing socket");
            }
        }
    }
}