package com.qb.xrealsys.ifafu.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by sky on 11/02/2018.
 */

public class GlobalLib {

    public static float GetRawSize(Activity activity, int unit, float value) {
        Resources res = activity.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    public static boolean CompareUtfWithGbk(String utf8, String gbk) throws IOException {
        return URLEncoder.encode(gbk, "gbk").contains(URLEncoder.encode(utf8, "gbk"));
    }

    public static BitmapDrawable BitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
