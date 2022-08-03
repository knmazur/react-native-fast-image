package com.dylanvann.fastimage;

import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_ERROR_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_END_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_EVENT;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PorterDuff;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.Request;
import com.dylanvann.fastimage.events.TopLoadEndEvent;
import com.dylanvann.fastimage.events.TopLoadEvent;
import com.dylanvann.fastimage.events.TopLoadStartEvent;
import com.dylanvann.fastimage.events.TopLoadProgressEvent;
import com.dylanvann.fastimage.events.TopSourceErrorEvent;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

public class FastImageViewManagerImpl {
    public static final String NAME = "FastImageView";

    private static final String REACT_ON_LOAD_START_EVENT = "onFastImageLoadStart";
    private static final String REACT_ON_PROGRESS_EVENT = "onFastImageProgress";
    private static final Map<String, List<FastImageViewWithUrl>> VIEWS_FOR_URLS = new WeakHashMap<>();

    public static void setSrc(FastImageViewWithUrl view, @Nullable ReadableMap source, RequestManager requestManager, FastImageProgressListener listener) {
        if (source == null || !source.hasKey("uri") || isNullOrEmpty(source.getString("uri"))) {
            // Cancel existing requests.
            clearView(view, requestManager);

            if (view.glideUrl != null) {
                FastImageOkHttpProgressGlideModule.forget(view.glideUrl.toStringUrl());
            }
            // Clear the image.
            view.setImageDrawable(null);
            return;
        }

        //final GlideUrl glideUrl = FastImageViewConverter.getGlideUrl(view.getContext(), source);
        final FastImageSource imageSource = FastImageViewConverter.getImageSource(view.getContext(), source);
        if (imageSource.getUri().toString().length() == 0) {
            ThemedReactContext context = (ThemedReactContext) view.getContext();
            int viewId = view.getId();
            WritableMap event = new WritableNativeMap();
            event.putString("message", "Invalid source prop:" + source);
            UIManagerHelper.getEventDispatcherForReactTag((ReactContext) context, viewId)
                    .dispatchEvent(new TopSourceErrorEvent(viewId, event));

            // Cancel existing requests.
            if (requestManager != null) {
                requestManager.clear(view);
            }

            if (view.glideUrl != null) {
                FastImageOkHttpProgressGlideModule.forget(view.glideUrl.toStringUrl());
            }
            // Clear the image.
            view.setImageDrawable(null);
            return;
        }

        final GlideUrl glideUrl = imageSource.getGlideUrl();

        // Cancel existing request.
        view.glideUrl = glideUrl;
        clearView(view, requestManager);

        String key = glideUrl.toStringUrl();
        FastImageOkHttpProgressGlideModule.expect(key, listener);
        List<FastImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null && !viewsForKey.contains(view)) {
            viewsForKey.add(view);
        } else if (viewsForKey == null) {
            List<FastImageViewWithUrl> newViewsForKeys = new ArrayList<>(Collections.singletonList(view));
            VIEWS_FOR_URLS.put(key, newViewsForKeys);
        }

        ThemedReactContext context = (ThemedReactContext) view.getContext();
        int viewId = view.getId();
        UIManagerHelper.getEventDispatcherForReactTag((ReactContext) context, viewId)
                .dispatchEvent(new TopLoadStartEvent(viewId, new WritableNativeMap()));

        if (requestManager != null) {
            requestManager
                    // This will make this work for remote and local images. e.g.
                    //    - file:///
                    //    - content://
                    //    - res:/
                    //    - android.resource://
                    //    - data:image/png;base64
                    .load(imageSource.getSourceForLoad())
                    .apply(FastImageViewConverter.getOptions(context, imageSource, source))
                    .listener(new FastImageRequestListener(key))
                    .into(view);
        }
    }

    public static void setTintColor(FastImageViewWithUrl view, @Nullable Integer color) {
        if (color == null) {
            view.clearColorFilter();
        } else {
            view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setResizeMode(FastImageViewWithUrl view, String resizeMode) {
        final FastImageViewWithUrl.ScaleType scaleType = FastImageViewConverter.getScaleType(resizeMode);
        view.setScaleType(scaleType);
    }

    public static void onDropViewInstance(FastImageViewWithUrl view, RequestManager requestManager) {
        // This will cancel existing requests.
        clearView(view, requestManager);

        if (view.glideUrl != null) {
            final String key = view.glideUrl.toString();
            FastImageOkHttpProgressGlideModule.forget(key);
            List<FastImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
            if (viewsForKey != null) {
                viewsForKey.remove(view);
                if (viewsForKey.size() == 0) VIEWS_FOR_URLS.remove(key);
            }
        }
    }

    public static Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(TopLoadStartEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_START_EVENT))
                .put(TopLoadProgressEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_PROGRESS_EVENT))
                .put(TopLoadEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_EVENT))
                .put(TopSourceErrorEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_ERROR_EVENT))
                .put(TopLoadEndEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_END_EVENT))
                .build();
    }

    public static void onProgress(String key, long bytesRead, long expectedLength) {
        List<FastImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null) {
            for (FastImageViewWithUrl view : viewsForKey) {
                WritableMap event = new WritableNativeMap();
                event.putInt("loaded", (int) bytesRead);
                event.putInt("total", (int) expectedLength);
                ThemedReactContext context = (ThemedReactContext) view.getContext();
                int viewId = view.getId();
                UIManagerHelper.getEventDispatcherForReactTag((ReactContext) context, viewId)
                        .dispatchEvent(new TopLoadProgressEvent(viewId, event));
            }
        }
    }

    public static float getGranularityPercentage() {
        return 0.5f;
    }

    private static boolean isNullOrEmpty(final String url) {
        return url == null || url.trim().isEmpty();
    }

    public static boolean isValidContextForGlide(final Context context) {
        Activity activity = getActivityFromContext(context);

        if (activity == null) {
            return false;
        }

        return !isActivityDestroyed(activity);
    }

    private static Activity getActivityFromContext(final Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }

        if (context instanceof ThemedReactContext) {
            final Context baseContext = ((ThemedReactContext) context).getBaseContext();
            if (baseContext instanceof Activity) {
                return (Activity) baseContext;
            }

            if (baseContext instanceof ContextWrapper) {
                final ContextWrapper contextWrapper = (ContextWrapper) baseContext;
                final Context wrapperBaseContext = contextWrapper.getBaseContext();
                if (wrapperBaseContext instanceof Activity) {
                    return (Activity) wrapperBaseContext;
                }
            }
        }

        return null;
    }

    private static boolean isActivityDestroyed(Activity activity) {
        return activity.isDestroyed() || activity.isFinishing() || activity.isChangingConfigurations();
    }

    private static void clearView(FastImageViewWithUrl view, RequestManager requestManager) {
        if (requestManager != null && view != null && view.getTag() != null && view.getTag() instanceof Request) {
            requestManager.clear(view);
        }
    }
}