package com.example.carobdnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;




public class ViewDataScreen extends Activity {

    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private static ReadInput mReadThread = null;


    private boolean mIsUserInitiatedDisconnect = false;

    // All controls here
    private static TextView mTxtSend;
    private ScrollView publishView;
    private TextView mTxtReceive;
    private Button mBtnClearInput;
    private ScrollView scrollView;
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    private Button publish_btn;

    public String sensorData;
    public String sensorTopic;
    public String tempSensorInput;


    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    protected static MqttAndroidClient client;

//    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
//    Handler mHandler = new Handler();

    public ViewDataScreen() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vew_data_log);
        ActivityHelper.initialize(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(FirstActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(FirstActivity.DEVICE_UUID));
        mMaxChars = b.getInt(FirstActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");
        mTxtSend = (TextView) findViewById(R.id.txtSend);
        mTxtReceive = (TextView) findViewById(R.id.txtReceive);
        chkScroll = (CheckBox) findViewById(R.id.chkScroll);
        chkReceiveText = (CheckBox) findViewById(R.id.chkReceiveText);
        scrollView = (ScrollView) findViewById(R.id.viewScroll);
        publishView = (ScrollView) findViewById(R.id.publishView);
        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);
        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());


        String clientId = MqttClient.generateClientId();
        publish_btn = (Button) findViewById(R.id.publish_btn);

        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }



        publish_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myTimer.scheduleAtFixedRate(myTask, 0l, 1 * (60 * 1000)); // Runs every 5 mins


            }
        });



        mBtnClearInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });




    }

    public void callWiteFun(String data){
        mReadThread.writeToArduino(data);//........................call hwrite here
    }


    public void uploadToMQTT2(){
        String topic;
        String payload;

        String dataArr[]=mReadThread.getItems();
        for (int i=0;i<dataArr.length; i++){

            topic = "driver1testingspace";//tokens.nextToken();// this will contain "everything before the / ie the topic info"
            payload = dataArr[i];//tokens.nextToken();// this will contain " the sensor data"


            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                final MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                //add code to write published items here .......................................................
                //mTxtSend.post(
//                mTxtSend.append("published: "+ message.toString());//);//................the input);

            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

        }
    }


    Timer myTimer = new Timer();
    TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            // your code
            uploadToMQTT2(); // Your method
        }
    };


    public class ReadInput implements Runnable { //...........change to private

        Queue<String> myq = new LinkedList<>();

        public String[] getItems(){
            int counter_=0;
            String aggreItems[]=new String[myq.size()];
            while(!myq.isEmpty()){
                if(myq.peek().contains("}")) {
                    aggreItems[counter_] = myq.poll();
                    counter_++;
                }
            }
            myq.clear();
            counter_=0;
            return aggreItems;
        }


        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

//        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[1024];//[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);





                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */


                        if (chkReceiveText.isChecked()) {
                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtReceive.append(strInput);//................the input


                                    /*
                                     * This section handles the aggregation of data
                                     */

                                    //StringTokenizer tempText = new StringTokenizer(mTxtReceive.getEditableText().toString(), "\n");
                                    String[] tempText = mTxtReceive.getEditableText().toString().split("\\r?\\n");
                                    Log.v("ViewDataScreen", mTxtReceive.getEditableText().toString());
                                    //while(tempText.hasMoreTokens()){
                                    for (int i = 0; i < tempText.length; i++) {
                                        tempSensorInput = tempText[i];

                                        //tempSensorInput=mTxtReceive.getEditableText().toString();
                                        //append to var till end and push
                                        //ie if not },strip and add to var
                                        //if }, then add to var and push to queue
                                        //set var to null and do again
                                        if(!tempSensorInput.contains("}")){//.............................fix this later..sometimes the data is cut so we dont get }

                                            sensorTopic=sensorTopic+tempSensorInput.replaceAll("[\\n\\t ]", "");
                                        }else if(tempSensorInput.contains("}")){
                                            sensorTopic=sensorTopic+tempSensorInput;
                                            myq.add(sensorTopic);
                                            sensorTopic="";
                                        }

                                    }
                                    ;

                                    //}


                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if (txtLength > mMaxChars) {
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }

                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


        public void writeToArduino(String input){//to write data to arduino
            OutputStream outputStream;
            byte[] bytes = input.getBytes();
            try{
                outputStream = mBTSocket.getOutputStream();
                while(!bStop){
                    outputStream.write(bytes);
                }
            }catch (IOException e){
                Log.e("ViewDataScreen","send error, unable to send",e);
            }
        }//...............................................................................copied and sent to onCreate so we can test somewhere else

        public void stop() {
            bStop = true;
        }

    }


    private class PublishInput {

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }

        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ViewDataScreen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }


}
