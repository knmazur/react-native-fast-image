package com.dylanvann.fastimage;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;

public class FastImageViewModule extends NativeFastImageSpec {
    private ReactApplicationContext context;

    public FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return FastImageViewModuleImpl.NAME;
    }

    @Override
    public void preload(final ReadableArray sources) {
        FastImageViewModuleImpl.preload(sources, context);
    }

    @Override
    public void clearMemoryCache(final Promise promise) {
        FastImageViewModuleImpl.clearMemoryCache(promise, context);
    }

    @Override
    public void clearDiskCache(Promise promise) {
        FastImageViewModuleImpl.clearDiskCache(promise, context);
    }
}