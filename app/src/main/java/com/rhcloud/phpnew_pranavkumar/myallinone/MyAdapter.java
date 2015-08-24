package com.rhcloud.phpnew_pranavkumar.myallinone;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.rhcloud.phpnew_pranavkumar.myallinone.data.MyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav on 8/14/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private Context mContext;
    private Cursor cursor;

    public MyAdapter(Context applicationContext, ArrayList<FeedItem> feedItemList,Cursor c, int flags, int loaderID) {
        this.feedItemList = feedItemList;
        this.mContext = applicationContext;
        this.cursor=c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        try {

            //holder.spinner.setVisibility(View.VISIBLE);
            //Log.i("imageshiiiiiiii", "bbbb");
            String g=cursor.getColumnName(position);
           // Log.i("imageshiiiiiiii", g);
            int versionIndex = cursor.getColumnIndex(MyContract.MyEntry.COLUMN_IMAGE);
            //Log.i("imageshiiiiiiii", "hi"+versionIndex);
            final String versionName = cursor.getString(versionIndex);
           // Log.i("imageshiiiiiiii", "hi");
            //String url = feedItemList.get(position).getThumbnail();
            //Animation anim = AnimationUtils.loadAnimation(mContext,R.drawable.circular_progress_bar);
            Glide.with(mContext).load(versionName).into(holder.imgThumbnail);

//            Glide.with(mContext).load(versionName).into(new GlideDrawableImageViewTarget(holder.imgThumbnail)
//            {
//                @Override
//                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                    super.onResourceReady(resource, animation);
//                    //holder.spinner.setVisibility(View.GONE);
//                }
//
//
//            });

//            Glide.with(mContext) .load(versionName)
//                    .asBitmap()
//                    .placeholder(R.mipmap.ic_launcher)
//                    .into(new BitmapImageViewTarget(holder.imgThumbnail) {
//                        @Override
//                        public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
//                            super.onResourceReady(drawable, anim);
//                            holder.spinner.setVisibility(View.GONE);
//                        }
//                    });


//            Glide.with(mContext)
//                    .load(versionName)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            holder.spinner.setVisibility(View.GONE);
//                            return false;
//                        }
//                    })
//                    .into(holder.imgThumbnail);

        } catch (Exception e) {

        }
    }


    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView imgThumbnail;
        private ProgressBar spinner;

        public ViewHolder(View itemView) {
            super(itemView);


            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
           // spinner=(ProgressBar)itemView.findViewById(R.id.progressBar);
            //spinner.setVisibility(View.GONE);


        }

    }
}
