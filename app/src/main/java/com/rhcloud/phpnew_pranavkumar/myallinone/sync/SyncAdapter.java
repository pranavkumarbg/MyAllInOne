package com.rhcloud.phpnew_pranavkumar.myallinone.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rhcloud.phpnew_pranavkumar.myallinone.FeedItem;
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
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
    String json;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        new DownloadJSON().execute();

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

    private class DownloadJSON extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public String doInBackground(String... params) {


            //  String json = JSONfunctions.getJSONfromURL("http://newjson-pranavkumar.rhcloud.com/GridViewJson");

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url("http://newjson-pranavkumar.rhcloud.com/GridViewJson")
                    .build();


            try {
                Response response = okHttpClient.newCall(request).execute();


                json = response.body().string();


                JSONObject reader = new JSONObject(json);
                JSONArray jsonarray = reader.getJSONArray("images");

                for (int i = 0; i < jsonarray.length(); i++) {

                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    FeedItem item = new FeedItem();

                    item.setThumbnail(jsonobject.optString("image"));
                    feedItemList.add(item);


                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {

            }
            return json;
        }

        @Override
        protected void onPostExecute(String args) {



            if (args != null && !args.isEmpty()) {

                ContentValues[] flavorValuesArr = new ContentValues[feedItemList.size()];
                // Loop through static array of Flavors, add each to an instance of ContentValues
                // in the array of ContentValues

                // Log.i("imagesffff", "");
                for(int i = 0; i < feedItemList.size(); i++){
                    FeedItem s=feedItemList.get(i);
                    //Log.i("images", s.getThumbnail());
                    flavorValuesArr[i] = new ContentValues();
                    flavorValuesArr[i].put(MyContract.MyEntry.COLUMN_IMAGE, s.getThumbnail());

                    Log.i("images", "iterating" + flavorValuesArr[i]);
                    // getApplication().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI,flavorValuesArr[i]);


                }

                getContext().getContentResolver().bulkInsert(MyContract.MyEntry.CONTENT_URI, flavorValuesArr);



            } else {
                Toast.makeText(getContext(), "server is down", Toast.LENGTH_LONG).show();

            }


        }


    }
}