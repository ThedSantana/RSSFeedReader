package com.tinakit.android.rssfeedreader;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ReaderActivity extends ListActivity {

    ArrayList<RssFeedItem> mRssFeedItems = new ArrayList<RssFeedItem>();
    static final String URL_RSS_FEED = "http://vids.kvie.org/program/pbs-kids-previews/rss/";
    boolean[] valueIsSet = new boolean[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        new ReaderAsyncTask().execute();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                RssFeedItem rssFeedItem = (RssFeedItem)getListAdapter().getItem(position);
                if (!rssFeedItem.getUrl().isEmpty()){
                    Uri uri = Uri.parse(rssFeedItem.getUrl().toString());
                    startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
                }
            }
        });
    }



    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        try {
            event = myParser.getEventType();
            RssFeedItem rssFeedItem = new RssFeedItem();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("title")){
                            rssFeedItem.setTitle(text);
                            valueIsSet[0] = true;
                        }
                        else if(name.equals("link")){
                            rssFeedItem.setUrl(text);
                            valueIsSet[1] = true;
                        }
                        else if(name.equals("description")){
                            rssFeedItem.setContent(text);
                            valueIsSet[2] = true;
                        }
                        else if(name.equals("pubDate")){
                            rssFeedItem.setDate(formatDateTime(text));
                            valueIsSet[3] = true;
                        }
                        else if(name.equals("guid")){
                            rssFeedItem.setId(text);
                            valueIsSet[4] = true;
                        }
                        else if(name.equals("media:thumbnail")) {
                            rssFeedItem.setImageUrl(text);
                            valueIsSet[5] = true;
                        }
                        else{
                        }
                        break;
                }
                if (checkRssFeedItem()){
                    mRssFeedItems.add(rssFeedItem);
                    rssFeedItem = new RssFeedItem();
                    clearCheckRssFeedItem();
                }

                event = myParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String formatDateTime(String dateString){
        try{
            Date date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss -SSSS", Locale.ENGLISH).parse(dateString);
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return "";
    }

    public void clearCheckRssFeedItem(){
        for(int i = 0; i < valueIsSet.length; i ++){
            valueIsSet[i] = false;
        }
    }

    public boolean checkRssFeedItem(){
        int count = 0;
        for(boolean b:valueIsSet){
            if(b == true)
                count++;
        }
        if (count == valueIsSet.length)
            return true;
        else
            return false;

    }
    private class ReaderAsyncTask extends AsyncTask<String,String,ArrayList<RssFeedItem>> {
        @Override
        protected ArrayList<RssFeedItem> doInBackground(String... strings) {

            try {
                URL url = new URL(URL_RSS_FEED);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                InputStream stream = conn.getInputStream();
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlPullParser.setInput(stream, null);
                parseXMLAndStoreIt(xmlPullParser);
                stream.close();
                } catch (Exception e) {
                e.printStackTrace();
                }

            return mRssFeedItems;

       }


        @Override
        protected void onPostExecute(ArrayList<RssFeedItem> items) {

            mRssFeedItems = items;
            setupAdapter();

        }

    }

    void setupAdapter(){
        if(getListView() == null) return;

        if (mRssFeedItems != null){
            FeedListViewAdapter adapter =
                    new FeedListViewAdapter(ReaderActivity.this,
                            R.layout.feed_list_item,
                            mRssFeedItems);
            setListAdapter(adapter);
        }else
            setListAdapter(null);


    }

}
