package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rhcloud.phpnew_pranavkumar.myallinone.data.MyContract;

/**
 * Created by Pranav on 8/15/2015.
 */
public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleCursorRecyclerAdapter.SimpleViewHolder> {
    private Context mContext;
    private  Cursor cursor;
    private static int sLoaderID;
    public SimpleCursorRecyclerAdapter(Context context,Cursor c,int flags, int loaderID) {
        super(context, c, flags);
        this.mContext=context;
        this.sLoaderID=loaderID;
        //Log.i("imageshiiiiiiii", "coming");
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, Cursor cursor) {
//        Log.i("imageshiiiiiiii", "bbbb");
//        MyDbHelper dbHelper=new MyDbHelper(mContext);
//        String selectQuery = "SELECT  * FROM image";
//        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
//        Cursor cursorq = sqliteDatabase.rawQuery(selectQuery, null);
//        // Above given query, read all the columns and fields of the table
//
//
//
//        // Cursor object read all the fields. So we make sure to check it will not miss any by looping through a while loop
//        while (cursorq.moveToNext()) {
//
//            String f=cursor.getString(1);
//            Log.i("imageshiiiiiiii", f);
//        }

        int versionIndex = cursor.getColumnIndex(MyContract.MyEntry.COLUMN_IMAGE);
        //Log.i("imageshiiiiiiii", "hi" + versionIndex);
        final String versionName = cursor.getString(versionIndex);
        //Log.i("imageshiiiiiiii", versionName);
        Glide.with(mContext).load(versionName).into(holder.imgThumbnail);
       // Log.i("image","loaded");
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {

        return super.swapCursor(newCursor);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.grid_item, parent, false);
        return new SimpleViewHolder(sView);
    }


    class SimpleViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView imgThumbnail;
        public SimpleViewHolder (View itemView)
        {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);

        }
    }
}
