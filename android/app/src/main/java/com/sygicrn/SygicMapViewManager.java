package com.sygicrn;

import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.sygic.aura.ResourceManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SygicMapViewManager extends SimpleViewManager<FrameLayout> {

    private final String TAG = "SygicMapViewManager";

    public final int COMMAND_CREATE = 1;

    public final String EVENT_TYPE_ON_STARTED = "onStarted";

    ReactApplicationContext mReactContext;

    public SygicMapViewManager(ReactApplicationContext reactContext) {
        super();
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return SygicMapViewManager.class.getSimpleName();
    }

    @Override
    public FrameLayout createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new FrameLayout(reactContext);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "create", COMMAND_CREATE
        );
    }

    @Override
    public void receiveCommand(FrameLayout root, String commandId, ReadableArray args) {
        super.receiveCommand(root, commandId, args);

        int commandIdInt = Integer.parseInt(commandId);
        int reactNativeViewId = args.getInt(0);

        Map<String, Boolean> eventsRegistrationMap = new HashMap<String, Boolean>();
        eventsRegistrationMap.put(EVENT_TYPE_ON_STARTED, args.getBoolean(1));

        switch (commandIdInt) {
            case COMMAND_CREATE:
                createFragment(root, reactNativeViewId, eventsRegistrationMap);
                break;
        }
    }

    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        EVENT_TYPE_ON_STARTED,
                        MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", EVENT_TYPE_ON_STARTED))
                )
                .build();
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return super.getExportedCustomDirectEventTypeConstants();
    }

    //////////////////////////////////////////////
    //  Private Staff
    //////////////////////////////////////////////

    private void createFragment(FrameLayout parentLayout, final int reactNativeViewId, final Map<String, Boolean> eventsRegistrationMap) {
        ResourceManager resourceManager = new ResourceManager(mReactContext, null);
        if (resourceManager.shouldUpdateResources()) {
            Toast.makeText(mReactContext, "Please wait while Sygic resources are being updated", Toast.LENGTH_LONG).show();
            resourceManager.updateResources(new ResourceManager.OnResultListener() {
                @Override
                public void onError(int errorCode, @NotNull String message) {
                    Log.e(TAG, "sygic error ["  + errorCode + "]:" + message);
                    Toast.makeText(mReactContext, "Error on loading Sygic", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess() {
                    initUI(parentLayout, reactNativeViewId, eventsRegistrationMap);
                }
            });
        } else {
            initUI(parentLayout, reactNativeViewId, eventsRegistrationMap);
        }
    }

    private void initUI(FrameLayout root, final int reactNativeViewId, Map<String, Boolean> eventsRegistrationMap) {
        setupLayoutHack(root);

        if (eventsRegistrationMap.get(EVENT_TYPE_ON_STARTED)) {
            SygicNavigationManager.getInstance().addOnStarted(new SygicNavigationManager.PendingAction() {
                @Override
                public void call() {
                    sendOnStartedDataToJS(reactNativeViewId);
                }
            });
        }

        SygicMapFragment smf = new SygicMapFragment();
        ((FragmentActivity) Objects.requireNonNull(mReactContext.getCurrentActivity())).getSupportFragmentManager()
                .beginTransaction().replace(reactNativeViewId, smf, String.valueOf(reactNativeViewId)).commit();
    }

    private void setupLayoutHack(final ViewGroup view) {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                manuallyLayoutChildren(view);
                view.getViewTreeObserver().dispatchOnGlobalLayout();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    private void manuallyLayoutChildren(ViewGroup view) {
        for (int i=0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            child.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY)
            );
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    private void sendOnStartedDataToJS(int reactNativeViewId) {
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                reactNativeViewId,
                EVENT_TYPE_ON_STARTED,
                null
        );
    }
}
