package edu.sjsu.canlog.app.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.sjsu.canlog.app.MainActivity;


/**
 * Created by Brian on 3/29/2014.
 */
public class BluetoothService {

    //private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    protected BluetoothSocket mConnectedSocket;
    protected Lock mSocketLock;
    //private ConnectedThread mConnectedThread;
    private Context mContext;
    private int mState;
    private static final int STATE_NONE=0;
    private static final int STATE_LISTEN=1;
    private static final int STATE_CONNECTING=2;
    private static final int STATE_CONNECTED=3;


    public static abstract class ResultHandler {
        public abstract void gotResult(Bundle result);
    }

    protected abstract class BluetoothTask extends AsyncTask<ResultHandler, Void, Bundle> {
        ResultHandler handler;
        @Override
        protected Bundle doInBackground(ResultHandler ... handlers) {
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
            android.util.Log.i("Backend", "About to acquire lock");
            mSocketLock.lock();
            try {
                result = doSocketTransfer();
            } finally {
                android.util.Log.i("Backend", "Releasing lock");
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
            this.handler.gotResult(result);
        }
    }

    protected BluetoothService(Context context)
    {
        mAdapter= BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        mSocketLock = new ReentrantLock();
        mSocketLock.lock(); //We release this once we've connected
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
        Log.d("dontcARE", "connect to: " + device);
        if(mState==STATE_CONNECTING)
        {
            if(mConnectThread!=null)
            {
                mConnectThread.cancel();
                mConnectThread=null;
            }
            /*
            if(mConnectedThread != null)
            {
                mConnectedThread.cancel();
                mConnectedThread=null;
            }*/
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
        }

        mConnectedSocket = socket;
        mSocketLock.unlock();
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
                tmp = device.createInsecureRfcommSocketToServiceRecord((UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")));
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
}