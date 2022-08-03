package com.dylanvann.fastimage;

import android.os.Handler;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;


public class FastImageViewModuleImpl {
    public static final String NAME = "FastImageView";

    public static void preload(final ReadableArray sources, final ReactApplicationContext context) {
        if(!context.hasCurrentActivity()) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sources.size(); i++) {
                    final ReadableMap source = sources.getMap(i);
                    final FastImageSource imageSource = FastImageViewConverter.getImageSource(context.getApplicationContext(), source);

                    Glide
                            .with(context.getApplicationContext())
                            // This will make this work for remote and local images. e.g.
                            //    - file:///
                            //    - content://
                            //    - res:/
                            //    - android.resource://
                            //    - data:image/png;base64
                            .load(
                                    imageSource.isBase64Resource() ? imageSource.getSource() :
                                            imageSource.isResource() ? imageSource.getUri() : imageSource.getGlideUrl()
                            )
                            .apply(FastImageViewConverter.getOptions(context.getApplicationContext(), imageSource, source))
                            .preload();
                }
            }
        });
    }

    public static void clearMemoryCache(final Promise promise, final ReactApplicationContext context) {
        if (!context.hasCurrentActivity()) {
            promise.resolve(null);
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Glide.get(context.getApplicationContext()).clearMemory();
                promise.resolve(null);
            }
        });
    }

    public static void clearDiskCache(Promise promise, ReactApplicationContext context) {
        if (!context.hasCurrentActivity()) {
            promise.resolve(null);
            return;
        }

        Glide.get(context.getApplicationContext()).clearDiskCache();
        promise.resolve(null);
    }
}