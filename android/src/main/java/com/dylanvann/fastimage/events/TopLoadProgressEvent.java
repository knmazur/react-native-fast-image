package com.dylanvann.fastimage.events;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class TopLoadProgressEvent extends Event<TopLoadProgressEvent> {
    public static String EVENT_NAME = "topLoadProgress";

    private WritableMap eventData;

    public TopLoadProgressEvent(int viewId, WritableMap eventData) {
        super(viewId);
        this.eventData = eventData;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public boolean canCoalesce() {
        return false;
    }

    @Override
    public short getCoalescingKey() {
        return 0;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}