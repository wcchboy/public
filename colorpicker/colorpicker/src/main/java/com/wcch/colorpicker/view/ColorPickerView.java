package com.wcch.colorpicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wcch.colorpicker.BuildConfig;
import com.wcch.colorpicker.ColorWheelView;
import com.wcch.colorpicker.R;
import com.wcch.colorpicker.inter.ColorObservable;
import com.wcch.colorpicker.inter.ColorObserver;
import com.wcch.colorpicker.util.LogUtil;
import com.wcch.colorpicker.view.AlphaSliderView;
import com.wcch.colorpicker.view.BrightnessSliderView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *颜色选取器视图
 */
public class ColorPickerView extends LinearLayout implements ColorObservable {
    private static final String TAG = "ColorPickerView";
    private ColorWheelView colorWheelView;//彩色控制盘视图
    private BrightnessSliderView brightnessSliderView;//亮度滑块视图
    private AlphaSliderView alphaSliderView;//透明度滑块视图
    private ColorObservable observableOnDuty;
    private boolean onlyUpdateOnTouchEventUp;

    private int initialColor = Color.BLACK;

    private int sliderMargin;
    private int sliderHeight;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        //构造函数中使用 TypedArray 从 XML 中检索自定义属性，并进行相应的初始化。
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        boolean enableAlpha = typedArray.getBoolean(R.styleable.ColorPickerView_enableAlpha, false);
        boolean enableBrightness = typedArray.getBoolean(R.styleable.ColorPickerView_enableBrightness, true);
        onlyUpdateOnTouchEventUp = typedArray.getBoolean(R.styleable.ColorPickerView_onlyUpdateOnTouchEventUp, false);
        typedArray.recycle();
        //圆形的颜色选取器包括他中间的一个选择view // 创建颜色控制盘视图，并设置其属性。
        colorWheelView = new ColorWheelView(context);
        float density = getResources().getDisplayMetrics().density;
        int margin = (int) (8 * density);
        LogUtil.d(TAG, "ColorPickerView density:"+density);
        LogUtil.d(TAG, "ColorPickerView margin:"+margin);
        sliderMargin = 2 * margin;
        sliderHeight = (int) (24 * density);
        LogUtil.d(TAG, "ColorPickerView sliderMargin:"+sliderMargin);
        LogUtil.d(TAG, "ColorPickerView sliderHeight:"+sliderHeight);
        //将颜色控制盘视图添加到 ColorPickerView 中。
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(colorWheelView, params);
        //其他初始化和属性设置： 设置亮度和透明度的可用性，并设置视图的填充。
        setEnabledBrightness(enableBrightness);
        setEnabledAlpha(enableAlpha);

        setPadding(margin, margin, margin, margin);
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
        updateObservableOnDuty();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (BuildConfig.DEBUG) {
            Log.d(TAG,"onMeasure maxWidth: "+maxWidth+" maxHeight: "+ maxHeight);
        }

        int desiredWidth = maxHeight - (getPaddingTop() + getPaddingBottom()) + (getPaddingLeft() + getPaddingRight());
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG,"onMeasure getPaddingTop(): "+getPaddingTop()+" getPaddingBottom():"+getPaddingBottom()+" getPaddingLeft():"+getPaddingLeft()+" getPaddingRight():"+getPaddingRight());
            LogUtil.d(TAG,"onMeasure sliderMargin: "+sliderMargin +" sliderHeight:"+sliderHeight);
            LogUtil.d(TAG,"onMeasure desiredWidth: "+desiredWidth);
        }
        if (brightnessSliderView != null) {
            desiredWidth -= (sliderMargin + sliderHeight);
        }
        if (alphaSliderView != null){
            desiredWidth -= (sliderMargin + sliderHeight);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG,"onMeasure desiredWidth: "+desiredWidth);
        }

        int width = Math.min(maxWidth, desiredWidth);
        int height = width - (getPaddingLeft() + getPaddingRight()) + (getPaddingTop() + getPaddingBottom());
        if (brightnessSliderView != null) {
            height += (sliderMargin + sliderHeight);
        }
        if (alphaSliderView != null) {
            height += (sliderMargin + sliderHeight);
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG,"onMeasure width: "+width+" height: "+ height);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec)));
    }

    /**
     * 设置颜色选择器的初始颜色。
     * @param color
     */
    public void setInitialColor(int color) {
        initialColor = color;
        colorWheelView.setColor(color, true);
    }

    public void setEnabledBrightness(boolean enable) {
        if (enable) {
            if (brightnessSliderView == null) {
                brightnessSliderView = new BrightnessSliderView(getContext());
                LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, sliderHeight);
                params.topMargin = sliderMargin;
                addView(brightnessSliderView, 1, params);
            }
            brightnessSliderView.bind(colorWheelView);
            updateObservableOnDuty();
        } else {
            if (brightnessSliderView != null) {
                brightnessSliderView.unbind();
                removeView(brightnessSliderView);
                brightnessSliderView = null;
            }
            updateObservableOnDuty();
        }

        if (alphaSliderView != null) {
            setEnabledAlpha(true);
        }
    }

    public void setEnabledAlpha(boolean enable) {
        if (enable) {
            if (alphaSliderView == null) {
                alphaSliderView = new AlphaSliderView(getContext());
                LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, sliderHeight);
                params.topMargin = sliderMargin;
                addView(alphaSliderView, params);
            }

            ColorObservable bindTo = brightnessSliderView;
            if (bindTo == null) {
                bindTo = colorWheelView;
            }
            alphaSliderView.bind(bindTo);
            updateObservableOnDuty();
        } else {
            if (alphaSliderView != null) {
                alphaSliderView.unbind();
                removeView(alphaSliderView);
                alphaSliderView = null;
            }
            updateObservableOnDuty();
        }
    }

    private void updateObservableOnDuty() {
        if (observableOnDuty != null) {
            for (ColorObserver observer: observers) {
                observableOnDuty.unsubscribe(observer);
            }
        }

        colorWheelView.setOnlyUpdateOnTouchEventUp(false);
        if (brightnessSliderView != null) {
            brightnessSliderView.setOnlyUpdateOnTouchEventUp(false);
        }
        if (alphaSliderView != null) {
            alphaSliderView.setOnlyUpdateOnTouchEventUp(false);
        }

        if (brightnessSliderView == null && alphaSliderView == null) {
            observableOnDuty = colorWheelView;
            colorWheelView.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp);
        } else {
            if (alphaSliderView != null) {
                observableOnDuty = alphaSliderView;
                alphaSliderView.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp);
            } else {
                observableOnDuty = brightnessSliderView;
                brightnessSliderView.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp);
            }
        }

        if (observers != null) {
            for (ColorObserver observer : observers) {
                observableOnDuty.subscribe(observer);
                observer.onColor(observableOnDuty.getColor(), false, true);
            }
        }
    }
    public void setColorWheelView(int color) {
        colorWheelView.setColor(color, true);
    }

    public void reset() {
        colorWheelView.setColor(initialColor, true);
    }

    List<ColorObserver> observers = new ArrayList<>();
    //颜色观察者模式的实现：
    @Override
    public void subscribe(ColorObserver observer) {
        observableOnDuty.subscribe(observer);
        observers.add(observer);
    }

    @Override
    public void unsubscribe(ColorObserver observer) {
        observableOnDuty.unsubscribe(observer);
        observers.remove(observer);
    }

    @Override
    public int getColor() {
        return observableOnDuty.getColor();
    }
}
