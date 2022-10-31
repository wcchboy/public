package com.wcch.android.environment;

import android.text.TextUtils;
import android.util.Log;

/**
 * ============================
 * Auther：wcch
 * Date：2022/10/30
 * Describe：
 * =============================
 */
public class EnvironmentManager {


    public enum VersionType {
        Test, Production
    }

    public static final VersionType VERSION_TYPE = VersionType.Test;

    private static EnvironmentManager INSTANCE = null;

    public static EnvironmentManager getInstance() {
        if (null == INSTANCE) {
            synchronized (EnvironmentManager.class) {
                if (null == INSTANCE)
                    INSTANCE = new EnvironmentManager();
            }
        }
        return INSTANCE;
    }

}
