package com.webmyne.android.d_brain.ui.Model;

/**
 * Created by priyasindkar on 28-10-2015.
 */
public class UserSettings {
    boolean isStartupEnabled;
    boolean isMainPowerOn;
    boolean isStartUpHangingBulb;

    public UserSettings() {
        isMainPowerOn = true;
    }

    public boolean isStartupEnabled() {
        return isStartupEnabled;
    }

    public void setIsStartupEnabled(boolean isStartupEnabled) {
        this.isStartupEnabled = isStartupEnabled;
    }

    public boolean isMainPowerOn() {
        return isMainPowerOn;
    }

    public void setIsMainPowerOn(boolean isMainPowerOn) {
        this.isMainPowerOn = isMainPowerOn;
    }

    public boolean isStartUpHangingBulb() {
        return isStartUpHangingBulb;
    }

    public void setIsStartUpHangingBulb(boolean isStartUpHangingBulb) {
        this.isStartUpHangingBulb = isStartUpHangingBulb;
    }
}
