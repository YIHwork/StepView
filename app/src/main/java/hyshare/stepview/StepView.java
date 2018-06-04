package hyshare.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by dev_hy on 2018/5/30.
 */

public class StepView extends LinearLayout
{
  private int mPaddingLeft;//父控件LinearLayout android:paddingLeft/paddingStart对应的值
  private boolean stepWidth_wrap;//stepWidth是固定大小还是依赖步骤视图内容大小
  private int stepWidth;//步骤视图宽度
  private int stepPaddingLeft;
  private int stepPaddingRight;

  private int stepNode_layoutReference;//节点位置参考对象的资源ID
  private Drawable stepNodeDrawable;//节点视图
  private boolean stepNodeSize_wrap;//节点size是否依赖drawable
  private int stepNodeSize;//节点size

  private int stepLine;//步骤连线color
  private int stepLineWidth;
  private int stepLineDashGap;
  private int stepLineDashWidth;

  private int stepLine1;//节点上半部分连线color
  private int stepLine1_width;
  private int stepLine1_dashGap;
  private int stepLine1_dashWidth;
  private boolean stepLine1_visible;//是否显示节点上半部分连线

  private int stepLine2;//节点下半部分连线color
  private int stepLine2_width;
  private int stepLine2_dashGap;
  private int stepLine2_dashWidth;
  private boolean stepLine2_visible;//是否显示节点下半部分连线

  private int stepDrawableWidth;//可绘制步骤视图的宽度大小
  private int nodePositionX;//节点x方向坐标，非屏幕坐标
  private int nodePositionY;//节点y方向坐标，非屏幕坐标

  private Paint mPaint;
  private Rect mBound;
  //  private Bitmap bitmap;
  private Canvas mCanvas;
  private Path path;
  private Xfermode mXfermode_SRC_IN;

  public StepView(Context context)
  {
    super(context);
    init(context, null);
  }

  public StepView(Context context, @Nullable AttributeSet attrs)
  {
    super(context, attrs);
    init(context, attrs);
  }

  public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs)
  {
    setWillNotDraw(false);
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mBound = new Rect();
    mCanvas = new Canvas();
    path = new Path();
    mXfermode_SRC_IN = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);


    float density = MetricsUtil.getDensity(context);
    int defaultPadding = (int) (5 * density);
    int defaultStepNodeReference = -1;
    int defaultStepNodeSize = (int) (7 * density);
    int defaultColor = Color.argb(0xff, 0xaa, 0xaa, 0xaa);
    int defaultLineWidth = (int) (1 * density);
    boolean defaultLineVisible = true;

    //初始化时缓存LinearLayout原始paddingLeft，替代onLayout中getPaddingLeft，
    //防止onLayout中paddingLeft+stepWidth引起paddingLeft不断增加
    mPaddingLeft = super.getPaddingLeft();
    if (attrs == null) {
      stepWidth_wrap = true;
      stepWidth = -1;
      stepPaddingLeft = defaultPadding;
      stepPaddingRight = defaultPadding;
      stepNode_layoutReference = defaultStepNodeReference;
      stepNodeDrawable = new ColorDrawable(defaultColor);
      stepNodeSize_wrap = true;
      stepNodeSize = defaultStepNodeSize;
      stepLine = defaultColor;
      stepLineWidth = defaultLineWidth;
      stepLineDashGap = -1;
      stepLineDashWidth = -1;
      stepLine1 = defaultColor;
      stepLine1_width = stepLineWidth;
      stepLine1_dashGap = stepLineDashGap;
      stepLine1_dashWidth = stepLineDashWidth;
      stepLine1_visible = defaultLineVisible;
      stepLine2 = stepLine;
      stepLine2_width = stepLineWidth;
      stepLine2_dashGap = stepLineDashGap;
      stepLine2_dashWidth = stepLineDashWidth;
      stepLine2_visible = defaultLineVisible;
    } else {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.stepView);
      stepWidth = typedArray.getDimensionPixelSize(R.styleable.stepView_stepWidth, -1);
      if (stepWidth == -1) {
        stepWidth_wrap = true;
      }
      stepPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.stepView_stepPaddingLeft, defaultPadding);
      stepPaddingRight = typedArray.getDimensionPixelSize(R.styleable.stepView_stepPaddingRight, defaultPadding);

      stepNode_layoutReference = typedArray.getResourceId(R.styleable.stepView_stepNodeLayoutReference, defaultStepNodeReference);
      stepNodeDrawable = typedArray.getDrawable(R.styleable.stepView_stepNode);
      if (stepNodeDrawable == null) {
        stepNodeDrawable = new ColorDrawable(defaultColor);
      }
      stepNodeSize = typedArray.getDimensionPixelSize(R.styleable.stepView_stepNodeSize, -1);
      if (stepNodeSize == -1) {
        stepNodeSize_wrap = true;
      }

      stepLine = typedArray.getColor(R.styleable.stepView_stepLine, defaultColor);
      stepLineWidth = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLineWidth, defaultLineWidth);
      stepLineDashGap = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLineDashGap, -1);
      stepLineDashWidth = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLineDashWidth, -1);

      stepLine1 = typedArray.getColor(R.styleable.stepView_stepLine1, stepLine);
      stepLine1_width = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine1Width, stepLineWidth);
      stepLine1_dashGap = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine1DashGap, stepLineDashGap);
      stepLine1_dashWidth = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine1DashWidth, stepLineDashWidth);
      stepLine1_visible = typedArray.getBoolean(R.styleable.stepView_stepLine1Visible, defaultLineVisible);

      stepLine2 = typedArray.getColor(R.styleable.stepView_stepLine2, stepLine);
      stepLine2_width = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine2Width, stepLineWidth);
      stepLine2_dashGap = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine2DashGap, stepLineDashGap);
      stepLine2_dashWidth = typedArray.getDimensionPixelSize(R.styleable.stepView_stepLine2DashWidth, stepLineDashWidth);
      stepLine2_visible = typedArray.getBoolean(R.styleable.stepView_stepLine2Visible, defaultLineVisible);
      typedArray.recycle();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    if (stepNodeSize_wrap) {
      if (stepNodeDrawable.getIntrinsicWidth() <= 0 || stepNodeDrawable.getIntrinsicHeight() <= 0) {
        stepNodeSize = (int) (7 * MetricsUtil.getDensity(getContext()));
      } else {
        stepNodeSize = Math.max(stepNodeDrawable.getIntrinsicWidth(), stepNodeDrawable.getIntrinsicHeight());
      }
    }
    if (stepWidth_wrap) {
      stepWidth = stepPaddingLeft + Math.max(stepNodeSize, Math.max(stepLine1_width, stepLine2_width)) + stepPaddingRight;
    }
    stepDrawableWidth = stepWidth - stepPaddingLeft - stepPaddingRight;
    int paddingLeft = mPaddingLeft + stepWidth;
    int paddingRight = getPaddingRight();
    int paddingTop = getPaddingTop();
    int paddingBottom = getPaddingBottom();
    super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    super.onLayout(changed, l, t, r, b);

    //确定节点位置
    nodePositionX = stepPaddingLeft + stepDrawableWidth / 2;
    if (stepNode_layoutReference == -1) {
      //如果没有设置节点参考对象，节点位置默认放在垂直中间位置
      nodePositionY = getHeight() / 2;
    } else {
      View referenceView = findViewById(stepNode_layoutReference);
      int refTop = 0;
      View viewGroup = referenceView;
      while (!this.equals(viewGroup)) {
        refTop += viewGroup.getTop();
        viewGroup = (View) viewGroup.getParent();
      }
      int refPaddingTop = referenceView.getPaddingTop();
      int refPaddingBottom = referenceView.getPaddingBottom();
      int refHeight = referenceView.getHeight();
      nodePositionY = refTop + (refHeight + refPaddingTop - refPaddingBottom) / 2;//refTop + refPaddingTop + (refHeight - refPaddingTop - refPaddingBottom) / 2
    }
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (stepDrawableWidth <= 0) {
      return;
    }

    //绘制线1
    if (stepLine1_visible) {
      mPaint.setColor(stepLine1);
      float strokeWidth = mPaint.getStrokeWidth();
      mPaint.setStrokeWidth(Math.min(stepDrawableWidth, stepLine1_width));
      boolean isDash = stepLine1_dashGap > 0 && stepLine1_dashWidth > 0;
      if (isDash) {
        Paint.Style mStyle = mPaint.getStyle();
        PathEffect mPathEffect = mPaint.getPathEffect();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{stepLine1_dashWidth, stepLine1_dashGap}, 0));
        path.reset();
        path.moveTo(nodePositionX, 0);
        path.lineTo(nodePositionX, nodePositionY);
        canvas.drawPath(path, mPaint);
        mPaint.setPathEffect(mPathEffect);
        mPaint.setStyle(mStyle);
      } else {
        canvas.drawLine(nodePositionX, 0, nodePositionX, nodePositionY, mPaint);
      }
      mPaint.setStrokeWidth(strokeWidth);
    }

    //绘制线2
    if (stepLine2_visible) {
      mPaint.setColor(stepLine2);
      float strokeWidth = mPaint.getStrokeWidth();
      mPaint.setStrokeWidth(Math.min(stepDrawableWidth, stepLine2_width));
      boolean isDash = stepLine2_dashGap > 0 && stepLine2_dashWidth > 0;
      if (isDash) {
        Paint.Style mStyle = mPaint.getStyle();
        PathEffect mPathEffect = mPaint.getPathEffect();
        mPaint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo(nodePositionX, nodePositionY);
        path.lineTo(nodePositionX, getHeight());
        PathEffect pathEffect = new DashPathEffect(new float[]{stepLine2_dashWidth, stepLine2_dashGap}, 0);
        mPaint.setPathEffect(pathEffect);
        canvas.drawPath(path, mPaint);
        mPaint.setPathEffect(mPathEffect);
        mPaint.setStyle(mStyle);
      } else {
        canvas.drawLine(nodePositionX, nodePositionY, nodePositionX, getHeight(), mPaint);
      }
      mPaint.setStrokeWidth(strokeWidth);
    }

    //绘制节点
    if (stepNodeSize <= 0) {
      return;
    }
    if (stepNodeDrawable != null) {
      if (stepNodeDrawable instanceof ColorDrawable) {
        drawStepNode(canvas, (ColorDrawable) stepNodeDrawable);
      } else {
        drawStepNode(canvas, stepNodeDrawable);
      }
    }
  }

  private void drawStepNode(Canvas canvas, ColorDrawable colorDrawable)
  {
    int nodeSize = Math.min(stepDrawableWidth, stepNodeSize);
    int nodeColor = colorDrawable.getColor();
    mPaint.setColor(nodeColor);
    canvas.drawCircle(nodePositionX, nodePositionY, nodeSize / 2f, mPaint);
  }

  private void drawStepNode(Canvas canvas, Drawable drawable)
  {
    int nodeSize = Math.min(stepDrawableWidth, stepNodeSize);
    Bitmap stepNodeBitmap = drawable2bitmap(drawable, nodeSize);
    int bitmapWidth = stepNodeBitmap.getWidth();
    int bitmapHeight = stepNodeBitmap.getHeight();
    int nodeWidth, nodeHeight;
    if (bitmapWidth > bitmapHeight) {
      nodeWidth = nodeSize;
      nodeHeight = bitmapHeight * nodeWidth / bitmapWidth;
    } else {
      nodeHeight = nodeSize;
      nodeWidth = bitmapWidth * nodeHeight / bitmapHeight;
    }
    int left = nodePositionX - nodeWidth / 2;
    int right = left + nodeWidth;
    int top = nodePositionY - nodeHeight / 2;
    int bottom = top + nodeHeight;
    mBound.set(left, top, right, bottom);

    Xfermode xfermode = mPaint.getXfermode();
    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    mCanvas.setBitmap(bitmap);

//      RectF rectF = new RectF(mBound);
//      float rx = rectF.width() / 2;
//      float ry = rectF.height() / 2;
//      mCanvas.drawRoundRect(rectF, rx, ry, mPaint);
    mCanvas.drawCircle(nodePositionX, nodePositionY, nodeSize / 2f, mPaint);
    mPaint.setXfermode(mXfermode_SRC_IN);
    mCanvas.drawBitmap(stepNodeBitmap, null, mBound, mPaint);
    mPaint.setXfermode(xfermode);
    canvas.drawBitmap(bitmap, 0, 0, mPaint);
  }

  /*重写setPadding，当设置padding时缓存LinearLayout原始paddingLeft，替代onLayout中getPaddingLeft
  * 防止onLayout中paddingLeft+stepWidth引起paddingLeft不断增加*/
  @Override
  public void setPadding(int left, int top, int right, int bottom)
  {
    mPaddingLeft = left < 0 ? 0 : left;
    super.setPadding(mPaddingLeft, top, right, bottom);
  }

  @Override
  public int getPaddingLeft()
  {
    int paddingLeft = super.getPaddingLeft();
    if (paddingLeft != mPaddingLeft) {
      paddingLeft = paddingLeft - stepWidth;
    }
    return paddingLeft;
  }

  private Bitmap drawable2bitmap(Drawable drawable, int defaultSize)
  {
    // 取 drawable 的长宽
    int w = drawable.getIntrinsicWidth();
    int h = drawable.getIntrinsicHeight();

    if (w <= 0) {
      w = defaultSize;
    }
    if (h <= 0) {
      h = defaultSize;
    }
    // 取 drawable 的颜色格式
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
        : Bitmap.Config.RGB_565;
    // 建立对应 bitmap
    Bitmap bitmap = Bitmap.createBitmap(w, h, config);
    // 建立对应 bitmap 的画布
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, w, h);
    // 把 drawable 内容画到画布中
    drawable.draw(canvas);
    return bitmap;
  }

  public void setStepWidthResource(@DimenRes int resId)
  {
    setStepWidth(getResources().getDimensionPixelSize(resId));
  }

  public void setStepWidth(int stepWidth)
  {
    boolean requestLayout = this.stepWidth != stepWidth;
    stepWidth_wrap = false;
    this.stepWidth = stepWidth;
    if (requestLayout) {
      requestLayout();
    }
  }

  public void setStepNodeResource(@DrawableRes int resId)
  {
    boolean invalidateCache = true;
    boolean requestLayout = stepNodeSize_wrap;
    Drawable drawable = getResources().getDrawable(resId);
    if (stepNodeDrawable != null && stepNodeDrawable instanceof ColorDrawable && drawable instanceof ColorDrawable) {
      if (((ColorDrawable) stepNodeDrawable).getColor() == ((ColorDrawable) drawable).getColor()) {
        invalidateCache = false;
      }
      requestLayout = false;
    }
    if (invalidateCache) {
      stepNodeDrawable = drawable;
      if (requestLayout) {
        requestLayout();
      }
      invalidate();
    }
  }

  public void setStepNodeColor(@ColorInt int color)
  {
    boolean invalidateCache = true;
    boolean requestLayout = stepNodeSize_wrap;
    if (stepNodeDrawable != null && stepNodeDrawable instanceof ColorDrawable) {
      if (((ColorDrawable) stepNodeDrawable).getColor() == color) {
        invalidateCache = false;
      }
      requestLayout = false;
    }
    if (invalidateCache) {
      stepNodeDrawable = new ColorDrawable(color);
      if (requestLayout) {
        requestLayout();
      }
      invalidate();
    }
  }

  public void setStepNodeSize(int nodeSize)
  {
    boolean requestLayout = true;
    boolean isWrap = nodeSize < 0;
    if ((isWrap && stepNodeSize_wrap) || (!isWrap && (stepNodeSize == nodeSize))) {
      requestLayout = false;
    }
    stepNodeSize_wrap = isWrap;
    stepNodeSize = nodeSize;
    if (requestLayout) {
      requestLayout();
    }
  }

  public void setStepLineResource(@ColorRes int resId)
  {
    setStepLine(getResources().getColor(resId));
  }

  public void setStepLine(@ColorInt int color)
  {
    boolean invalidateCache = (stepLine1 != color && stepLine1_visible) || (stepLine2 != color && stepLine2_visible);
    stepLine = color;
    stepLine1 = color;
    stepLine2 = color;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLineWidth(int width)
  {
    boolean invalidateCache = (stepLine1_width != width && stepLine1_visible) || (stepLine2_width != width && stepLine2_visible);
    stepLineWidth = width;
    stepLine1_width = width;
    stepLine2_width = width;
    if (invalidateCache) {
      if (stepNodeSize_wrap) {
        requestLayout();
      }
      invalidate();
    }
  }

  public void setStepLineDashGap(int dashGap)
  {
    boolean invalidateCache = false;
    boolean isLine1Dash = dashGap > 0 && stepLine1_dashWidth > 0;
    boolean isLine2Dash = dashGap > 0 && stepLine2_dashWidth > 0;
    if (stepLine1_visible && isLine1Dash && stepLine1_dashGap != dashGap) {
      invalidateCache = true;
    } else if (stepLine2_visible && isLine2Dash && stepLine2_dashGap != dashGap) {
      invalidateCache = true;
    }
    stepLineDashGap = dashGap;
    stepLine1_dashGap = dashGap;
    stepLine2_dashGap = dashGap;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLineDashWidth(int dashWidth)
  {
    boolean invalidateCache = false;
    boolean isLine1Dash = dashWidth > 0 && stepLine1_dashGap > 0;
    boolean isLine2Dash = dashWidth > 0 && stepLine2_dashGap > 0;
    if (stepLine1_visible && isLine1Dash && stepLine1_dashWidth != dashWidth) {
      invalidateCache = true;
    } else if (stepLine2_visible && isLine2Dash && stepLine2_dashWidth != dashWidth) {
      invalidateCache = true;
    }
    stepLineDashWidth = dashWidth;
    stepLine1_dashWidth = dashWidth;
    stepLine2_dashWidth = dashWidth;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLine1(@ColorInt int color)
  {
    boolean invalidateCache = stepLine1 != color && stepLine1_visible;
    stepLine1 = color;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLine1Width(int width)
  {
    boolean invalidateCache = stepLine1_visible && stepLine1_width != width;
    stepLine1_width = width;
    if (invalidateCache) {
      if (stepNodeSize_wrap) {
        requestLayout();
      }
      invalidate();
    }
  }

  public void setStepLine1DashGap(int dashGap)
  {
    boolean isLine1Dash = dashGap > 0 && stepLine1_dashWidth > 0;
    boolean invalidateCache = stepLine1_visible && isLine1Dash && stepLine1_dashGap != dashGap;
    stepLine1_dashGap = dashGap;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLine1DashWidth(int dashWidth)
  {
    boolean isLine1Dash = dashWidth > 0 && stepLine1_dashGap > 0;
    boolean invalidateCache = stepLine1_visible && isLine1Dash && stepLine1_dashWidth != dashWidth;
    stepLine1_dashWidth = dashWidth;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setLine1Visible(boolean isVisible)
  {
    if (this.stepLine1_visible == isVisible) {
      return;
    }
    this.stepLine1_visible = isVisible;
    requestLayout();
  }

  public void setStepLine2(@ColorInt int color)
  {
    boolean invalidateCache = stepLine2 != color && stepLine2_visible;
    stepLine2 = color;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLine2Width(int width)
  {
    boolean invalidateCache = stepLine2_visible && stepLine2_width != width;
    stepLine2_width = width;
    if (invalidateCache) {
      if (stepNodeSize_wrap) {
        requestLayout();
      }
      invalidate();
    }
  }

  public void setStepLine2DashGap(int dashGap)
  {
    boolean isLine2Dash = dashGap > 0 && stepLine2_dashWidth > 0;
    boolean invalidateCache = stepLine2_visible && isLine2Dash && stepLine2_dashGap != dashGap;
    stepLine2_dashGap = dashGap;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setStepLine2DashWidth(int dashWidth)
  {
    boolean isLine2Dash = dashWidth > 0 && stepLine2_dashGap > 0;
    boolean invalidateCache = stepLine2_visible && isLine2Dash && stepLine2_dashWidth != dashWidth;
    stepLine2_dashWidth = dashWidth;
    if (invalidateCache) {
      invalidate();
    }
  }

  public void setLine2Visible(boolean isVisible)
  {
    if (this.stepLine2_visible == isVisible) {
      return;
    }
    this.stepLine2_visible = isVisible;
    requestLayout();
  }
}
