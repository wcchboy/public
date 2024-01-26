package com.wcch.android.utils;

import com.wcch.android.App;
import com.wcch.android.Constants;

import java.math.BigDecimal;

/**
 * @author created by Lzq
 * @time：2022/3/23
 * @Des：分辨率转换
 */
public class ResolutionUtil {

    private static int SCREEN_WIDTH = AndroidUtil.INSTANCE.getScreenWidth(App.getInstance().getApplicationContext());
    private static int SCREEN_HEIGHT = AndroidUtil.INSTANCE.getScreenHeight(App.getInstance().getApplicationContext());
    private static int FP_SCREEN_WIDTH = SCREEN_WIDTH / 2;
    private static int FP_SCREEN_HEIGHT = SCREEN_HEIGHT / 2;

    public static Resolution getNewResolution(int mode, int width, int height) {
        int delta;
        float rat = getRat(width, height);
        Resolution resolution = new Resolution();
        if (mode == Constants.FULL_SCREEN_MODE) {//全屏模式
            //1,宽高都没有超过屏幕分辨率
            if (width < SCREEN_WIDTH && height < SCREEN_HEIGHT) {
                int deltaWidth = SCREEN_WIDTH - width;
                int deltaHeight = SCREEN_HEIGHT - height;
                if (width >= height) {
                    width = SCREEN_WIDTH;
                    height = (int) (height + (deltaWidth * rat));
                    if (height > SCREEN_HEIGHT)
                        height = SCREEN_HEIGHT;

                } else {
                    height = SCREEN_HEIGHT;
                    width = (int) (width + (deltaHeight * rat));
                    if (width > SCREEN_WIDTH)
                        width = SCREEN_WIDTH;
                }
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //2，宽超过了分辨率
            if (width >= SCREEN_WIDTH && height <= SCREEN_HEIGHT) {
                delta = width - SCREEN_WIDTH;
                width = SCREEN_WIDTH;
                height = (int) (height - (delta * rat));
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //3，高超过了分辨率
            if (height >= SCREEN_HEIGHT && width <= SCREEN_WIDTH) {
                delta = height - SCREEN_HEIGHT;
                height = SCREEN_HEIGHT;
                width = (int) (width - (delta * rat));
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //4，宽高都超过了分辨率
            if (width >= SCREEN_WIDTH && height >= SCREEN_HEIGHT) {
                int deltaWidth = width - SCREEN_WIDTH;
                int deltaHeight = height - SCREEN_HEIGHT;
                if (width >= height) {
                    width = SCREEN_WIDTH;
                    height = (int) (height - (deltaWidth * rat));
                    if (height > SCREEN_HEIGHT)
                        height = SCREEN_HEIGHT;
                } else {
                    height = SCREEN_HEIGHT;
                    width = (int) (width - (deltaHeight * rat));
                    if (width > SCREEN_WIDTH)
                        width = SCREEN_WIDTH;
                }
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
        } else {//分屏模式
            if (width < FP_SCREEN_WIDTH && height < FP_SCREEN_HEIGHT) {
                int deltaWidth = FP_SCREEN_WIDTH - width;
                int deltaHeight = FP_SCREEN_HEIGHT - height;
                if (width >= height) {
                    width = FP_SCREEN_WIDTH;
                    height = (int) (height + (deltaWidth * rat));
                    if (height > FP_SCREEN_HEIGHT)
                        height = FP_SCREEN_HEIGHT;

                } else {
                    height = FP_SCREEN_HEIGHT;
                    width = (int) (width + (deltaHeight * rat));
                    if (width > FP_SCREEN_WIDTH)
                        width = FP_SCREEN_WIDTH;
                }
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //2，宽超过了分辨率
            if (width >= FP_SCREEN_WIDTH && height <= FP_SCREEN_HEIGHT) {
                delta = width - FP_SCREEN_WIDTH;
                width = FP_SCREEN_WIDTH;
                height = (int) (height - (delta * rat));
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //3，高超过了分辨率
            if (height >= FP_SCREEN_HEIGHT && width <= FP_SCREEN_WIDTH) {
                delta = height - FP_SCREEN_HEIGHT;
                height = FP_SCREEN_HEIGHT;
                width = (int) (width - (delta * rat));
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
            //4，宽高都超过了分辨率
            if (width >= FP_SCREEN_WIDTH && height >= FP_SCREEN_HEIGHT) {
                int deltaWidth = width - FP_SCREEN_WIDTH;
                int deltaHeight = height - FP_SCREEN_HEIGHT;
                if (width >= height) {
                    width = FP_SCREEN_WIDTH;
                    height = (int) (height - (deltaWidth * rat));
                    if (height > FP_SCREEN_HEIGHT)
                        height = FP_SCREEN_HEIGHT;
                } else {
                    height = FP_SCREEN_HEIGHT;
                    width = (int) (width - (deltaHeight * rat));
                    if (width > FP_SCREEN_WIDTH)
                        width = FP_SCREEN_WIDTH;
                }
                resolution.setWidth(width);
                resolution.setHeight(height);
            }
        }
        return resolution;
    }


    public static class Resolution {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private static float getRat(int width, int height) {
        BigDecimal b1 = new BigDecimal(width);
        BigDecimal b2 = new BigDecimal(height);
        if (width >= height) {
            return b2.divide(b1, 2, BigDecimal.ROUND_HALF_DOWN).floatValue();
        } else {
            return b1.divide(b2, 2, BigDecimal.ROUND_HALF_DOWN).floatValue();
        }

    }
}
