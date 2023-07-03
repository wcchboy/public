package com.wcch.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.igrs.betotablet.R;
import com.igrs.betotablet.utils.LogUtil;


public class SlideButton extends View {

    //状态改变监听
    public interface SlideButtonOnCheckedListener {
        void onCheckedChangeListener(SlideButton slideButton, boolean isChecked);
    }

    private SlideButtonOnCheckedListener mListener;

    //view默认的高,view默认的宽是高的两倍(单位:dp)
    public static final int VIEW_HEIGHT = 20;
    //椭圆的边框宽度
    private static final int strokeLineWidth = 3;
    //圆的边框宽度
    private static final int circleStrokeWidth = 3;

    //椭圆填充颜色
    private String StrokeSolidColor = "#FF3B5478";
    //圆形checked填充颜色
    private String CircleCheckedColor = "#ffffffff";
    //圆形非checked填充颜色
    private String CircleNoCheckedColor = "#ffffffff";

    //控件内边距
    private static int PADDING = 20;
    //移动的判定距离
    private static int MOVE_DISTANCE = 50;

    //圆的x轴圆心
    private float circle_x;


    //圆角矩形的高
    private int strokeHeight;
    //圆角矩形的半径
    private float strokeCircleRadius;
    //内部圆的半径
    private float circleRadius;
    private Scroller mScroller;
    //当前按钮的开关状态
    private boolean isChecked = true;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private float circleStartX;
    private float circleEndX;
    private int centerX;
    private int centerY;
    private float preX = 0;
    private boolean isMove;
    private int view_height_int;
    private int strokeCheckedSolidColor_int = getResources().getColor(R.color.slide_selected);
    private int strokeNoCheckedSolidColor_int = getResources().getColor(R.color.slide_un_selected);
    private int circleChecked_int = getResources().getColor(R.color.white);
    private boolean defaultValue = true;
    private boolean clickable = true;
    private int type = 100010;

    public SlideButton(Context context) {
        super(context);
        init(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlideButton);
        clickable = array.getBoolean(R.styleable.SlideButton_android_clickable, true);
        defaultValue = array.getBoolean(R.styleable.SlideButton_android_defaultValue, true);
        init(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlideButton);
        clickable = array.getBoolean(R.styleable.SlideButton_android_clickable, true);
        defaultValue = array.getBoolean(R.styleable.SlideButton_android_defaultValue, true);
        init(context);
    }

    /**
     * * 设置小圆模式
     *
     * @param strokeSolidColor   圆角矩形的填充颜色
     * @param circleCheckedColor 内部小圆被选中的颜色
     */
    public void setSmallCircleModel(int strokeSolidColor, int strokeCheckedSolidColor, int circleCheckedColor) {
//        isBigCircle = false;
        strokeCheckedSolidColor_int = strokeCheckedSolidColor;//选中的颜色
        strokeNoCheckedSolidColor_int = strokeSolidColor;//非选中的颜色
        circleChecked_int = circleCheckedColor;//内部小圆的颜色
        invalidate();
    }

    /**
     * 设置点击监听
     *
     * @param listener
     */
    public void setOnCheckedListener(SlideButtonOnCheckedListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置按钮状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (isChecked) {
            circle_x = circleEndX;
        } else {
            circle_x = circleStartX;
        }
        invalidate();
    }

    private void init(Context context) {
        setEnabled(true);
        setClickable(true);
        mPaint = new Paint();
        mScroller = new Scroller(context);
        view_height_int = dip2px(context, VIEW_HEIGHT);
        strokeNoCheckedSolidColor_int = Color.parseColor(StrokeSolidColor);
        circleChecked_int = Color.parseColor(CircleCheckedColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            //如果是wrap_content
            heightSize = view_height_int;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = heightSize * 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        PADDING = h / 15;
        MOVE_DISTANCE = mWidth / 100;
        //圆角椭圆的高
        strokeHeight = h - PADDING * 2;
        //外部圆角矩形的半径
        strokeCircleRadius = strokeHeight / 2;
        centerY = mHeight / 2;

        circleRadius = strokeCircleRadius - PADDING;
//        Log.i("TAG", "mHeight:" + mHeight + "   strokeCircleRadius: " + strokeCircleRadius);
        //内部圆的x轴起始坐标
        circleStartX = PADDING + strokeCircleRadius;
        //内部圆的x轴终点坐标
        circleEndX = mWidth - circleStartX;
        if (isChecked) {
            circle_x = circleEndX;
        } else {
            circle_x = circleStartX;
        }

        //控件的中线
        centerX = mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
        drawCircle(canvas);
    }

    //画圆角矩形
    private void drawRect(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        if (isChecked) {
            mPaint.setColor(strokeCheckedSolidColor_int);
        } else {
            mPaint.setColor(strokeNoCheckedSolidColor_int);
        }
        //画填充
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);

        //画边框
        mPaint.setStrokeWidth(strokeLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);
    }

    //画里面的圆
    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        float circleRadiusNew = circleRadius;

        mPaint.setColor(Color.parseColor("#ffffffff"));
        canvas.drawCircle(circle_x, centerY, circleRadiusNew, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = canTouch();
        LogUtil.e("zjx_touch"," canTouch is "+b);
        if (!b) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                isMove = false;
                if (!isChecked) {
                    circle_x = PADDING + strokeCircleRadius;
                } else {
                    circle_x = mWidth - PADDING - strokeCircleRadius;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float move_x = event.getX();
                if (Math.abs(move_x - preX) > MOVE_DISTANCE) {
                    isMove = true;
                    if (move_x < circleStartX) {
                        circle_x = circleStartX;
                        isChecked = false;
                    } else if (move_x > circleEndX) {
                        circle_x = circleEndX;
                        isChecked = true;
                    } else {
                        circle_x = move_x;
                    }
                //    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e("zjx_touch"," isMove is "+isMove+" isChecked is "+isChecked);
                if (isMove) {
                    if (circle_x >= centerX) {
                        //关闭(执行开启)
                        mScroller.startScroll((int) circle_x, 0, (int) (circleEndX - circle_x), 0);
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll((int) circle_x, 0, (int) (circleStartX - circle_x), 0);
                        isChecked = false;
                    }
                } else {
                    if (!isChecked) {
                        //关闭(执行开启)
                        mScroller.startScroll((int) circle_x, 0, (int) (circleEndX - circle_x), 0);
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll((int) circle_x, 0, (int) (circleStartX - circle_x), 0);
                        isChecked = false;
                    }
                }
                if (mListener != null) {
                    mListener.onCheckedChangeListener(this, isChecked);
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            circle_x = mScroller.getCurrX();
            invalidate();
        }
    }

    public void setType(int type) {
        this.type = type;
    }


  //  private String[] names = {"","", "扬声器", "麦克风", "摄像机", "HID", "投屏", "遥控", "共享盘"};
    private String[] names = {"","扬声器", "麦克风", "摄像机", "", "投屏", "遥控", "共享盘"};
    private boolean canTouch() {
        if (!clickable) return false;
        /*BeToPhysicalDevice localDevice = BeToHelper.getBeToHelper().getLocalDevice();
        if (localDevice == null) return false;
        List<BeToFunctionDevice> functionDeviceList = localDevice.function_devices;
        if (functionDeviceList == null || functionDeviceList.size() == 0) return false;
        for (BeToFunctionDevice device : functionDeviceList) {
            LogUtil.e("zjx_touch","点击了names[type]: "+names[type]);
       //     if (device.type != type) continue;
//            if (device.current_share_count > 0) {
//                ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING,names[type] + "正在共享");
//                return false;
//            }

            if (names[type] =="扬声器"){
                if(BeToHelper.getBeToHelper().getSpeakerIsUse()){
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getResources().getString(R.string.speaker_is_use));
                    return  false ;
                }
            }

            if (names[type] =="麦克风"){
             if(BeToHelper.getBeToHelper().getMicIsUse()){
                 ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getResources().getString(R.string.mic_is_use));
                return  false ;
              }
            }

            if (names[type] =="摄像机"){
                if(BeToHelper.getBeToHelper().getCameraIsUse()){
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getResources().getString(R.string.camera_is_use));
                    return  false ;
                }
            }

            if (names[type] =="投屏"){
                if(BeToHelper.getBeToHelper().getScreencastIsUse()){
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getResources().getString(R.string.screencast_is_use));
                    return  false ;
                }
            }

            if (names[type] =="遥控"){
                if(BeToHelper.getBeToHelper().getRemotectrlIsUse()){
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getResources().getString(R.string.remotectrl_is_use));
                    return  false ;
                }
            }

        }*/
        return true;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
