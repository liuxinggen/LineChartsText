package com.text.linechartstext;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 类名：com.text.linechartstext
 * 时间：2017/12/1 9:45
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class LineCharts extends View {
    private static final String TAG = "LineCharts";
    private Context mContext;

    /**
     * XY轴画笔
     */
    private Paint mPaintXY;
    /**
     * 折线画笔,最大值
     */
    private Paint mPaintLineMax;
    /**
     * 折线画笔，最小值
     */
    private Paint mPaintLineMin;
    /**
     * 标题画笔
     */
    private Paint mPaintTitle;
    /**
     * 刻度画笔
     */
    private Paint mPaintScale;
    /**
     * 折线宽度
     */
    private float mLineSize;
    /**
     * 最大值和最小值的颜色
     */
    private int mLineColorMax;
    private int mLineColorMin;
    /**
     * X,Y轴的颜色
     */
    private int mXYColor;
    /**
     * X,Y轴的宽度
     */
    private int mXYSize;
    /**
     * 标题大小
     */
    private float mTitleSize;
    /**
     * 刻度大小
     */
    private float mScaleSize;
    /**
     * 标题颜色
     */
    private int mTitleColor;
    /**
     * 获取屏幕的宽高和分辨率
     */
    private int screenWidth, screenHeight;
    private float screenDensity;
    /**
     * 原点坐标
     */
    private float pointX, pointY;
    /**
     *
     */
    private int left, top, right, bottom;
    private float translationX, translationY;
    private float x, y;
    /**
     * 数据
     */
    private String[] dataX;
    private Float[] dataY;
    private String[] lineData;
    private String title;

    /**
     * 每个刻度的最小值
     */
    public float divideSize;
    /**
     * 用到的数量
     */
    public int px20, px5, px10, px4, px2;

    public LineCharts(Context context) {
        this(context, null);
    }

    public LineCharts(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineCharts(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        px20 = dip2px(context, 20);
        px5 = dip2px(context, 5);
        px10 = dip2px(context, 10);
        px4 = dip2px(context, 4);
        px2 = dip2px(context, 2);


        TypedArray array = context
                .obtainStyledAttributes(attrs, R.styleable.LineChartsView);
        mLineColorMax = Color.parseColor("#1BC1F3");
        mLineColorMin = Color.parseColor("#707677");
        mXYColor = Color.parseColor("#1BC1F3");
        mTitleColor = Color.parseColor("#1BC1F3");
        mScaleSize = px10;


        mLineSize = array
                .getDimensionPixelOffset(R.styleable.LineChartsView_mPaintWidth_line,
                        px2);
        mLineColorMax = array.getColor(R.styleable.LineChartsView_mPaintColor_max, mLineColorMax);
        mLineColorMin = array.getColor(R.styleable.LineChartsView_mPaintColor_min, mLineColorMin);
        mXYColor = array.getColor(R.styleable.LineChartsView_mPaintColor_XY, mXYColor);
        mTitleSize = array
                .getDimensionPixelOffset(R.styleable.LineChartsView_mPaintWidth_title,
                        px20);
        mXYSize = array
                .getDimensionPixelOffset(R.styleable.LineChartsView_mPaintWidth_XY,
                        px2);
        mTitleColor = array.getColor(R.styleable.LineChartsView_mPaintColor_title, mTitleColor);
        array.recycle();

        mPaintXY = new Paint();
        mPaintXY.setAntiAlias(true);
        mPaintXY.setStrokeWidth(mXYSize);
        mPaintXY.setColor(mXYColor);
        mPaintXY.setStyle(Paint.Style.STROKE);

        mPaintLineMax = new Paint();
        mPaintLineMax.setAntiAlias(true);
        mPaintLineMax.setStrokeWidth(mLineSize);
        mPaintLineMax.setColor(mLineColorMax);
        mPaintLineMax.setStyle(Paint.Style.STROKE);

        mPaintLineMin = new Paint();
        mPaintLineMin.setAntiAlias(true);
        mPaintLineMin.setStrokeWidth(mLineSize);
        mPaintLineMin.setColor(mLineColorMin);
        mPaintLineMin.setStyle(Paint.Style.STROKE);


        mPaintTitle = new Paint();
        mPaintTitle.setAntiAlias(true);
        mPaintTitle.setTextSize(mTitleSize);
        mPaintTitle.setColor(mTitleColor);
        mPaintTitle.setStyle(Paint.Style.STROKE);

        mPaintScale = new Paint();
        mPaintScale.setAntiAlias(true);
        mPaintScale.setTextSize(mScaleSize);
        mPaintScale.setColor(mTitleColor);
        mPaintScale.setStyle(Paint.Style.STROKE);

        //获取屏幕的宽高
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenDensity = dm.density;
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = widthMeasure(widthMeasureSpec);
        int height = heighthMeasure(heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 下面开始绘制
         * 1.先确定X轴和Y轴的位置，我们固定到距离view底部10dp，距离view左边10dp的位置。
         * 2.确定X轴和Y轴的长度，X轴：距离view右边10dp；Y轴：距离view上边20dp(留下空间绘制标题)。
         * 3.平分X轴和Y轴
         * 4.绘制折线
         * 5.绘制折线上的数据点
         */
        systemSize();
        //绘制坐标轴
        pointX = left + px20 + (float) getTextWidth(mPaintXY, "" + dataY[0]);
        pointY = bottom - top - px20;
        Log.i(TAG, "onDraw: pointX=" + pointX + ",pointY=" + pointY);
        //Y轴
        float yEndY = top +
                px5 +
                dip2px(mContext, (float) getTextHeight(mPaintTitle));
        canvas.drawLine(pointX, pointY, pointX, yEndY, mPaintXY);
        //X轴
        float xEndX = right + left - px10;
        canvas.drawLine(pointX, pointY, xEndX, pointY, mPaintXY);

        //绘制标题
        float textWidth = (float) getTextWidth(mPaintTitle, title);
        float mTitleX = (right + left) / 2 - textWidth / 2;
        float mTitleY = yEndY - px4;
        canvas.drawText(title, mTitleX, mTitleY, mPaintTitle);


        //绘制刻度
        float scaleLength = px4;
        //X轴
        float xLength = xEndX - pointX;
        float scaleX = xLength / dataX.length;
        //刻度的x轴
        float xScaleX = scaleX + pointX;
        //刻度的y轴
        float xScaleY = (pointY - scaleLength);
        //最后一个不去绘制
        for (int i = 0; i < (dataX.length); i++) {
            canvas.drawLine(xScaleX, pointY, xScaleX, xScaleY, mPaintXY);
            canvas.drawText(dataX[i],
                    (float) (xScaleX - (getTextWidth(mPaintScale, dataX[i])) / 2),
                    (pointY + (float) getTextHeight(mPaintScale)),
                    mPaintScale);
            xScaleX += scaleX;
        }
        //Y轴
        float yLength = pointY - yEndY;
        float scaleY = yLength / dataY.length;
        //刻度的x轴
        float yScaleX = (pointX + scaleLength);
        //刻度的y轴
        float yScaleY = pointY - scaleY;
        //最后一个不去绘制
        for (int i = 0; i < (dataY.length); i++) {
            canvas.drawLine(pointX, yScaleY, yScaleX, yScaleY, mPaintXY);
            canvas.drawText("" + dataY[i],
                    ((pointX - px2 -
                            (float) (getTextWidth(mPaintScale, "" + dataY[0])))),
                    (yScaleY + ((float) getTextHeight(mPaintScale) / 2)),
                    mPaintScale);
            yScaleY -= scaleY;
        }

        //绘制折线
        xScaleX = scaleX + pointX;
        float tempX = scaleX + pointX;
        for (int i = 0; i < dataX.length; i++) {
            //X轴坐标：xScaleX
            //Y轴坐标：pointY - scaleY/10*lineData[i]
            canvas.drawText(lineData[i],
                    (float) (xScaleX - (getTextWidth(mPaintScale, lineData[i]) / 2)),
                    ((pointY - (scaleY / divideSize * Float.parseFloat(lineData[i]))) -
                            px10),
                    mPaintScale);
            if ((i + 1) < dataX.length) {
                tempX += scaleX;
                canvas.drawLine(xScaleX,
                        (pointY - (scaleY / divideSize * Float.parseFloat(lineData[i]))),
                        tempX,
                        (pointY - (scaleY / divideSize * Float.parseFloat(lineData[i + 1]))),
                        mPaintLineMax);
                xScaleX += scaleX;
            }
        }


    }

    /**
     * 系统上的一些值
     */
    private void systemSize() {
        //left,right是相对于父布局左边的距离
        //top,bottom是相对于父布局上边的距离
        left = getLeft();
        top = getTop();
        right = getRight();
        bottom = getBottom();

        //translationX，translationY是View左上角相对于父布局的偏移量
        translationX = getTranslationX();
        translationY = getTranslationY();

        //x,y是View左上角的坐标
        x = getX();
        y = getY();

        //这几个参数的换算关系如下
        x = left + translationX;
        y = top + translationY;

        Log.i(TAG, "onDraw: left=" + left + ",top=" + top +
                ",right=" + right + ",bottom=" + bottom);
        Log.i(TAG, "onDraw: translationX=" + translationX + ",translationY=" + translationY);
        Log.i(TAG, "onDraw: x=" + x + ",y=" + y);
    }


    public void setData(String[] dataX, Float[] dataY, String[] lineData) {
        this.dataX = dataX;
        this.dataY = dataY;
        this.lineData = lineData;
        divideSize = (float) (dataY[1] - dataY[0]);
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }


    /**
     * 测量高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int heighthMeasure(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = screenHeight / 2;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 测量宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int widthMeasure(int widthMeasureSpec) {

        int result = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = screenWidth;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取文字的高度
     *
     * @param mPaint
     * @return
     */
    public double getTextHeight(Paint mPaint) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 获取文字的宽度
     *
     * @param mPaint
     * @return
     */
    public double getTextWidth(Paint mPaint, String text) {
        return mPaint.measureText(text, 0, text.length());
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}


