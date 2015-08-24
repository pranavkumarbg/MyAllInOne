package com.rhcloud.phpnew_pranavkumar.myallinone;

/**
 * Created by Pranav on 8/16/2015.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.rhcloud.phpnew_pranavkumar.myallinone.data.MyContract;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyAlarmService extends Service {
    private ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
    Cursor cursor;
    String o;
    FeedItem item;
    String[] mProjection =
            {
                    //MyContract.MyEntry._ID,    // Contract class constant for the _ID column name
                    MyContract.MyEntry.COLUMN_IMAGE,   // Contract class constant for the word column name
                    // Contract class constant for the locale column name
            };

    // Defines a string to contain the selection clause
    String mSelectionClause = null;

    // Initializes an array to contain selection arguments
    String[] mSelectionArgs = {""};
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        final int currentId = startId;

        Runnable r = new Runnable() {
            public void run() {

               // for (int i = 0; i < 3; i++) {
                   // long endTime = System.currentTimeMillis() + 10 * 1000;

                   // while (System.currentTimeMillis() < endTime)
                   // {
                        synchronized (this)
                        {
                            try
                            {
                                sendHttp();
                                //Log.i("service", "send http");
                               // wait(endTime - System.currentTimeMillis());
                            }
                            catch (Exception e)
                            {

                            }
                        }
                    //}
                    // Log.i("service","running");


                //}
                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();
        return Service.START_STICKY;
    }

    private void sendHttp() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://newjson-pranavkumar.rhcloud.com/GridViewJson")
                .build();


        try {
            Response response = okHttpClient.newCall(request).execute();


            String json = response.body().string();


            JSONObject reader = new JSONObject(json);
            JSONArray jsonarray = reader.getJSONArray("images");

            for (int i = 0; i < jsonarray.length(); i++)
            {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                item = new FeedItem();

                item.setThumbnail(jsonobject.optString("image"));
               // feedItemList.add(item);
               // String h=jsonobject.optString("image");
                //Log.i("images-json",h);
                String s = item.getThumbnail();

                //Log.i("images-feeditem",s);
                mSelectionClause = MyContract.MyEntry.COLUMN_IMAGE + " = ?";

                // Moves the user's input string to the selection arguments.
                mSelectionArgs[0] = s;


                Cursor mCursor = getContentResolver().query(
                        MyContract.MyEntry.CONTENT_URI,  // The content URI of the words table
                        mProjection,                       // The columns to return for each row
                        mSelectionClause,                  // Either null, or the word the user entered
                        mSelectionArgs,                    // Either empty, or the string the user entered
                        null);                       // The sort order for the returned rows

                // Some providers return null if an error occurs, others throw an exception
                if (null == mCursor)
                {

                    // If the Cursor is empty, the provider found no matches

                } else if (mCursor.getCount() < 1)
                {


                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyContract.MyEntry.COLUMN_IMAGE, s);
                    getApplication().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI, contentValues);

                    Log.i("servicei", "inserted new record");
                    //cursor.close();

                   
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("My All In One App")
                                    .setContentText("New images added");
// Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());



                } else
                {
                    // Insert code here to do something with the results
                    Log.i("service", "record is present");
                    // cursor.close();
                }


            }





        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {

        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}