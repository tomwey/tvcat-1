package com.sunian.baselib.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * Created by fujun on 2018/4/8.
 * 键盘的管理类
 */

public class UtilInput {

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        if (context == null || view == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        context = null;
        view = null;
    }

    /**
     * Return whether soft input is visible.
     * <p>The minimum height is 200</p>
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(final Activity activity) {
        if (activity == null)
            return false;
        else
            return isSoftInputVisible(activity, 200);
    }

    /**
     * Return whether soft input is visible.
     *
     * @param activity             The activity.
     * @param minHeightOfSoftInput The minimum height of soft input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(final Activity activity,
                                             final int minHeightOfSoftInput) {
        return getContentViewInvisibleHeight(activity) >= minHeightOfSoftInput;
    }

    private static int getContentViewInvisibleHeight(Activity activity) {
        final View contentView = activity.findViewById(android.R.id.content);
        activity = null;
        Rect outRect = new Rect();
        contentView.getWindowVisibleDisplayFrame(outRect);
        return contentView.getBottom() - outRect.bottom;
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }

        String[] viewArray = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field filed;
        Object filedObject;

        for (String view : viewArray) {
            try {
                filed = inputMethodManager.getClass().getDeclaredField(view);
                if (!filed.isAccessible()) {
                    filed.setAccessible(true);
                }
                filedObject = filed.get(inputMethodManager);
                if (filedObject != null && filedObject instanceof View) {
                    View fileView = (View) filedObject;
                    if (fileView.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        filed.set(inputMethodManager, null); // 置空，破坏掉path to gc节点
                    } else {
                        break;// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        destContext = null;
    }
}
