package com.wcch.android.soft.util;


import com.wcch.android.config.Config;
import com.wcch.android.window.Window;

import java.math.BigDecimal;

/**
 * @author created by Lzq
 * @time：2022/4/12
 * @Des：获取窗口默认位置信息
 */
public class WindowLocationUtil {
    /**
     * 获取初始化窗口大小和坐标
     *
     * @param windowCount    窗口数量
     * @param windowType     当前窗口类型
     * @param windowPosition 窗口位置
     * @param windowOri      窗口方向
     */
    public static Window.WindowLocation getWindowLocation(int windowCount, int windowType, int windowPosition, int windowOri, float screenRatio) {
        Window.WindowLocation windowLocation;
        if (windowType == Config.TYPE_ANDROID_PHONE) {
            windowLocation = getAndroidPhoneDefaultLocation(windowCount, windowPosition, windowOri, screenRatio);
        } else if (windowType == Config.TYPE_IOS_PHONE) {
            windowLocation = getIPhoneDefaultLocation(windowCount, windowPosition, windowOri, screenRatio);
        } else if (windowType == Config.TYPE_ANDROID_PAD) {
            windowLocation = getAndroidPadDefaultLocation(windowCount, windowPosition, windowOri, screenRatio);
        } else if (windowType == Config.TYPE_IOS_PAD) {
            windowLocation = getIosPadDefaultLocation(windowCount, windowPosition, windowOri, screenRatio);
        } else {
            windowLocation = getPcDefaultLocation(windowCount, windowPosition);
        }
        return windowLocation;
    }

    /**
     * 获取Android设备默认位置信息
     *
     * @param windowCount
     * @param windowPosition
     * @param windowOri
     */
    private static Window.WindowLocation getAndroidPhoneDefaultLocation(int windowCount, int windowPosition, int windowOri, float screenRatio) {
        if (windowCount <= 0) return null;
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        if (windowCount == 1) {
            if (windowOri == 0 || windowOri == 2) {
                //竖屏状态
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                //横屏状态
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_BIG_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x = (int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f);
            int y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else if (windowCount == 2) {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_BIG_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
            } else {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + Config.SCREEN_WIDTH / 2.0f);
            }
            y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else if (windowCount == 3) {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_SMALL_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else {
                x = (int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PHONE_DEFAULT_SMALL_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 2) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            } else {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        }
    }

    /**
     * 获取AndroidPad默认位置信息
     * @param windowCount
     * @param windowPosition
     * @param windowOri
     * @param screenRatio
     * @return
     */
    private static Window.WindowLocation getAndroidPadDefaultLocation(int windowCount, int windowPosition, int windowOri, float screenRatio) {
        if (windowCount <= 0) return null;
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        if (windowCount == 1) {
            if (windowOri == 0 || windowOri == 2) {
                //竖屏状态
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                //横屏状态
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_BIG_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x = (int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f);
            int y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else if (windowCount == 2) {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_BIG_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_BIG_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
            } else {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + Config.SCREEN_WIDTH / 2.0f);
            }
            y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else if (windowCount == 3) {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_SMALL_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else {
                x = (int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else {
            if (windowOri == 0 || windowOri == 2) {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_SMALL_WINDOW_VERTICAL_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() * screenRatio));
            } else {
                windowLocation.setCardViewWidth(Config.PAD_DEFAULT_SMALL_WINDOW_HOR_WIDTH);
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / screenRatio));
            }
            windowLocation.setWindowWidth(windowLocation.getCardViewWidth());
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 2) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            } else {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f + Config.SCREEN_WIDTH / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f + Config.SCREEN_HEIGHT / 2.0f);
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        }
    }
    /**
     * 获取IosPad默认位置信息
     * @param windowCount
     * @param windowPosition
     * @param windowOri
     * @param screenRatio
     * @return
     */
    private static Window.WindowLocation getIosPadDefaultLocation(int windowCount, int windowPosition, int windowOri, float screenRatio) {
        return getAndroidPadDefaultLocation(windowCount,windowPosition,windowOri,screenRatio);
    }

    /**
     * 获取iPhone默认位置信息
     *
     * @param windowCount
     * @param windowPosition
     * @param windowOri
     */
    private static Window.WindowLocation getIPhoneDefaultLocation(int windowCount, int windowPosition, int windowOri, float screenRatio) {
        return getAndroidPhoneDefaultLocation(windowCount, windowPosition, windowOri, screenRatio);
    }

    /**
     * 获取PC默认位置信息
     *
     * @param windowCount    当前窗口数量
     * @param windowPosition 窗口排序位置
     */
    private static Window.WindowLocation getPcDefaultLocation(int windowCount, int windowPosition) {
        if (windowCount <= 0) return null;
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        if (windowCount == 1) {
            windowLocation.setCardViewWidth(Config.PC_DEFAULT_BIG_WINDOW_WIDTH);
            windowLocation.setCardViewHeight((int) (Config.PC_DEFAULT_BIG_WINDOW_WIDTH * Config.RAT));
            windowLocation.setWindowWidth(Config.PC_DEFAULT_BIG_WINDOW_WIDTH);
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int x = (int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f);
            int y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setX(x);
            windowLocation.setY(y);
            int windowMargin = getWindowMargin(Config.TYPE_PC, 0, windowLocation.getCardViewWidth());
            windowLocation.setMargin(windowMargin);
            return windowLocation;
        } else if (windowCount == 2) {
            windowLocation.setCardViewWidth(Config.PC_DEFAULT_BIG_WINDOW_WIDTH);
            windowLocation.setCardViewHeight((int) (Config.PC_DEFAULT_BIG_WINDOW_WIDTH * Config.RAT));
            windowLocation.setWindowWidth(Config.PC_DEFAULT_BIG_WINDOW_WIDTH);
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int windowMargin = getWindowMargin(Config.TYPE_PC, 0, windowLocation.getCardViewWidth());
            windowLocation.setMargin(windowMargin);
            int y = (int) ((Config.SCREEN_HEIGHT - windowLocation.getWindowHeight()) / 2.0f);
            windowLocation.setY(y);
            int x;
            if (windowPosition == 0) {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f) - windowLocation.getWindowWidth()) / 2.0f);
            } else {
                x = (int) ((int) (((Config.SCREEN_WIDTH / 2.0f) - windowLocation.getWindowWidth()) / 2.0f) + Config.SCREEN_WIDTH / 2.0f);
            }
            windowLocation.setX(x);
            return windowLocation;
        }
        if (windowCount == 3) {
            windowLocation.setCardViewWidth(Config.PC_DEFAULT_SMALL_WINDOW_WIDTH);
            windowLocation.setCardViewHeight((int) (Config.PC_DEFAULT_SMALL_WINDOW_WIDTH * Config.RAT));
            windowLocation.setWindowWidth(Config.PC_DEFAULT_SMALL_WINDOW_WIDTH);
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int windowMargin = getWindowMargin(Config.TYPE_PC, 0, windowLocation.getCardViewWidth());
            windowLocation.setMargin(windowMargin);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + (Config.SCREEN_WIDTH / 2.0f));
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + (Config.SCREEN_WIDTH / 2.0f));
                y = (int) (((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f) + (Config.SCREEN_HEIGHT / 2.0f));
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        } else {
            windowLocation.setCardViewWidth(Config.PC_DEFAULT_SMALL_WINDOW_WIDTH);
            windowLocation.setCardViewHeight((int) (Config.PC_DEFAULT_SMALL_WINDOW_WIDTH * Config.RAT));
            windowLocation.setWindowWidth(Config.PC_DEFAULT_SMALL_WINDOW_WIDTH);
            windowLocation.setWindowHeight(windowLocation.getCardViewHeight() + Config.WINDOW_TOOL_BAR_HEIGHT);
            int windowMargin = getWindowMargin(Config.TYPE_PC, 0, windowLocation.getCardViewWidth());
            windowLocation.setMargin(windowMargin);
            int x, y;
            if (windowPosition == 0) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 1) {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + (Config.SCREEN_WIDTH / 2.0f));
                y = (int) ((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f);
            } else if (windowPosition == 2) {
                x = (int) ((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f);
                y = (int) (((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f) + (Config.SCREEN_HEIGHT / 2.0f));
            } else {
                x = (int) (((Config.SCREEN_WIDTH / 2.0f - windowLocation.getWindowWidth()) / 2.0f) + (Config.SCREEN_WIDTH / 2.0f));
                y = (int) (((Config.SCREEN_HEIGHT / 2.0f - windowLocation.getWindowHeight()) / 2.0f) + (Config.SCREEN_HEIGHT / 2.0f));
            }
            windowLocation.setX(x);
            windowLocation.setY(y);
            return windowLocation;
        }
    }


    /**
     * 等分模式窗口位置信息
     *
     * @param windowCount
     * @param windowPosition
     * @param window
     * @return dividerLine
     */
    public static Window.WindowLocation getDfModeLocation(int windowCount, int windowPosition, Window window) {
        if (windowCount <= 1) return null;
        switch (windowCount) {
            case 2:
                return get2CountWindowLocation(windowPosition, window);
            case 3:
                return get3CountWindowLocation(windowPosition, window);
            case 4:
                return get4CountWindowLocation(windowPosition, window);
        }
        return null;
    }

    private static Window.WindowLocation get2CountWindowLocation(int windowPosition, Window window) {
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        windowLocation.setWindowWidth((int) (Config.SCREEN_WIDTH / 2.0f - Config.DIVIDER_LINE_WIDTH));
        windowLocation.setWindowHeight((int) (Config.SCREEN_HEIGHT / 2.0f));
        windowLocation.setY((int) (Config.SCREEN_HEIGHT / 4.0f));
        if (windowPosition == 0) {
            windowLocation.setX(0);
        } else {
            windowLocation.setX((int) (Config.SCREEN_WIDTH / 2.0f + Config.DIVIDER_LINE_WIDTH * 2.0f));
        }

        if (window.getType() != Config.TYPE_PC) {
            //手机
            if (window.getOri() == 0 || window.getOri() == 2) {
                windowLocation.setCardViewHeight(windowLocation.getWindowHeight());
                windowLocation.setCardViewWidth((int) (windowLocation.getWindowHeight() / window.getScreenRatio()));
            } else {
                windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / window.getScreenRatio()));
            }

        } else {
            //PC
            windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
            windowLocation.setCardViewHeight((int) (windowLocation.getWindowWidth() * Config.RAT));
        }


        return windowLocation;
    }

    private static Window.WindowLocation get3CountWindowLocation(int windowPosition, Window window) {
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        windowLocation.setWindowWidth((int) (Config.SCREEN_WIDTH / 2.0f - Config.DIVIDER_LINE_WIDTH));
        windowLocation.setWindowHeight((int) (Config.SCREEN_HEIGHT / 2.0f - Config.DIVIDER_LINE_WIDTH));

        if (windowPosition == 0) {
            windowLocation.setX(0);
            windowLocation.setY(0);
        } else if (windowPosition == 1) {
            windowLocation.setX((int) (windowLocation.getWindowWidth() + Config.DIVIDER_LINE_WIDTH * 2.0f));
            windowLocation.setY(0);
//            if (window.getType() == Config.TYPE_IPHONE || window.getType() == Config.TYPE_ANDROID_PHONE) {
//                //手机
//                if (window.getOri() == 0 || window.getOri() == 2) {
//                    windowLocation.setCardViewHeight(Config.SCREEN_HEIGHT / 2);
//                    windowLocation.setCardViewWidth((int) (Config.SCREEN_HEIGHT / 2 / window.getScreenRatio()));
//                } else {
//                    windowLocation.setCardViewWidth(Config.SCREEN_WIDTH / 2);
//                    windowLocation.setCardViewHeight((int) (Config.SCREEN_WIDTH / 2 * window.getScreenRatio()));
//                }
//            } else {
//                //PC
//                windowLocation.setCardViewWidth(Config.SCREEN_WIDTH / 2);
//                windowLocation.setCardViewHeight((int) ((Config.SCREEN_WIDTH / 2) * Config.RAT));
//            }

        } else {
            windowLocation.setX((int) ((Config.SCREEN_WIDTH - windowLocation.getWindowWidth()) / 2.0f));
            windowLocation.setY(Config.SCREEN_HEIGHT - windowLocation.getWindowHeight());

//            if (window.getType() == Config.TYPE_IPHONE || window.getType() == Config.TYPE_ANDROID_PHONE) {
//                //手机
//                if (window.getOri() == 0 || window.getOri() == 2) {
//                    windowLocation.setCardViewHeight(Config.SCREEN_HEIGHT / 2);
//                    windowLocation.setCardViewWidth((int) (Config.SCREEN_HEIGHT / 2 / window.getScreenRatio()));
//                } else {
//                    windowLocation.setCardViewWidth(Config.SCREEN_WIDTH / 2);
//                    windowLocation.setCardViewHeight((int) (Config.SCREEN_WIDTH / 2 * window.getScreenRatio()));
//                }
//            } else {
//                //PC
//                windowLocation.setCardViewWidth(Config.SCREEN_WIDTH / 2);
//                windowLocation.setCardViewHeight((int) ((Config.SCREEN_WIDTH / 2) * Config.RAT));
//            }

        }

        if (window.getType() != Config.TYPE_PC) {
            //手机
            if (window.getOri() == 0 || window.getOri() == 2) {
                windowLocation.setCardViewHeight(windowLocation.getWindowHeight());
                windowLocation.setCardViewWidth((int) (windowLocation.getCardViewHeight() / window.getScreenRatio()));
            } else {
                windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / window.getScreenRatio()));
            }


        } else {
            //PC
            windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
            windowLocation.setCardViewHeight((int) (windowLocation.getWindowWidth() / Config.RAT));
        }
        return windowLocation;
    }

    private static Window.WindowLocation get4CountWindowLocation(int windowPosition, Window window) {
        Window.WindowLocation windowLocation = new Window.WindowLocation();
        windowLocation.setWindowWidth((int) (Config.SCREEN_WIDTH / 2.0f - Config.DIVIDER_LINE_WIDTH));
        windowLocation.setWindowHeight((int) (Config.SCREEN_HEIGHT / 2.0f - Config.DIVIDER_LINE_WIDTH));
        if (windowPosition == 0) {
            windowLocation.setX(0);
            windowLocation.setY(0);
        } else if (windowPosition == 1) {
            windowLocation.setX((int) (windowLocation.getWindowWidth() + Config.DIVIDER_LINE_WIDTH * 2.0f));
            windowLocation.setY(0);
        } else if (windowPosition == 2) {
            windowLocation.setX(0);
            windowLocation.setY((int) (windowLocation.getWindowHeight() + Config.DIVIDER_LINE_WIDTH * 2.0f));
        } else {
            windowLocation.setX((int) (windowLocation.getWindowWidth() + Config.DIVIDER_LINE_WIDTH * 2.0f));
            windowLocation.setY((int) (windowLocation.getWindowHeight() + Config.DIVIDER_LINE_WIDTH * 2.0f));
        }
        if (window.getType() != Config.TYPE_PC) {
            //手机
            if (window.getOri() == 0 || window.getOri() == 2) {
                windowLocation.setCardViewHeight(windowLocation.getWindowHeight());
                windowLocation.setCardViewWidth((int) (windowLocation.getCardViewHeight() / window.getScreenRatio()));
            } else {
                windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
                windowLocation.setCardViewHeight((int) (windowLocation.getCardViewWidth() / window.getScreenRatio()));
            }
        } else {
            //PC
            windowLocation.setCardViewWidth(windowLocation.getWindowWidth());
            windowLocation.setCardViewHeight((int) (windowLocation.getWindowWidth() / Config.RAT));
        }
        return windowLocation;
    }

    /**
     * 获取cardView的margin值
     * TODO 手机需要通过设备的宽高比来返回margin值
     *
     * @param windType
     * @param windowOri
     * @param currentWidth
     * @return 最大：64px  最小：18px
     */
    private Object object = new Object();
    private static final int PC_MIN_MARGIN = Config.is4K ? 18 : 9;
    private static final int PC_MAX_MARGIN = Config.is4K ? 62 : 31;

    private static final int ANDROID_PHONE_HOR_MIN_MARGIN = Config.is4K ? 13 : 5;
    private static final int ANDROID_PHONE_HOR_MAX_MARGIN = Config.is4K ? 32 : 16;
    private static final int ANDROID_PHONE_VERTICAL_MIN_MARGIN = Config.is4K ? 14 : 7;
    private static final int ANDROID_PHONE_VERTICAL_MAX_MARGIN = Config.is4K ? 32 : 16;

    private static final int IPHONE_PHONE_HOR_MIN_MARGIN = Config.is4K ? 14 : 7;
    private static final int IPHONE_PHONE_HOR_MAX_MARGIN = Config.is4K ? 50 : 25;
    private static final int IPHONE_PHONE_VERTICAL_MIN_MARGIN = Config.is4K ? 16 : 8;
    private static final int IPHONE_PHONE_VERTICAL_MAX_MARGIN = Config.is4K ? 34 : 17;

    public static int getWindowMargin(int windType, int windowOri, int currentWidth) {
        synchronized (Object.class) {
            int margin;
            if (windType == Config.TYPE_ANDROID_PHONE || windType == Config.TYPE_ANDROID_PAD) {
                int androidMinWidth, androidMaxWidth;
                if (windowOri == 0 || windowOri == 2) {
                    androidMinWidth = Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH;
                    androidMaxWidth = Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH;
                    if (currentWidth == androidMinWidth) return ANDROID_PHONE_VERTICAL_MIN_MARGIN;
                    if (currentWidth == androidMaxWidth) return ANDROID_PHONE_VERTICAL_MAX_MARGIN;
                    int deltaWidth = androidMaxWidth - androidMinWidth;
                    int deltaR = ANDROID_PHONE_VERTICAL_MAX_MARGIN - ANDROID_PHONE_VERTICAL_MIN_MARGIN;
                    int x = currentWidth - Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH;
                    BigDecimal a = new BigDecimal(x);
                    BigDecimal b = new BigDecimal(deltaWidth);
                    BigDecimal c = new BigDecimal(deltaR);

                    BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_DOWN).multiply(c);
                    margin = multiply.intValue() + ANDROID_PHONE_VERTICAL_MIN_MARGIN;
                } else {
                    androidMinWidth = Config.PHONE_WINDOW_HOR_MIN_WIDTH;
                    androidMaxWidth = Config.PHONE_WINDOW_HOR_MAX_WIDTH;
                    if (currentWidth == androidMinWidth) return ANDROID_PHONE_HOR_MIN_MARGIN;
                    if (currentWidth == androidMaxWidth) return ANDROID_PHONE_HOR_MAX_MARGIN;
                    int deltaWidth = androidMaxWidth - androidMinWidth;
                    int deltaR = ANDROID_PHONE_HOR_MAX_MARGIN - ANDROID_PHONE_HOR_MIN_MARGIN;
                    int x = currentWidth - Config.PHONE_WINDOW_HOR_MIN_WIDTH;
                    BigDecimal a = new BigDecimal(x);
                    BigDecimal b = new BigDecimal(deltaWidth);
                    BigDecimal c = new BigDecimal(deltaR);
                    BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_DOWN).multiply(c);
                    margin = multiply.intValue() + ANDROID_PHONE_HOR_MIN_MARGIN;
                }
            } else if (windType == Config.TYPE_IOS_PHONE || windType == Config.TYPE_IOS_PAD) {
                int androidMinWidth, androidMaxWidth;
                if (windowOri == 0 || windowOri == 2) {
                    androidMinWidth = Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH;
                    androidMaxWidth = Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH;
                    if (currentWidth == androidMinWidth) return IPHONE_PHONE_VERTICAL_MIN_MARGIN;
                    if (currentWidth == androidMaxWidth) return IPHONE_PHONE_VERTICAL_MAX_MARGIN;
                    int deltaWidth = androidMaxWidth - androidMinWidth;
                    int deltaR = IPHONE_PHONE_VERTICAL_MAX_MARGIN - IPHONE_PHONE_VERTICAL_MIN_MARGIN;
                    int x = currentWidth - Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH;
                    BigDecimal a = new BigDecimal(x);
                    BigDecimal b = new BigDecimal(deltaWidth);
                    BigDecimal c = new BigDecimal(deltaR);
                    BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_DOWN).multiply(c);
                    margin = multiply.intValue() + IPHONE_PHONE_VERTICAL_MIN_MARGIN;
                    margin = (margin > IPHONE_PHONE_VERTICAL_MAX_MARGIN) ? IPHONE_PHONE_VERTICAL_MAX_MARGIN : margin;
                } else {
                    androidMinWidth = Config.PHONE_WINDOW_HOR_MIN_WIDTH;
                    androidMaxWidth = Config.PHONE_WINDOW_HOR_MAX_WIDTH;
                    if (currentWidth == androidMinWidth) return IPHONE_PHONE_HOR_MIN_MARGIN;
                    if (currentWidth == androidMaxWidth) return IPHONE_PHONE_HOR_MAX_MARGIN;
                    int deltaWidth = androidMaxWidth - androidMinWidth;
                    int deltaR = IPHONE_PHONE_HOR_MAX_MARGIN - IPHONE_PHONE_HOR_MIN_MARGIN;
                    int x = currentWidth - Config.PHONE_WINDOW_HOR_MIN_WIDTH;
                    BigDecimal a = new BigDecimal(x);
                    BigDecimal b = new BigDecimal(deltaWidth);
                    BigDecimal c = new BigDecimal(deltaR);
                    BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_DOWN).multiply(c);
                    margin = multiply.intValue() + IPHONE_PHONE_HOR_MIN_MARGIN;
                    margin = (margin > IPHONE_PHONE_HOR_MIN_MARGIN) ? IPHONE_PHONE_HOR_MIN_MARGIN : margin;
                }
            } else {
                int pcMinWidth = Config.PC_WINDOW_MIN_WIDTH;
                int pcMaxWidth = Config.PC_WINDOW_MAX_WIDTH;
                if (currentWidth == Config.PC_WINDOW_MIN_WIDTH) return PC_MIN_MARGIN;
                if (currentWidth == Config.PC_WINDOW_MAX_WIDTH) return PC_MAX_MARGIN;

                int deltaWidth = pcMaxWidth - pcMinWidth;
                int deltaR = PC_MAX_MARGIN - PC_MIN_MARGIN;
                int x = currentWidth - pcMinWidth;
                BigDecimal a = new BigDecimal(x);
                BigDecimal b = new BigDecimal(deltaWidth);
                BigDecimal c = new BigDecimal(deltaR);
                BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_DOWN).multiply(c);
                margin = multiply.intValue() + PC_MIN_MARGIN;
            }
            return margin;
        }

    }


    /**
     * 动态获取圆角值
     *
     * @param windowType 窗口类型
     * @param windowOri 窗口方向
     * @param currentHeight 当前高度
     * @return
     */
    //
    //Android手机竖直方向radius
    private static final int ANDROID_PHONE_MIN_RADIUS_V = Config.is4K ? 56 : 28;
    private static final int ANDROID_PHONE_MAX_RADIUS_V = Config.is4K ? 70 : 35;
    //Android手机水平方向radius
    private static final int ANDROID_PHONE_MIN_RADIUS_H = Config.is4K ? 12 : 6;
    private static final int ANDROID_PHONE_MAX_RADIUS_H = Config.is4K ? 88 : 44;

    //Android手机竖直方向radius  //苹果手机竖屏 最小76--》128  横屏：76--》220
    private static final int IPHONE_PHONE_MIN_RADIUS_V = Config.is4K ? 36 : 18;
    private static final int IPHONE_PHONE_MAX_RADIUS_V = Config.is4K ? 60 : 30;
    //Android手机水平方向radius
    private static final int IPHONE_PHONE_MIN_RADIUS_H = Config.is4K ? 24 : 12;
    private static final int IPHONE_PHONE_MAX_RADIUS_H = Config.is4K ? 70 : 35;

    public static int getWindowRadius(int windowType, int windowOri, int currentHeight) {
        if (windowType == Config.TYPE_ANDROID_PHONE || windowType == Config.TYPE_ANDROID_PAD) {
            if (windowOri == 0 || windowOri == 2) {
                //竖屏
//                int phoneWindowMinHeightVertical = Config.PHONE_WINDOW_MIN_HEIGHT_VERTICAL;
//                int phoneWindowMaxHeightVertical = Config.PHONE_WINDOW_MAX_HEIGHT_VERTICAL;
//                if (currentHeight == phoneWindowMinHeightVertical)
//                    return ANDROID_PHONE_MIN_RADIUS_V;
//                if (currentHeight == phoneWindowMaxHeightVertical)
//                    return ANDROID_PHONE_MAX_RADIUS_V;
//                int deltaH = phoneWindowMaxHeightVertical - phoneWindowMinHeightVertical;
//                int deltaR = ANDROID_PHONE_MAX_RADIUS_V - ANDROID_PHONE_MIN_RADIUS_V;
//                int x = currentHeight - phoneWindowMinHeightVertical;
//                BigDecimal a = new BigDecimal(x);
//                BigDecimal b = new BigDecimal(deltaH);
//                BigDecimal c = new BigDecimal(deltaR);
//                BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(c);
//                int i = multiply.intValue() + ANDROID_PHONE_MIN_RADIUS_V;
                return 20;

            } else {
                //横屏
                int phoneWindowMinHeightVertical = Config.PHONE_WINDOW_MIN_HEIGHT_HORIZONTAL;
                int phoneWindowMaxHeightVertical = Config.PHONE_WINDOW_MAX_HEIGHT_HORIZONTAL;
                if (currentHeight == phoneWindowMinHeightVertical)
                    return ANDROID_PHONE_MIN_RADIUS_H;
                if (currentHeight == phoneWindowMaxHeightVertical)
                    return ANDROID_PHONE_MAX_RADIUS_H;
                int deltaH = phoneWindowMaxHeightVertical - phoneWindowMinHeightVertical;
                int deltaR = ANDROID_PHONE_MAX_RADIUS_H - ANDROID_PHONE_MIN_RADIUS_H;
                int x = currentHeight - phoneWindowMinHeightVertical;
                BigDecimal a = new BigDecimal(x);
                BigDecimal b = new BigDecimal(deltaH);
                BigDecimal c = new BigDecimal(deltaR);
                BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(c);
                int i = multiply.intValue() + ANDROID_PHONE_MIN_RADIUS_H;
                i = (i > ANDROID_PHONE_MAX_RADIUS_H) ? ANDROID_PHONE_MAX_RADIUS_H : i;
                return i;
            }
        } else if (windowType == Config.TYPE_IOS_PHONE || windowType == Config.TYPE_IOS_PAD) {
            if (windowOri == 0 || windowOri == 2) {
                //竖屏
                int phoneWindowMinHeightVertical = Config.PHONE_WINDOW_MIN_HEIGHT_VERTICAL;
                int phoneWindowMaxHeightVertical = Config.PHONE_WINDOW_MAX_HEIGHT_VERTICAL;
                if (currentHeight == phoneWindowMinHeightVertical)
                    return IPHONE_PHONE_MIN_RADIUS_V;
                if (currentHeight == phoneWindowMaxHeightVertical)
                    return IPHONE_PHONE_MAX_RADIUS_V;
                int deltaH = phoneWindowMaxHeightVertical - phoneWindowMinHeightVertical;
                int deltaR = IPHONE_PHONE_MAX_RADIUS_V - IPHONE_PHONE_MIN_RADIUS_V;
                int x = currentHeight - phoneWindowMinHeightVertical;
                BigDecimal a = new BigDecimal(x);
                BigDecimal b = new BigDecimal(deltaH);
                BigDecimal c = new BigDecimal(deltaR);
                BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(c);
                int i = multiply.intValue() + IPHONE_PHONE_MIN_RADIUS_V;
                return i;

            } else {
                //横屏
                int phoneWindowMinHeightVertical = Config.PHONE_WINDOW_MIN_HEIGHT_HORIZONTAL;
                int phoneWindowMaxHeightVertical = Config.PHONE_WINDOW_MAX_HEIGHT_HORIZONTAL;
                if (currentHeight == phoneWindowMinHeightVertical)
                    return IPHONE_PHONE_MIN_RADIUS_H;
                if (currentHeight == phoneWindowMaxHeightVertical)
                    return IPHONE_PHONE_MAX_RADIUS_H;
                int deltaH = phoneWindowMaxHeightVertical - phoneWindowMinHeightVertical;
                int deltaR = IPHONE_PHONE_MAX_RADIUS_H - IPHONE_PHONE_MIN_RADIUS_H;
                int x = currentHeight - phoneWindowMinHeightVertical;
                BigDecimal a = new BigDecimal(x);
                BigDecimal b = new BigDecimal(deltaH);
                BigDecimal c = new BigDecimal(deltaR);
                BigDecimal multiply = a.divide(b, 2, BigDecimal.ROUND_HALF_UP).multiply(c);
                int i = multiply.intValue() + IPHONE_PHONE_MIN_RADIUS_H;
                return i;
            }
        } else {
            return 0;
        }
    }

    public static Window.WindowLocation getSoftWindowSize(int windowCount, int windowType, int windowOri, float screenRatio) {
        if (windowCount <= 0) return null;
        Window.WindowLocation location = new Window.WindowLocation();
        if (windowCount == 1) {
            //只有一个窗口时
            if (windowType == Config.TYPE_PC) {
                location.setCardViewWidth(Config.SCREEN_WIDTH);
                location.setCardViewHeight(Config.SCREEN_HEIGHT);
            } else {
                if (windowOri == 0 || windowOri == 2) {
                    //手机竖屏,高为基准
                    location.setCardViewHeight(Config.SCREEN_HEIGHT);
                    location.setCardViewWidth((int) (Config.SCREEN_HEIGHT / screenRatio));
                } else {
                    //手机横屏,宽为基准
                    location.setCardViewWidth(Config.SCREEN_WIDTH);
                    location.setWindowHeight((int) (Config.SCREEN_WIDTH / screenRatio));
                }
            }
        } else {
            if (windowType == Config.TYPE_PC) {
                location.setCardViewWidth(Config.FP_SCREEN_WIDTH);
                location.setCardViewHeight(Config.FP_SCREEN_WIDTH);
            } else {
                if (windowOri == 0 || windowOri == 2) {
                    //手机竖屏,高为基准
                    location.setCardViewHeight(Config.FP_SCREEN_WIDTH);
                    location.setCardViewWidth((int) (Config.FP_SCREEN_WIDTH / screenRatio));
                } else {
                    //手机横屏,宽为基准
                    location.setCardViewWidth(Config.FP_SCREEN_WIDTH);
                    location.setWindowHeight((int) (Config.FP_SCREEN_WIDTH / screenRatio));
                }
            }
        }
        return location;
    }
}
