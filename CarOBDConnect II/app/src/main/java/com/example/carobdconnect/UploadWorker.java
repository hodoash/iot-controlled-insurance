//package com.example.carobdconnect;
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//public class UploadWorker extends Worker {
//
//    private static final String TAG = "UploadWorker";
//
//    public UploadWorker(
//            @NonNull Context context,
//            @NonNull WorkerParameters params) {
//        super(context, params);
//    }
//    //Context alb=context;
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        // Do the work here--in this case, upload the images.
//        Log.v(TAG, "hero");
//        ViewDataScreen.uploadToMQTT();
//        // Indicate whether the work finished successfully with the Result
////        if (Result.success().equals(true)) {
////            ViewDataScreen.sendToScreen();
////        }
//        return Result.success();
//    }
//
//}