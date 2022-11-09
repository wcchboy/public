package com.wcch.android.environment;

/**
 * ============================
 * Auther：wcch
 * Date：2022/10/30
 * Describe：
 * =============================
 * @author admin
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
