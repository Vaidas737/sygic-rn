package com.sygicrn;

import android.util.Log;

import com.sygic.aura.embedded.IApiCallback;
import com.sygic.sdk.api.Api;
import com.sygic.sdk.api.events.ApiEvents;
import com.sygic.sdk.api.exception.GeneralException;


public class SygicNavigationManager {
    private static final String TAG = "SygicNavigationManager";

    private static final SygicNavigationManager mInstance = new SygicNavigationManager();
    private static final SygicNavigationCallback mCallback = new SygicNavigationCallback();

    private static PendingAction onStartedPendingAction;

    public interface PendingAction {
        void call();
    }

    public static SygicNavigationManager getInstance() {
        return mInstance;
    }

    public SygicNavigationCallback getSygicCallback() {
        return mCallback;
    }

    public String getVersion() {
        String version = null;
        try {
            version = Api.getApplicationVersion(0).getVersion();
        } catch (GeneralException e) {
            Log.e(TAG,"error on getVersion, code " + e.getCode(), e);
        } catch (Exception e) {
            Log.e(TAG, "error on getVersion", e);
        }
        return version;
    }

    public void addOnStarted(PendingAction action) {
        onStartedPendingAction = action;
    }

    /**
     * Callback receiver from Sygic Navigation
     */
    private static class SygicNavigationCallback implements IApiCallback {

        @Override
        public void onEvent(final int event, final String data) {
            switch (event) {
                case ApiEvents.EVENT_APP_STARTED:
                    if (onStartedPendingAction != null) {
                        onStartedPendingAction.call();
                        onStartedPendingAction = null;
                    }
                    break;
            }
        }

        @Override
        public void onServiceConnected() { }

        @Override
        public void onServiceDisconnected() { }
    }
}
