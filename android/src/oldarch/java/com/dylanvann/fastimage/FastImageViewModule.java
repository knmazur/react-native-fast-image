package com.dylanvann.fastimage;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

class FastImageViewModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;

    FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return FastImageViewModuleImpl.NAME;
    }

    @ReactMethod
    public void preload(final ReadableArray sources) {
        FastImageViewModuleImpl.preload(sources, reactContext);
    }

    @ReactMethod
    public void clearMemoryCache(final Promise promise) {
        FastImageViewModuleImpl.clearMemoryCache(promise, reactContext);
    }

    @ReactMethod
    public void clearDiskCache(Promise promise) {
        FastImageViewModuleImpl.clearDiskCache(promise, reactContext);
    }
}