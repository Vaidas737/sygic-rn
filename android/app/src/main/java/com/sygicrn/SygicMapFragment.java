package com.sygicrn;

import com.sygic.aura.embedded.SygicFragmentSupportV4;

public class SygicMapFragment extends SygicFragmentSupportV4 {

    @Override
    public void onResume() {
        startNavi();
        setCallback(SygicNavigationManager.getInstance().getSygicCallback());
        super.onResume();
    }
}
