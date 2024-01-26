package com.wcch.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wcch.colorpicker.inter.ColorObservable;
import com.wcch.colorpicker.inter.ColorObserver;
import com.wcch.colorpicker.inter.Updatable;
import com.wcch.colorpicker.util.DpUtil;

import org.jetbrains.annotations.Nullable;

/**
 *颜色幻灯片视图
 * 是颜色选择器中用于控制颜色值的滑块视图的基类。
 * 该类包含了一些通用的属性和方法，同时定义了一些抽象方法，这些方法的具体实现将由其子类提供
 */
public abstract class ColorSliderView extends View implements ColorObservable, Updatable {

    protected int baseColor = Color.WHITE;//存储当前颜色值，默认为白色
    //colorPaint, borderPaint, selectorPaint: 用于绘制颜色、边框和选择器的画笔。
    private Paint colorPaint;
    private Paint borderPaint;
    private Paint selectorPaint;
    //selectorPath, currentSelectorPath: 选择器的路径，以及当前选择器路径的实例。
    private Path selectorPath;
    private Path currentSelectorPath = new Path();
    //选择器的大小
    protected float selectorSize;
    //当前滑块位置对应的数值，通常在 [0, 1] 范围内
    protected float currentValue = 1f;
    // 标志，指示是否仅在触摸事件抬起时更新颜色。
    private boolean onlyUpdateOnTouchEventUp;
    // 用于颜色变化通知的 ColorObservableEmitter 实例。
    private ColorObservableEmitter emitter = new ColorObservableEmitter();
    //   用于处理触摸事件，
    private ThrottledTouchEventHandler handler = new ThrottledTouchEventHandler(this);


    float imageX = 0f;
    float imageY = 0f;
    Bitmap customImage;
    public ColorSliderView(Context context) {
        this(context, null);
    }

    public ColorSliderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(0);
        borderPaint.setColor(Color.BLACK);
        selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint.setColor(Color.BLACK);
        selectorPath = new Path();
        selectorPath.setFillType(Path.FillType.WINDING);
    }

    /**
     * 当视图大小改变时，会调用此方法。在这里，初始化了画笔、选择器路径，并计算选择器的大小和位置。
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //创建一个倒三角，标识滑动到哪里
        //该方法可能用于设置colorPaint画笔的其他属性
        configurePaint(colorPaint);
        /********绘制 三角用这段代码***********/
        //重置selectorPath的路径，以确保在定义新的路径之前清除任何先前的路径。
        /*selectorPath.reset();
        //计算选择器的大小，其高度的25%。这里的h可能是一个变量，表示某个视图或画布的高度
        selectorSize = h * 0.25f;
        //移动到路径的起始点，此处是坐标 (0, 0)
        selectorPath.moveTo(0, 0);
        //添加一条线到指定的坐标 (selectorSize * 2, 0)。这是三角形的底边。
        selectorPath.lineTo(selectorSize * 2, 0);
        //添加一条线到指定的坐标 (selectorSize, selectorSize)。这是三角形的斜边。
        selectorPath.lineTo(selectorSize, selectorSize);
        //闭路径，将最后一个点与路径的起始点相连，形成一个封闭的三角形。
        selectorPath.close();*/

        /********绘制 自定义图片用这段代码***********/
        // 修改这里，使用自定义图标
        selectorSize = h * 0.25f;
        // 例如，假设你有一个叫做 your_custom_icon 的图片资源
        customImage = BitmapFactory.decodeResource(getResources(), R.drawable.color_selection_circle_917);
        // 计算图片的位置，居中放置在矩形框内
        imageX = (w - customImage.getWidth()) / 2f;
        imageY = (h - customImage.getHeight()) / 2f;

        // 移动到路径的起始点，此处是坐标 (0, 0)
        selectorPath.moveTo(0, 0);
        // 添加一条线到指定的坐标 (selectorSize * 2, 0)，这是三角形的底边。
        selectorPath.lineTo(selectorSize * 2, 0);
        // 添加一条线到指定的坐标 (selectorSize, selectorSize)，这是三角形的斜边。
        selectorPath.lineTo(selectorSize, selectorSize);
        // 闭路径，将最后一个点与路径的起始点相连，形成一个封闭的三角形。
        selectorPath.close();

    }

    /**
     * 在绘制视图时调用。绘制了颜色、边框和选择器，以及选择器的当前路径。
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //drawTriangle(canvas);
        drawCustomImages(canvas);
    }

    /**
     * 绘制一个倒三角 底色是一个矩形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas){
        float width = getWidth();
        float height = getHeight();
        //绘制一个填充颜色的矩形，矩形的左上角是 (selectorSize, selectorSize)，右下角是 (width - selectorSize, height)。
        // 这个矩形的颜色是 colorPaint 的颜色，colorPaint 是一个 Paint 对象，可能在代码的其他地方被配置为特定的颜色。
        canvas.drawRect(selectorSize, selectorSize, width - selectorSize, height, colorPaint);
        //绘制一个边框的矩形，同样是在相同的位置，并且颜色是 borderPaint 的颜色。borderPaint 也是一个 Paint 对象，可能被配置为特定的边框颜色和宽度。
        canvas.drawRect(selectorSize, selectorSize, width - selectorSize, height, borderPaint);
        //这一行通过 offset 方法移动了 selectorPath 的位置。currentValue 是一个浮点数，可能表示某种百分比或者具体的偏移量。
        // 这个偏移是在水平方向上进行的，它的范围在 (0, 1) 之间。这可能用于实现一个滑块或者其他用户界面元素的交互效果。这个路径的变化将在接下来的
        //canvas.drawPath(currentSelectorPath, selectorPaint); 中被应用。
        selectorPath.offset(currentValue * (width - 2 * selectorSize), 0, currentSelectorPath);
        //使用 selectorPaint 绘制一个路径。这个路径是之前通过偏移修改的 selectorPath。selectorPaint 是一个 Paint 对象，可能被配置为特定的路径颜色和样式。
        canvas.drawPath(currentSelectorPath, selectorPaint);
    }


    /**
     * 绘制一个自定义的图标 底色是一个带圆角的矩形
     * @param canvas
     */
    private void drawCustomImages(Canvas canvas){
        float width = getWidth();
        float height = getHeight();
        // 8dp是你想要转换的圆角大小
        float dpValue = 8;
        float cornerRadius = DpUtil.dpToPixels(getContext(),dpValue);
        // 绘制填充颜色的圆角矩形
        canvas.drawRoundRect(selectorSize, selectorSize, width - selectorSize, height, cornerRadius, cornerRadius, colorPaint);
        // 绘制带边框的圆角矩形
        canvas.drawRoundRect(selectorSize, selectorSize, width - selectorSize, height, cornerRadius, cornerRadius, borderPaint);

        // 绘制自定义图片
        if (customImage != null) {
            float imageWidth = customImage.getWidth();
            float imageHeight = customImage.getHeight();

            // 计算图片的位置，确保在矩形框的最左和最右
            float maxImageX = width - selectorSize - (float) (imageWidth*0.6);  // 最右边的位置 (float) (imageWidth*0.6)细调整之后的
            //float maxImageX = width - selectorSize-imageWidth;  // 最右边的位置 超出了
            float minImageX = selectorSize;  // 最左边的位置
            float imageX = Math.min(maxImageX, Math.max(minImageX, (width - imageWidth) * currentValue + minImageX));

            // 计算图片的位置，确保不超出矩形框上方
            float imageCenterX = width / 2f + currentValue * (width - 2 * selectorSize);
            float imageX2 = imageCenterX - imageWidth / 2f;
            //float imageY = selectorSize;  // 图标在矩形框上方
            // 计算图片的位置，确保垂直居中 后面这段是显示偏上了细调+(float) (imageWidth*0.05);
            float imageY = (height - imageHeight) / 2f + selectorSize+(float) (imageWidth*0.05);

            // 限制图标的大小，确保不超出矩形框
            float maxImageWidth = (float) (width - 1.5 * selectorSize);  // 图标的最大宽度
            float maxImageHeight = (float) (height - 1.5 * selectorSize);  // 图标的最大高度

            if (imageWidth > maxImageWidth) {
                imageWidth = maxImageWidth;
                imageHeight = imageWidth * customImage.getHeight() / customImage.getWidth();
            }

            if (imageHeight > maxImageHeight) {
                imageHeight = maxImageHeight;
                imageWidth = imageHeight * customImage.getWidth() / customImage.getHeight();
            }

            // 在指定位置绘制自定义图片
            canvas.drawBitmap(customImage, null, new RectF(imageX, imageY, imageX + imageWidth, imageY + imageHeight), null);
        }
    }

    /**
     * 处理触摸事件，根据触摸事件的不同阶段执行相应的操作，例如在按下或移动时更新滑块位置，并在抬起时通知颜色变化
     * @param event
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                handler.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                update(event);
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据触摸事件的位置更新当前数值，并通知颜色变化。在 onlyUpdateOnTouchEventUp 为真时，仅在触摸事件抬起时通知颜色变化。
     * @param event
     */
    @Override
    public void update(MotionEvent event) {
        updateValue(event.getX());
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            emitter.onColor(assembleColor(), true, isTouchUpEvent);
        }
    }

    /**
     * 设置基础颜色，并根据需要通知颜色变化
     * @param color
     * @param fromUser
     * @param shouldPropagate
     */
    void setBaseColor(int color, boolean fromUser, boolean shouldPropagate) {
        baseColor = color;
        configurePaint(colorPaint);
        int targetColor = color;
        if (!fromUser) {
            // if not set by user (means programmatically), resolve currentValue from color value
            currentValue = resolveValue(color);
        } else {
            targetColor = assembleColor();
        }

        if (!onlyUpdateOnTouchEventUp) {
            emitter.onColor(targetColor, fromUser, shouldPropagate);
        } else if (shouldPropagate) {
            emitter.onColor(targetColor, fromUser, true);
        }
        invalidate();
    }

    /**
     * resolveValue、configurePaint、assembleColor 方法：
     * 根据触摸事件的位置更新当前数值，确保数值在合理范围内。
     * @param eventX
     */
    private void updateValue(float eventX) {
        float left = selectorSize;
        float right = getWidth() - selectorSize;
        if (eventX < left) eventX = left;
        if (eventX > right) eventX = right;
        currentValue = (eventX - left) / (right - left);
        invalidate();
    }

    /**
     * 这三个是抽象方法，分别用于解析颜色值得到对应数值、配置画笔、组装颜色值。这些方法的具体实现由子类提供。
     * @param color
     * @return
     */
    protected abstract float resolveValue(int color);

    protected abstract void configurePaint(Paint colorPaint);

    protected abstract int assembleColor();

    /**
     * subscribe、unsubscribe、getColor 方法：
     * 实现了 ColorObservable 接口的方法，用于订阅和取消订阅颜色变化通知，以及获取当前颜色值。
     * @param observer
     */
    @Override
    public void subscribe(ColorObserver observer) {
        emitter.subscribe(observer);
    }

    @Override
    public void unsubscribe(ColorObserver observer) {
        emitter.unsubscribe(observer);
    }

    @Override
    public int getColor() {
        return emitter.getColor();
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }

    private ColorObserver bindObserver = new ColorObserver() {
        @Override
        public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
            setBaseColor(color, fromUser, shouldPropagate);
        }
    };

    private ColorObservable boundObservable;

    /**
     * bind、unbind 方法：
     * 将当前视图与其他颜色观察者绑定，以便同步颜色变化。 bind 方法会在绑定时订阅颜色变化通知，同时设置基础颜色，unbind 方法用于解除绑定。
     * @param colorObservable
     */
    public void bind(ColorObservable colorObservable) {
        if (colorObservable != null) {
            colorObservable.subscribe(bindObserver);
            setBaseColor(colorObservable.getColor(), true, true);
        }
        boundObservable = colorObservable;
    }

    public void unbind() {
        if (boundObservable != null) {
            boundObservable.unsubscribe(bindObserver);
            boundObservable = null;
        }
    }
}
