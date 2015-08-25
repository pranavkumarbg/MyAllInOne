package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rhcloud.phpnew_pranavkumar.myallinone.sync.SyncAdapter;

/**
 * Created by Pranav on 8/16/2015.
 */
public class MyReceiver extends BroadcastReceiver
{
    private static final int REQUEST_CODE=12345;
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        Intent service1 = new Intent(context, MyAlarmService.class);
//        context.startService(service1);

//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        if (info != null) {
//            if (info.isConnected()) {
                //start service
                SyncAdapter.syncImmediately(context);

//            }
//            else
//            {
//                //stop service
//
//            }
      //  }



    }
}
