package com.xx.xxswitch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/5/19.
 */
public class XXSwitch extends View {

    Paint paint;
    //当前背景图
    private Bitmap switchBackgroupBitmap;
    //开关关闭时的背景图
    private Bitmap closedBackgroundBitmap;
    //开关打开时的背景图
    private Bitmap openedBackgroundBitmap;
    //滑块
    private Bitmap switchForegroupBitmap;

    private boolean isSwitchState = true; //开关状态

    private boolean isTouchState = false; //触摸状态

    private float currentPosition; // 当前开关位置

    private int maxPosition; // 开关滑动最大位置

    OnSwitchStateUpdateListener onSwitchStateUpdateListener;

    //设置前景图
    public void setForegroundDrawable(int switchForeground) {
        switchForegroupBitmap = BitmapFactory.decodeResource(getResources(), switchForeground);
    }

    public void setOpenStateDrawable(int openStateDrawable) {
        openedBackgroundBitmap = BitmapFactory.decodeResource(getResources(), openStateDrawable);
    }

    public void setCloseStateDrawable(int closeStateDrawable) {
        closedBackgroundBitmap = BitmapFactory.decodeResource(getResources(), closeStateDrawable);
    }

    public XXSwitch(Context context) {
        this(context, null);
    }

    public XXSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XXSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        //读取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XXSwitch);
        isSwitchState = array.getBoolean(R.styleable.XXSwitch_switch_on, false);
        openedBackgroundBitmap = ((BitmapDrawable) array.getDrawable(R.styleable.XXSwitch_open_bg)).getBitmap();
        closedBackgroundBitmap = ((BitmapDrawable) array.getDrawable(R.styleable.XXSwitch_close_bg)).getBitmap();
        switchForegroupBitmap = ((BitmapDrawable) array.getDrawable(R.styleable.XXSwitch_slip_foreground)).getBitmap();
        array.recycle();
        //设置初始化的属性
        paint = new Paint();
        switchBackgroupBitmap = isSwitchState ? openedBackgroundBitmap : closedBackgroundBitmap;
    }

    //测量出自定义控件的长宽
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
    }

    //绘制控件
    @Override
    protected void onDraw(Canvas canvas) {
        // 先绘制背景
        canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);

        if (isTouchState) {//如果处于触摸状态
            //滑块移动的距离
            float moveDistance = currentPosition - switchForegroupBitmap.getWidth() / 2.0f;
            maxPosition = switchBackgroupBitmap.getWidth() - switchForegroupBitmap.getWidth();
            //限制滑块只能在0-maxPosition之间滑动
            if (moveDistance < 0) {
                moveDistance = 0;
            }else if (moveDistance > maxPosition){
                moveDistance = maxPosition;
            }
            //绘制开关
            canvas.drawBitmap(switchForegroupBitmap, moveDistance, 0, paint);
        }else {//直接绘制开关
            if (isSwitchState) {//将开关置为开启状态
                maxPosition = switchBackgroupBitmap.getWidth() - switchForegroupBitmap.getWidth();
                canvas.drawBitmap(switchForegroupBitmap, maxPosition, 0, paint);
            }else {//将开关置为关闭状态
                canvas.drawBitmap(switchForegroupBitmap, 0, 0, paint);
            }
        }

    }

    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //处于触摸状态
                isTouchState = true;
                //获取滑块的坐标
                currentPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取滑块的坐标
                currentPosition = event.getX();
                //背景中间点的位置
                float midPosition = switchBackgroupBitmap.getWidth() / 2.0f;
                //还在移动时立即改变状态
                switchBackgroupBitmap = currentPosition > midPosition ? openedBackgroundBitmap : closedBackgroundBitmap;
                break;
            case MotionEvent.ACTION_UP:
                //触摸状态结束
                isTouchState = false;
                currentPosition = event.getX();
                //背景中间点的位置
                float centerPosition = switchBackgroupBitmap.getWidth() / 2.0f;
                //开关位置大于中间点的位置则显示关，否则显示开
                boolean currentState = currentPosition > centerPosition;
                //如果当然状态不相同且绑定了监听对象 则执行监听方法
                if (currentState != isSwitchState && onSwitchStateUpdateListener != null) {
                    onSwitchStateUpdateListener.onSwitchUpdate(currentState);
                }
                //当前状态置为开关状态
                isSwitchState = currentState;
                switchBackgroupBitmap = isSwitchState ? openedBackgroundBitmap : closedBackgroundBitmap;
                break;
        }
        //不断重新绘制界面
        invalidate();
        return true;
    }

    //添加事件监听接口
    public interface OnSwitchStateUpdateListener {
        //回调方法
        void onSwitchUpdate(boolean switchState);
    }

    public void setOnSwitchStateUpdateListener(OnSwitchStateUpdateListener listener) {
        this.onSwitchStateUpdateListener = listener;
    }

}
