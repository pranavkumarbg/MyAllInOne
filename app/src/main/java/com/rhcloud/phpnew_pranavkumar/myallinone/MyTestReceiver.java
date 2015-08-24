package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Pranav on 8/20/2015.
 */
public class MyTestReceiver extends ResultReceiver {
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    private Receiver mReceiver;


    public MyTestReceiver(Handler handler) {
        super(handler);
    }


    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }


    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }



    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}

