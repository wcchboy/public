package com.wcch.android.window;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wcch.android.view.TouchFrameLayout;


/**
 * @author created by Lzq
 * @time：2022/3/16
 * @Des：
 */
public class Window {
    private int type;//设备类型  0：PC  1：Android  2：iPhone  3：Mc  4：其他设备类型
    private int windowId;//窗口Id
    private RelativeLayout windowView;
    private boolean selected;//窗口是否被选中
    private int windowStatus;//窗口状态 0:全屏   1：分屏   2：最小化
    private String name;//名字
    private ImageView dragBg;
    private RelativeLayout fullScreenBg;
    private RelativeLayout fullScreenButton;
    private long lastTouchTime;
    private String ip;
    private TouchFrameLayout flVideoParentView;
    private ImageView ivClose;//白板内关闭投屏
    private ImageView ivFullScreen;//白版内全屏
    private ImageView ivScreenShot;//白版内截屏
    private ConstraintLayout toolBar;//控制条
    private ImageView scaleView;//缩放按钮
    private RelativeLayout floatWindowView;
    private RelativeLayout cardViewBg;
    private WindowManager.LayoutParams params;
    private WindowLocation windowLocation;
    private float ScreenRatio;
    private LinearLayout windowStatusIconGroup;
    private RelativeLayout dfModeViewGroup;
    private ImageView ivDfMode;
    private ImageView ivFullScreenMode;
    private ImageView ivDfModeIcon;
    private ImageView ivFullScreenIcon;
    private ImageView lockScreenIcon;
    private ImageView ivWindowModeIcon;
    private RelativeLayout rlWindowMode;
    private RelativeLayout rlDfMode;
    private RelativeLayout rlFullScreenMode;
    private RelativeLayout rlScreenLock;//锁屏按钮
    private TextView tvToolBarDeviceName;//控制条上设备名
    private LinearLayout llDeviceNameAndIconView;//等分模式设备名，设备icon View
    private ImageView ivDeviceTypeIcon;//设备类型icon
    private TextView tvDeviceName;//等分模式设备名
    private RelativeLayout rlDeviceNameTouchBar;//未被选中的窗口拖拽条
    private TextView tvDeviceNameTouchBarDeviceName;//未被选中的窗口拖拽条的设备名
    private CardView cardView;

    private int ori;
    private DeskSizeChangeBean deskSizeChange;
    private boolean isShowing = false;
    private boolean focus = false;
    private RelativeLayout rlFocusBg;
    private int resolutionWidth;
    private int resolutionHeight;

    public TouchFrameLayout getFlVideoParentView() {
        return flVideoParentView;
    }

    public void setFlVideoParentView(TouchFrameLayout flVideoParentView) {
        this.flVideoParentView = flVideoParentView;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getWindowStatus() {
        return windowStatus;
    }

    public void setWindowStatus(int windowStatus) {
        this.windowStatus = windowStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getDragBg() {
        return dragBg;
    }

    public void setDragBg(ImageView dragBg) {
        this.dragBg = dragBg;
    }

    public RelativeLayout getFullScreenBg() {
        return fullScreenBg;
    }

    public void setFullScreenBg(RelativeLayout fullScreenBg) {
        this.fullScreenBg = fullScreenBg;
    }

    public RelativeLayout getFullScreenButton() {
        return fullScreenButton;
    }

    public void setFullScreenButton(RelativeLayout fullScreenButton) {
        this.fullScreenButton = fullScreenButton;
    }

    public RelativeLayout getRlScreenLock() {
        return rlScreenLock;
    }

    public void setRlScreenLock(RelativeLayout rlScreenLock) {
        this.rlScreenLock = rlScreenLock;
    }

    public long getLastTouchTime() {
        return lastTouchTime;
    }

    public void setLastTouchTime(long lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public RelativeLayout getWindowView() {
        return windowView;
    }

    public void setWindowView(RelativeLayout windowView) {
        this.windowView = windowView;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ImageView getIvClose() {
        return ivClose;
    }

    public void setIvClose(ImageView ivClose) {
        this.ivClose = ivClose;
    }

    public ImageView getIvFullScreen() {
        return ivFullScreen;
    }

    public void setIvFullScreen(ImageView ivFullScreen) {
        this.ivFullScreen = ivFullScreen;
    }

    public ImageView getIvScreenShot() {
        return ivScreenShot;
    }

    public void setIvScreenShot(ImageView ivScreenShot) {
        this.ivScreenShot = ivScreenShot;
    }

    public ConstraintLayout getToolBar() {
        return toolBar;
    }

    public void setToolBar(ConstraintLayout toolBar) {
        this.toolBar = toolBar;
    }

    public ImageView getScaleView() {
        return scaleView;
    }

    public void setScaleView(ImageView scaleView) {
        this.scaleView = scaleView;
    }

    public RelativeLayout getFloatWindowView() {
        return floatWindowView;
    }

    public void setFloatWindowView(RelativeLayout floatWindowView) {
        this.floatWindowView = floatWindowView;
    }

    public RelativeLayout getCardViewBg() {
        return cardViewBg;
    }

    public void setCardViewBg(RelativeLayout cardViewBg) {
        this.cardViewBg = cardViewBg;
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    public WindowLocation getWindowLocation() {
        return windowLocation;
    }

    public void setWindowLocation(WindowLocation windowLocation) {
        this.windowLocation = windowLocation;
    }

    public float getScreenRatio() {
        return ScreenRatio;
    }

    public void setScreenRatio(float screenRatio) {
        ScreenRatio = screenRatio;
    }

    public LinearLayout getWindowStatusIconGroup() {
        return windowStatusIconGroup;
    }

    public void setWindowStatusIconGroup(LinearLayout windowStatusIconGroup) {
        this.windowStatusIconGroup = windowStatusIconGroup;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public RelativeLayout getDfModeViewGroup() {
        return dfModeViewGroup;
    }

    public void setDfModeViewGroup(RelativeLayout dfModeViewGroup) {
        this.dfModeViewGroup = dfModeViewGroup;
    }

    public ImageView getIvDfModeIcon() {
        return ivDfModeIcon;
    }

    public void setIvDfModeIcon(ImageView ivDfModeIcon) {
        this.ivDfModeIcon = ivDfModeIcon;
    }

    public ImageView getIvFullScreenModeIcon() {
        return ivFullScreenIcon;
    }

    public void setIvFullScreenModeIcon(ImageView ivFullScreenIcon) {
        this.ivFullScreenIcon = ivFullScreenIcon;
    }

    public ImageView getLockScreenIcon() {
        return lockScreenIcon;
    }

    public void setLockScreenIcon(ImageView lockScreenIcon) {
        this.lockScreenIcon = lockScreenIcon;
    }


    public RelativeLayout getRlWindowModeGroup() {
        return rlWindowMode;
    }

    public void setRlWindowModeGroup(RelativeLayout rlWindowMode) {
        this.rlWindowMode = rlWindowMode;
    }

    public ImageView getIvDfMode() {
        return ivDfMode;
    }

    public void setIvDfMode(ImageView ivDfMode) {
        this.ivDfMode = ivDfMode;
    }

    public ImageView getIvFullScreenMode() {
        return ivFullScreenMode;
    }

    public void setIvFullScreenMode(ImageView ivFullScreenMode) {
        this.ivFullScreenMode = ivFullScreenMode;
    }

    public ImageView getIvWindowModeIcon() {
        return ivWindowModeIcon;
    }

    public void setIvWindowModeIcon(ImageView ivWindowModeIcon) {
        this.ivWindowModeIcon = ivWindowModeIcon;
    }

    public RelativeLayout getRlWindowMode() {
        return rlWindowMode;
    }

    public void setRlWindowMode(RelativeLayout rlWindowMode) {
        this.rlWindowMode = rlWindowMode;
    }

    public RelativeLayout getRlDfMode() {
        return rlDfMode;
    }

    public void setRlDfMode(RelativeLayout rlDfMode) {
        this.rlDfMode = rlDfMode;
    }

    public void setRlFullScreenMode(RelativeLayout rlFullScreenMode) {
        this.rlFullScreenMode = rlFullScreenMode;
    }

    public RelativeLayout getRlDfModeGroup() {
        return rlDfMode;
    }

    public void setRlDfModeGroup(RelativeLayout rlDfMode) {
        this.rlDfMode = rlDfMode;
    }

    public RelativeLayout getRlFullScreenMode() {
        return rlFullScreenMode;
    }

    public void setRlFullScreenModeGroup(RelativeLayout rlFullScreenMode) {
        this.rlFullScreenMode = rlFullScreenMode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOri() {
        return ori;
    }

    public void setOri(int ori) {
        this.ori = ori;
    }

    public TextView getTvToolBarDeviceName() {
        return tvToolBarDeviceName;
    }

    public void setTvToolBarDeviceName(TextView tvToolBarDeviceName) {
        this.tvToolBarDeviceName = tvToolBarDeviceName;
    }

    public LinearLayout getLlDeviceNameAndIconView() {
        return llDeviceNameAndIconView;
    }

    public void setLlDeviceNameAndIconView(LinearLayout llDeviceNameAndIconView) {
        this.llDeviceNameAndIconView = llDeviceNameAndIconView;
    }

    public ImageView getIvDeviceTypeIcon() {
        return ivDeviceTypeIcon;
    }

    public void setIvDeviceTypeIcon(ImageView ivDeviceTypeIcon) {
        this.ivDeviceTypeIcon = ivDeviceTypeIcon;
    }

    public TextView getTvDeviceName() {
        return tvDeviceName;
    }

    public void setTvDeviceName(TextView tvDeviceName) {
        this.tvDeviceName = tvDeviceName;
    }

    public RelativeLayout getRlDeviceNameTouchBar() {
        return rlDeviceNameTouchBar;
    }

    public void setRlDeviceNameTouchBar(RelativeLayout rlDeviceNameTouchBar) {
        this.rlDeviceNameTouchBar = rlDeviceNameTouchBar;
    }

    public TextView getTvDeviceNameTouchBarDeviceName() {
        return tvDeviceNameTouchBarDeviceName;
    }

    public void setTvDeviceNameTouchBarDeviceName(TextView tvDeviceNameTouchBarDeviceName) {
        this.tvDeviceNameTouchBarDeviceName = tvDeviceNameTouchBarDeviceName;
    }

    public DeskSizeChangeBean getDeskSizeChange() {
        return deskSizeChange;
    }

    public void setDeskSizeChange(DeskSizeChangeBean deskSizeChange) {
        this.deskSizeChange = deskSizeChange;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public RelativeLayout getRlFocusBg() {
        return rlFocusBg;
    }

    public void setRlFocusBg(RelativeLayout rlFocusBg) {
        this.rlFocusBg = rlFocusBg;
    }


    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public static class WindowLocation {
        private float scaleRate;//窗口缩放比例
        private int x;
        private int y;
        private int windowWidth;
        private int windowHeight;
        private int gravity;
        private int cardViewWidth;
        private int cardViewHeight;
        private int margin;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWindowWidth() {
            return windowWidth;
        }

        public void setWindowWidth(int windowWidth) {
            this.windowWidth = windowWidth;
        }

        public int getWindowHeight() {
            return windowHeight;
        }

        public void setWindowHeight(int windowHeight) {
            this.windowHeight = windowHeight;
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public float getScaleRate() {
            return scaleRate;
        }

        public void setScaleRate(float scaleRate) {
            this.scaleRate = scaleRate;
        }

        public int getCardViewWidth() {
            return cardViewWidth;
        }

        public void setCardViewWidth(int cardViewWidth) {
            this.cardViewWidth = cardViewWidth;
        }

        public int getCardViewHeight() {
            return cardViewHeight;
        }

        public void setCardViewHeight(int cardViewHeight) {
            this.cardViewHeight = cardViewHeight;
        }

        public int getMargin() {
            return margin;
        }

        public void setMargin(int margin) {
            this.margin = margin;
        }
    }
}
