package com.dylanvann.fastimage;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.viewmanagers.FastImageViewManagerDelegate;
import com.facebook.react.viewmanagers.FastImageViewManagerInterface;

import java.util.Map;

import javax.annotation.Nullable;

@ReactModule(name = FastImageViewManagerImpl.NAME)
class FastImageViewManager extends SimpleViewManager<FastImageViewWithUrl>
        implements FastImageProgressListener, FastImageViewManagerInterface<FastImageViewWithUrl> {

    private final ViewManagerDelegate<FastImageViewWithUrl> mDelegate;

    public FastImageViewManager() {
        mDelegate = new FastImageViewManagerDelegate<>(this);
    }

    @Nullable
    @Override
    protected ViewManagerDelegate<FastImageViewWithUrl> getDelegate() {
        return mDelegate;
    }

    @Nullable
    private RequestManager requestManager = null;

    @Override
    public String getName() {
        return FastImageViewManagerImpl.NAME;
    }

    @Override
    protected FastImageViewWithUrl createViewInstance(ThemedReactContext reactContext) {
        if (FastImageViewManagerImpl.isValidContextForGlide(reactContext)) {
            requestManager = Glide.with(reactContext);
        }

        return new FastImageViewWithUrl(reactContext);
    }

    @Override
    @ReactProp(name = "source")
    public void setSource(FastImageViewWithUrl view, @Nullable ReadableMap source) {
        FastImageViewManagerImpl.setSrc(view,source,requestManager,this);
    }

    @Override
    @ReactProp(name = "tintColor", customType = "Color")
    public void setTintColor(FastImageViewWithUrl view, int color) {
        FastImageViewManagerImpl.setTintColor(view,color);
    }

    @Override
    @ReactProp(name = "resizeMode")
    public void setResizeMode(FastImageViewWithUrl view, String resizeMode) {
        FastImageViewManagerImpl.setResizeMode(view,resizeMode);
    }

    @Override
    public void onDropViewInstance(FastImageViewWithUrl view) {
        FastImageViewManagerImpl.onDropViewInstance(view,requestManager);
        super.onDropViewInstance(view);
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return FastImageViewManagerImpl.getExportedCustomDirectEventTypeConstants();
    }

    @Override
    public void onProgress(String key, long bytesRead, long expectedLength) {
        FastImageViewManagerImpl.onProgress(key,bytesRead,expectedLength);
    }

    @Override
    public float getGranularityPercentage() {
       return FastImageViewManagerImpl.getGranularityPercentage();
    }
}