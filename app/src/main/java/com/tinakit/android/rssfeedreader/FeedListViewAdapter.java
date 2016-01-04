package com.tinakit.android.rssfeedreader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tina on 11/27/2014.
 */
public class FeedListViewAdapter extends ArrayAdapter<RssFeedItem> {
    private ArrayList<RssFeedItem> listData = null;
    private Context mContext;

    public FeedListViewAdapter(Context context, int resourceId, ArrayList<RssFeedItem> items) {
        super(context, resourceId, items);
        mContext = context;
        listData = items;
    }

    static class ViewHolder {
        TextView dateTextView;
        TextView titleTextView;
        ImageView thumbImageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RssFeedItem rssFeedItem = listData.get(position);
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.feed_list_item, null);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.date_text_view);
            holder.thumbImageView = (ImageView) convertView.findViewById(R.id.thumb_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTextView.setText(rssFeedItem.getTitle());
        holder.dateTextView.setText(rssFeedItem.getDate());

        if (holder.thumbImageView != null) {
            new ImageDownloaderTask(holder.thumbImageView).execute(rssFeedItem.getImageUrl());

            Picasso.with(mContext)
                    .load(rssFeedItem.getImageUrl())
                    .into(holder.thumbImageView);
        }

        return convertView;
    }


}

