package com.webmyne.android.d_brain.ui.Listeners;

import com.webmyne.android.d_brain.ui.Widgets.TouchPanelBox;

/**
 * Created by priyasindkar on 10-09-2015.
 */
public interface OnPaneItemClickListener {
    public void onPanelItemSelection(TouchPanelBox touchPanelBox,String oldName, int positionInPanel, String panelId);
}
