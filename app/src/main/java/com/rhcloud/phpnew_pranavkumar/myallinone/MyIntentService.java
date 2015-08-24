package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Pranav on 8/16/2015.
 */
public class MyIntentService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "DownloadService";

    public MyIntentService() {
        super(MyIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");


        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");


        Bundle bundle = new Bundle();


        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);


            try {
                //String results = downloadData(url);
                String results ="hiiiiiiiii";

                /* Sending result back to activity */
                if (null != results && results.length() > 0) {
                    bundle.putString("result", results);

                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {


                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }
}