package com.wcch.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.wcch.android.R;
import com.wcch.android.entity.PaichengEntity;
import com.wcch.android.entity.PointBean;
import com.wcch.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewCompat;

public class CustomView extends View {
    private List<Integer> allColor;
    private List<RectF> allData;
    private int curPosition;
    private boolean mAdd;
    private Context mContext;
    private List<PaichengEntity> mData;
    private PointBean mEndPoint;
    private long mEndTime;
    private OnPaiChengShowListener mListener;
    private boolean mOver;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;
    private float mScreenH;
    private float mScreenW;
    private float mSpaceH;
    private float mSpaceW;
    private PointBean mStartPoint;
    private long mStartTime;
    private float mStartY;
    private int mWedEnd;
    private int mWedStart;
    private String[] time2s;
    private String[] times;
    private String[] weds;
    private List<Float> xs;
    private List<Float> ys;

    public interface OnPaiChengShowListener {
        void edit(List<Integer> list);

        void show(long j, long j2, int i, int i2, int i3, boolean z);
    }

    public float getSpaceH() {
        return this.mSpaceH;
    }

    public List<Float> getXs() {
        return this.xs;
    }

    public List<RectF> getAllData() {
        return this.allData;
    }

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.xs = new ArrayList();
        this.ys = new ArrayList();
        this.allData = new ArrayList();
        this.allColor = new ArrayList();
        this.weds = new String[7];
        this.times = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "00:00"};
        this.time2s = new String[]{"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30", "00:00"};
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mWedStart = 0;
        this.mWedEnd = 0;
        this.mData = new ArrayList();
        this.curPosition = -1;
        this.mContext = context;
        init();
    }

    public float dipTopx(Context context, float f) {
        return (f * context.getResources().getDisplayMetrics().density) + 0.5f;
    }

    private void init() {
        this.weds = new String[]{this.mContext.getResources().getString(R.string.mon), this.mContext.getResources().getString(R.string.tue), this.mContext.getResources().getString(R.string.wed), this.mContext.getResources().getString(R.string.thu), this.mContext.getResources().getString(R.string.fri), this.mContext.getResources().getString(R.string.sat), this.mContext.getResources().getString(R.string.sun)};
        this.mPaint4 = new TextPaint(1);
        this.mPaint4.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint4.setTextSize(dipTopx(this.mContext, 13.0f));
        this.mPaint = new TextPaint(1);
        this.mPaint.setColor(this.mContext.getResources().getColor(R.color.text_color));
        this.mPaint.setTextSize(dipTopx(this.mContext, 13.0f));
        this.mPaint2 = new TextPaint(1);
        this.mPaint3 = new TextPaint(1);
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        this.mScreenW = (float) windowManager.getDefaultDisplay().getWidth();
        this.mScreenH = (float) windowManager.getDefaultDisplay().getHeight();
        this.mSpaceW = this.mScreenW / 9.0f;
        this.mSpaceH = this.mScreenH * 0.03f;
        this.xs.clear();
        this.ys.clear();
        for (int i = 0; i <= this.weds.length; i++) {
            this.xs.add(Float.valueOf((((float) i) + 1.5f) * this.mSpaceW));
        }
        for (int i2 = 0; i2 < this.times.length; i2++) {
            this.ys.add(Float.valueOf(((float) (i2 + 2)) * this.mSpaceH));
            if (i2 != this.times.length - 1) {
                this.ys.add(Float.valueOf((((float) i2) + 2.5f) * this.mSpaceH));
            }
        }
    }

    public float getFontWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    public float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = 0;
        while (true) {
            String[] strArr = this.weds;
            if (i >= strArr.length) {
                break;
            }
            canvas.drawText(strArr[i], (((float) (i + 2)) * this.mSpaceW) - (getFontWidth(this.mPaint, strArr[i]) / 2.0f), this.mSpaceH * 1.5f, this.mPaint);
            float f = ((float) i) + 1.5f;
            float f2 = this.mSpaceW;
            float f3 = this.mSpaceH;
            canvas.drawLine(f * f2, f3 * 2.0f, f * f2, f3 * 26.0f, this.mPaint);
            i++;
        }
        int i2 = 0;
        while (true) {
            String[] strArr2 = this.times;
            if (i2 >= strArr2.length) {
                break;
            }
            canvas.drawText(strArr2[i2], this.mSpaceW / 2.0f, ((((float) i2) + 2.0f) * this.mSpaceH) - ((this.mPaint.getFontMetrics().top + this.mPaint.getFontMetrics().bottom) / 2.0f), this.mPaint);
            float f4 = this.mSpaceW;
            float f5 = (float) (i2 + 2);
            float f6 = this.mSpaceH;
            canvas.drawLine(f4 * 1.5f, f5 * f6, 8.5f * f4, f5 * f6, this.mPaint);
            i2++;
        }
        String[] strArr3 = this.weds;
        float f7 = this.mSpaceW;
        float length = (((float) strArr3.length) + 1.5f) * f7;
        float f8 = this.mSpaceH;
        canvas.drawLine(length, f8 * 2.0f, (((float) strArr3.length) + 1.5f) * f7, f8 * 26.0f, this.mPaint);
        List<RectF> list = this.allData;
        if (list != null && this.mData != null && list.size() > 0 && this.mData.size() > 0) {
            for (int i3 = 0; i3 < this.allData.size(); i3++) {
                this.mPaint3.setColor(this.allColor.get(i3).intValue());
                canvas.drawRect(this.allData.get(i3), this.mPaint3);
                if (this.mData.size() >= this.allData.size()) {
                    canvas.drawText(this.mData.get(i3).getTitle(), this.allData.get(i3).left, this.allData.get(i3).top + getFontHeight(this.mPaint4), this.mPaint4);
                }
            }
        }
        PointBean pointBean = this.mStartPoint;
        if (!(pointBean == null || this.mEndPoint == null)) {
            canvas.drawRect((float) pointBean.x, (float) this.mStartPoint.y, (float) this.mEndPoint.x, (float) this.mEndPoint.y, this.mPaint2);
        }
    }

    private RectF getCurRect(float f, float f2) {
        float f3 = this.mSpaceW;
        float f4 = (((float) ((int) ((f - (f3 * 0.5f)) / f3))) * f3) + (f3 * 0.5f);
        float f5 = (((float) (((int) ((f - (f3 * 0.5f)) / f3)) + 1)) * f3) + (f3 * 0.5f);
        float f6 = this.mSpaceH;
        return new RectF(f4, ((float) ((int) (f2 / f6))) * f6, f5, ((float) (((int) (f2 / f6)) + 1)) * f6);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c8, code lost:
        r11 = java.lang.Long.parseLong(r25.time2s[r1].substring(0, 2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d8, code lost:
        if (r1 != (r25.time2s.length - 1)) goto L_0x00dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00da, code lost:
        r11 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00dc, code lost:
        r11 = ((r11 * 3600) + (java.lang.Long.parseLong(r25.time2s[r1].substring(3, 5)) * 60)) - 1;
        r13.setY((double) r25.ys.get(r1).floatValue());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01a0, code lost:
        if (r25.mEndPoint.y != -1.0d) goto L_0x01a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01b7, code lost:
        if (r25.mStartPoint.y == r5) goto L_0x01b9;
     */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x025e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r26) {
        /*
        // Method dump skipped, instructions count: 1048
        */
        return true;
    }

    public void setData(List<PaichengEntity> list, int i) {
        this.mData = list;
        this.curPosition = i;
        if (i == -1 || i >= this.allData.size() || i >= list.size()) {
            this.mStartPoint = null;
            this.mEndPoint = null;
            this.allData.clear();
            this.allColor.clear();
            for (int i2 = 0; i2 < list.size(); i2++) {
                PaichengEntity paichengEntity = list.get(i2);
                long startTime = paichengEntity.getStartTime();
                long endTime = paichengEntity.getEndTime();
                int wedStart = paichengEntity.getWedStart();
                int wedEnd = paichengEntity.getWedEnd();
                int intColor = Utils.getIntColor(paichengEntity.getNewColor());
                RectF rectF = new RectF();
                rectF.left = this.xs.get(wedStart - 1).floatValue();
                rectF.top = (this.mSpaceH * ((float) (startTime + 7200))) / 3600.0f;
                rectF.right = this.xs.get(wedEnd).floatValue();
                rectF.bottom = (this.mSpaceH * ((float) (endTime + 7200))) / 3600.0f;
                this.allData.add(rectF);
                this.allColor.add(Integer.valueOf(intColor));
            }
            invalidate();
            return;
        }
        long startTime2 = list.get(i).getStartTime();
        long endTime2 = list.get(i).getEndTime();
        RectF rectF2 = this.allData.get(i);
        this.allData.get(i).set(rectF2.left, (this.mSpaceH * ((float) (startTime2 + 7200))) / 3600.0f, rectF2.right, (this.mSpaceH * ((float) (endTime2 + 7200))) / 3600.0f);
        invalidate();
    }

    public void setOnPaiChengShowListener(OnPaiChengShowListener onPaiChengShowListener) {
        this.mListener = onPaiChengShowListener;
    }

    public void deleteItem(int i) {
        if (i < this.allData.size() && i < this.allColor.size()) {
            this.allData.remove(i);
            this.allColor.remove(i);
            invalidate();
        }
    }
}