package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Pranav on 8/16/2015.
 */
public class MyReceiver extends BroadcastReceiver
{
    private static final int REQUEST_CODE=12345;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);

    }
}
