package com.rhcloud.phpnew_pranavkumar.myallinone.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.rhcloud.phpnew_pranavkumar.myallinone.FeedItem;
import com.rhcloud.phpnew_pranavkumar.myallinone.MainActivity;
import com.rhcloud.phpnew_pranavkumar.myallinone.R;
import com.rhcloud.phpnew_pranavkumar.myallinone.data.MyContract;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 1;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
    String json;
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

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://newjson-pranavkumar.rhcloud.com/GridViewJson")
                .build();


        try {
            Response response = okHttpClient.newCall(request).execute();


            json = response.body().string();


            JSONObject reader = new JSONObject(json);
            JSONArray jsonarray = reader.getJSONArray("images");

            for (int i = 0; i < jsonarray.length(); i++)
            {

                JSONObject jsonobject = jsonarray.getJSONObject(i);

                item = new FeedItem();

                item.setThumbnail(jsonobject.optString("image"));
                feedItemList.add(item);

                // String h=jsonobject.optString("image");
                //Log.i("images-json",h);


                String s = item.getThumbnail();

                //Log.i("images-feeditem",s);
                mSelectionClause = MyContract.MyEntry.COLUMN_IMAGE + " = ?";

                // Moves the user's input string to the selection arguments.
                mSelectionArgs[0] = s;


                Cursor mCursor = getContext().getContentResolver().query(
                        MyContract.MyEntry.CONTENT_URI,  // The content URI of the words table
                        mProjection,                       // The columns to return for each row
                        mSelectionClause,                  // Either null, or the word the user entered
                        mSelectionArgs,                    // Either empty, or the string the user entered
                        null);                       // The sort order for the returned rows

                // Some providers return null if an error occurs, others throw an exception
                if (null == mCursor) {

                    // If the Cursor is empty, the provider found no matches

                    ContentValues[] flavorValuesArr = new ContentValues[feedItemList.size()];
                    // Loop through static array of Flavors, add each to an instance of ContentValues
                    // in the array of ContentValues

                    // Log.i("imagesffff", "");
                    for (int j = 0; j < feedItemList.size(); j++) {
                        FeedItem sk = feedItemList.get(j);
                        //Log.i("images", s.getThumbnail());
                        flavorValuesArr[j] = new ContentValues();
                        flavorValuesArr[j].put(MyContract.MyEntry.COLUMN_IMAGE, sk.getThumbnail());

                        //Log.i("images", "iterating" + flavorValuesArr[i]);
                        // getApplication().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI,flavorValuesArr[i]);


                    }

                    getContext().getContentResolver().bulkInsert(MyContract.MyEntry.CONTENT_URI, flavorValuesArr);
                    notifyme();


                } else if (mCursor.getCount() < 1) {


                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyContract.MyEntry.COLUMN_IMAGE, s);
                    getContext().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI, contentValues);

//                    for(int j = 0; j < feedItemList.size(); j++)
//                    {
//
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(MyContract.MyEntry.COLUMN_IMAGE, s);
//                    getContext().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI, contentValues);
//
//
//                    }

                    Log.i("servicei", "inserted new record");
                    //cursor.close();


                    notifyme();


                } else {
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

    private void notifyme() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My All In One App")
                        .setContentText("New images added");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getContext(), MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
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
                (NotificationManager)getContext(). getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }





}