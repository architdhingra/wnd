package com.wnd.myapp.lenovate;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Sachin Kharb on 8/23/2016.
 */
public class VolleySingelton {
    private static VolleySingelton mInstance = null;
    private static Context context;
    private RequestQueue mRequestQ;
    private ImageLoader imageLoader;
    private VolleySingelton volleySingelton;

    private VolleySingelton(Context context) {

        this.context = context;
        this.mRequestQ = getRequestQ();

        imageLoader = new ImageLoader(mRequestQ,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingelton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingelton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQ() {

        if (mRequestQ == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQ = new RequestQueue(cache, network);
            mRequestQ.start();
        }
        return mRequestQ;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}