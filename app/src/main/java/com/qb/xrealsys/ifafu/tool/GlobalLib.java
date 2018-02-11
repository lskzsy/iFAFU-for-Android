package com.qb.xrealsys.ifafu.tool;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by sky on 11/02/2018.
 */

public class GlobalLib {

    public static float GetRawSize(Activity activity, int unit, float value) {
        Resources res = activity.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }
}
