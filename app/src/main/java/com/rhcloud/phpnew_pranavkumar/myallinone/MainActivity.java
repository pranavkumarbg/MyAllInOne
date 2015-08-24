package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.rhcloud.phpnew_pranavkumar.myallinone.data.MyContract;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,MyTestReceiver.Receiver {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SimpleCursorRecyclerAdapter mAdapter;
    private ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
    String json;
    Cursor c;
    private static final int CURSOR_LOADER_ID = 0;
    public MyTestReceiver receiverForTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SyncAdapter.syncImmediately(this);
        c = this.getContentResolver().query(MyContract.MyEntry.CONTENT_URI,
                        new String[]{MyContract.MyEntry._ID},
                        null,
                        null,
                        null);

        if (c.getCount() == 0){
            insertData();
           // Log.i("insertdata", "insertdata");
        }
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupAdapter();


        startService();
//        receiverForTest = new MyTestReceiver(new Handler());
//        receiverForTest.setReceiver(this);
//        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MyIntentService.class);
//
//
//        /* Send optional extras to Download IntentService */
//        intent.putExtra("url", "urlkkkkkkk");
//        intent.putExtra("receiver", receiverForTest);
//        intent.putExtra("requestId", 101);
//
//
//        startService(intent);



    }



    private void setupAdapter()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        // StaggeredGridLayoutManager mLayoutManager1 = new StaggeredGridLayoutManager(2,1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SimpleCursorRecyclerAdapter(getApplicationContext(), null, 0, CURSOR_LOADER_ID);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void startService() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.DAY_OF_MONTH, 13);

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 48);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 10 * 1000;

        Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                10 * 1000, pendingIntent);

//        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
//        // Create a PendingIntent to be triggered when the alarm goes off
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Setup periodic alarm every 5 seconds
//        long firstMillis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
//        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }

    // insert data into database
    public void insertData(){

        new DownloadJSON().execute();
        //SyncAdapter.syncImmediately(this);
        //SyncAdapter.syncImmediately(this);
        //Log.i("sync", "insertdata");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new android.content.CursorLoader(this,MyContract.MyEntry.CONTENT_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);

        Log.i("onloadfinish","yeppe");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

      mAdapter.swapCursor(null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case MyIntentService.STATUS_RUNNING:


                //setProgressBarIndeterminateVisibility(true);
                break;
            case MyIntentService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                // setProgressBarIndeterminateVisibility(false);

                //Toast.makeText(this, "service stoped", Toast.LENGTH_LONG).show();


                String results1 = resultData.getString("result");

                //textView.setText("download complete");
                Toast.makeText(this, results1, Toast.LENGTH_LONG).show();


                break;
            case MyIntentService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
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
                    String f=jsonobject.optString("image");
                   // Log.i("images",f);
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

                    //Log.i("images", "iterating" + flavorValuesArr[i]);
                   // getApplication().getContentResolver().insert(MyContract.MyEntry.CONTENT_URI,flavorValuesArr[i]);


                }

                getApplication().getContentResolver().bulkInsert(MyContract.MyEntry.CONTENT_URI, flavorValuesArr);

                //getLoaderManager().restartLoader(CURSOR_LOADER_ID,null,MainActivity.this);
                //getLoaderManager().initLoader(CURSOR_LOADER_ID, null, MainActivity.this);


               // mAdapter = new SimpleCursorRecyclerAdapter(getApplicationContext(), null, 0, CURSOR_LOADER_ID);

               // mRecyclerView.setAdapter(mAdapter);


            } else {
                //Toast.makeText(getApplicationContext(), "server is down", Toast.LENGTH_LONG).show();

            }


        }


    }

}
