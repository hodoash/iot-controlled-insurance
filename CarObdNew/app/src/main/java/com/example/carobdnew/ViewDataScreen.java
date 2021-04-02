package com.example.carobdnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.annotation.RequiresApi;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//public class ViewDataScreen extends Activity {
//}


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


        //final constaints=

//        public class UploadWorker extends Worker {
//            public UploadWorker(
//                    @NonNull Context context,
//                    @NonNull WorkerParameters params) {
//                super(context, params);
//            }
//            @Override
//            public Result doWork() {
//
//                // Do the work here--in this case, upload the images.
//                //uploadImages();
//                String topic;
//                String payload;
//                String dataArr[]=mReadThread.getItems();
//                for (int i=0;i<dataArr.length; i++){
//
//                    StringTokenizer tokens = new StringTokenizer(dataArr[i], "/");
//
//                    topic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
//                    payload = tokens.nextToken();// this will contain " the sensor data"
//
//                    byte[] encodedPayload = new byte[0];
//                    try {
//                        encodedPayload = payload.getBytes("UTF-8");
//                        MqttMessage message = new MqttMessage(encodedPayload);
//                        client.publish(topic, message);
//                    } catch (UnsupportedEncodingException | MqttException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                //String topic = "myobd/testing";//.........................topic. change it to sensor topic
//                // String payload = "hi, this is the first obd payload";//...................payload. change it to sensor data
//
//                //uploadToMQTT();
//
//                // Indicate whether the work finished successfully with the Result
//                return Result.success();
//            }
//        }


//        final PeriodicWorkRequest periodicWorkRequest =
//                new PeriodicWorkRequest
//                        .Builder(UploadWorker.class, 15, TimeUnit.MINUTES)//change to minutes
//                        .build();


//        final Runnable mHandlerTask = new Runnable()
//        {
//            @Override
//            public void run() {
////                doSomething();
//                uploadToMQTT2();
//                mHandler.postDelayed(mHandlerTask, INTERVAL);
//            }
//        };

//        void startRepeatingTask()
//        {
//            mHandlerTask.run();
//        }
//
//        void stopRepeatingTask()
//        {
//            mHandler.removeCallbacks(mHandlerTask);
//        }


        publish_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myTimer.scheduleAtFixedRate(myTask, 0l, 1 * (60 * 1000)); // Runs every 5 mins

//                WorkManager                                               // .............................................this ..view this space
//                        .getInstance(getApplicationContext())
//                        .enqueue(periodicWorkRequest);
//                mHandlerTask.run();
//                Timer myTimer = new Timer("MyTimer", true);
//                myTimer.scheduleAtFixedRate(new MyTask(), 10000, 1000 * 60 * 2);
//                ComponentName componentName = new ComponentName(this, UploadScheduler.class); //(this, UploadScheduler.class);
//                JobInfo info = new JobInfo.Builder(123,componentName)
//                        .setPeriodic(5000)
//                        .setPersisted(true)
//                        .build();
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 100ms
//                        uploadToMQTT2();
//                    }
//                }, 3000);//120000

                //UploadWorker tempWorker = new UploadWorker(Context );
                //createConn();//do not remove this
                //Enqueuing the work request


//                String topic;
//                String payload;
//
//                String dataArr[]=mReadThread.getItems();
//                for (int i=0;i<dataArr.length; i++){
//
//                    StringTokenizer tokens = new StringTokenizer(dataArr[i], "/");
//
//                    topic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
//                    payload = tokens.nextToken();// this will contain " the sensor data"
//
//                    byte[] encodedPayload = new byte[0];
//                    try {
//                        encodedPayload = payload.getBytes("UTF-8");
//                        MqttMessage message = new MqttMessage(encodedPayload);
//                        client.publish(topic, message);
//                    } catch (UnsupportedEncodingException | MqttException e) {
//                        e.printStackTrace();
//                    }
//
//                }
                //String topic = "myobd/testing";//.........................topic. change it to sensor topic
                // String payload = "hi, this is the first obd payload";//...................payload. change it to sensor data

            }
        });

//        public void scheduleJob(View v){
//
//        }


        mBtnClearInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });


    }

//    public static void sentToScreen(){
//        mTxtSend.post(new Runnable() {
//            @Override
//            public void run() {
//                mTxtSend.append();//................the input
//)
//    }
//    private class MyTask extends TimerTask {
//
//        public void run(){
//            uploadToMQTT2();
//        }
//
//    }

    public void uploadToMQTT2() {
        String topic;
        String payload;

        String dataArr[] = mReadThread.getItems();
        for (int i = 0; i < dataArr.length; i++) {

            StringTokenizer tokens = new StringTokenizer(dataArr[i], "/");

            topic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
            payload = tokens.nextToken();// this will contain " the sensor data"

            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                final MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                //add code to write published items here .......................................................
                //mTxtSend.post(
                //\\mTxtSend.append("published: "+ message.toString());//);//................the input);

            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

        }
    }

    public static void uploadToMQTT() {
        String topic;
        String payload;

        String dataArr[] = mReadThread.getItems();
        for (int i = 0; i < dataArr.length; i++) {

            StringTokenizer tokens = new StringTokenizer(dataArr[i], "/");

            topic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
            payload = tokens.nextToken();// this will contain " the sensor data"

            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                final MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                //add code to write published items here .......................................................
                //mTxtSend.post(
//                        mTxtSend.append("published: "+ message.toString());//);//................the input);

            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

        }
        //String topic = "myobd/testing";//.........................topic. change it to sensor topic
        // String payload = "hi, this is the first obd payload";//...................payload. change it to sensor data

    }


//    public static class UploadWorker extends Worker {
//        public UploadWorker(
//                @NonNull  Context context,
//                @NonNull WorkerParameters params) {
//            super(context, params);
//        }
//        //Context alb=context;
//        @Override
//        public Result doWork() {
//
//            // Do the work here--in this case, upload the images.
//            uploadToMQTT();
//
//            // Indicate whether the work finished successfully with the Result
//            return Result.success();
//        }
//
//    }

    Timer myTimer = new Timer();
    TimerTask myTask = new TimerTask() {
        @Override
        public void run() {
            // your code
            uploadToMQTT2(); // Your method
        }
    };


    private class ReadInput implements Runnable {

        HashMap<String, Integer> topicMap = new HashMap<String, Integer>();//Creating HashMap to store topic and number
        HashMap<String, Integer> dataMap = new HashMap<String, Integer>();//Creating HashMap to store topic and sensor

        public String[] getItems() {// should be called with a scheduler
            String aggreItems[] = new String[topicMap.size()];
            int counter_ = 0;
            for (Map.Entry item : topicMap.entrySet()) {
                try {
                    //System.out.println(m.getKey()+" "+m.getValue());
                    aggreItems[counter_] = item.getKey() + "/" + dataMap.get(sensorTopic) / Integer.valueOf(item.getValue().toString());
                    counter_++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            topicMap.clear();//clears map
            dataMap.clear();//clears map
            counter_ = 0;//set counter to 0
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
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

//                        /*
//                         * This section handles the aggregation of data
//                         */
//
//                        tempSensorInput=strInput;
//
//                        StringTokenizer tokens = new StringTokenizer(tempSensorInput, "/");
//                         sensorTopic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
//                         sensorData = tokens.nextToken();// this will contain " the sensor data"
//                        Log.v("ViewDataScreen",strInput);
//
//                        if(!topicMap.containsKey(sensorTopic)){//if topic not part
//                            topicMap.put(sensorTopic,1);//place name and 1 in map
//                            dataMap.put(sensorTopic,Integer.parseInt(sensorData));// place data in map
//                        }else{
//                            topicMap.replace(sensorTopic,topicMap.get(sensorTopic)+1);//increment count
//                            dataMap.replace(sensorTopic,dataMap.get(sensorTopic)+Integer.parseInt(sensorData));//add curr sum
//                        }



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


                                        StringTokenizer tokens = new StringTokenizer(tempSensorInput, "/");
                                        sensorTopic = tokens.nextToken();// this will contain "everything before the / ie the topic info"
                                        try {
                                            sensorData = tokens.nextToken();// this will contain " the sensor data"
                                        } catch (Exception e) {
                                            sensorData = "0";
                                        }

                                        if (!topicMap.containsKey(sensorTopic)) {//if topic not part
                                            topicMap.put(sensorTopic, 1);//place name and 1 in map
                                            dataMap.put(sensorTopic, Integer.parseInt(sensorData));// place data in map
                                        } else {
                                            topicMap.put(sensorTopic, topicMap.get(sensorTopic) + 1);//increment count
                                            dataMap.put(sensorTopic, dataMap.get(sensorTopic) + Integer.parseInt(sensorData));//add curr sum
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
