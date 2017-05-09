package com.example.gustavo.chamada;

import android.view.View;
import android.view.ViewGroup;

/**
 *
 * Common methods for all views
 *
 */

public class ScreenUtils {

    /* This code is based on the one provided at:
    * http://stackoverflow.com/questions/6238881/how-to-disable-all-click-events-of-a-layout
    * */
    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (enabled)
            view.setAlpha((float) 1);
        else
            view.setAlpha((float) .5);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0 ; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
                view.setEnabled(enabled);
            }
        }
    }

}
