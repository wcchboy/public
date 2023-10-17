package com.wcch.android.utils;

import android.text.TextUtils;
import android.view.View;

import com.wcch.android.window.ActivityWindowManager;
import com.wcch.android.window.ProjectionWindowManager;
import com.wcch.android.window.SoftWindow;
import com.wcch.android.window.Window;

import java.util.List;

/**
 * @author created by Lzq
 * @time：2022/6/15
 * @Des：
 */
public class WindowFinder {

    private static WindowFinder finder;
    private List<Window> windowList = ProjectionWindowManager.getInstance().getWindowList();
    private List<SoftWindow> preSoftWindowList = ProjectionWindowManager.getInstance().getPerWindowList();
    private List<Window> publicWindowList = ActivityWindowManager.getInstance().getWindowList();

    private WindowFinder() {
    }

    public static WindowFinder getInstance() {
        synchronized (WindowFinder.class) {
            if (finder == null) {
                finder = new WindowFinder();
            }
            return finder;
        }
    }

    public SoftWindow getSoftWindowByDevId(String dev_id) {
        if (TextUtils.isEmpty(dev_id)) return null;
        List<Window> windowList;
        windowList = this.windowList;
        if (windowList == null || windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                if (TextUtils.equals(softWindow.getDev_id(), dev_id))
                    return softWindow;
            }
        }
        return null;
    }

    public SoftWindow getSoftWindowByTaskId(String taskId) {
        if (TextUtils.isEmpty(taskId)) return null;
        if (windowList == null || windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                if (TextUtils.equals(softWindow.getTaskModel().taskId, taskId))
                    return softWindow;
            }
        }
        return null;
    }

    public SoftWindow getSoftWindowFromPreList(String dev_id) {
        if (TextUtils.isEmpty(dev_id)) return null;
        if (preSoftWindowList == null || preSoftWindowList.size() == 0) return null;
        for (SoftWindow softWindow : preSoftWindowList) {
            if (TextUtils.equals(softWindow.getTaskModel().dev_id, dev_id))
                return softWindow;
        }
        return null;
    }

    public Window getWindowByView(View v) {
        if (v == null) return null;
        if (windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window.getFloatWindowView() == v) {
                return window;
            }
        }
        return null;
    }

    public Window getFocusWindow() {
        if (windowList == null || windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window.isFocus()) return window;
        }
        return null;
    }

    public Window getDefaultFocusWindow() {
        return windowList.get(0);
    }

    public void setWindowFocus(Window window) {
        if (windowList == null || windowList.size() == 0) return;
        for (Window w : windowList) {
            if (w == window) {
                w.setFocus(true);
            } else {
                w.setFocus(false);
            }
        }
    }
}
