package com.tinakit.android.rssfeedreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tina on 11/28/2014.
 */
class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference imageViewReference;

    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference(imageView);
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = (ImageView)imageViewReference.get();
            if (imageView != null) {

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(imageView.getContext().getResources()
                            .getDrawable(R.drawable.list_placeholder_smile));
                }
            }

        }
    }

    static Bitmap downloadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            URL feedImage = new URL(url);
            conn = (HttpURLConnection)feedImage.openConnection();
            if (!url.toString().equalsIgnoreCase("null")) {
                InputStream inputStream = null;
                try {
                    inputStream = conn.getInputStream();
                    Bitmap img = BitmapFactory.decodeStream(inputStream);
                    return img;
                }finally {
                    if (inputStream != null)
                        inputStream.close();
                }
            }

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            Log.d("ImageDownloader", "MalformedURLException: " + url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ImageDownloader", "Error while retrieving bitmap from " + url);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;

    }

}
