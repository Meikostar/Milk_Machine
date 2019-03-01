package com.mykar.framework.ui.view.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mykar.framework.R;

/**
 * 圆形图片
 * */
public class CircleImageView extends WebImageView {

	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 1;

	private static final int DEFAULT_BOREDER_WIDTH = 0;
	private static final int DEFAULT_BOREDER_COLOR = Color.BLACK;

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private int mBorderColor = DEFAULT_BOREDER_COLOR;
	private int mBorderWidth = DEFAULT_BOREDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapHeight;
	private int mBitmapWidth;

	private float mDrawableRadius;
	private float mBorderRadius;

	private boolean mReady;
	private boolean mSetupPending;

	public CircleImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated constructor stub
		super(context, attrs, defStyle);
		// 自定义圆形的边框宽度和颜色属性
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView, defStyle, 0);
		mBorderWidth = array
				.getDimensionPixelSize(
						R.styleable.CircleImageView_border_width,
						DEFAULT_BOREDER_WIDTH);
		mBorderColor = array.getColor(R.styleable.CircleImageView_border_color,
				DEFAULT_BOREDER_COLOR);
		array.recycle();
		init();

	}

	@Override
	public ScaleType getScaleType() {
		return SCALE_TYPE;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}

		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
				mBitmapPaint);
		if (mBorderWidth != 0) {
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
					mBorderPaint);
		}
	}

	private void init() {
		super.setScaleType(SCALE_TYPE);
		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	private void setup() {
		// TODO Auto-generated method stub
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (mBitmap == null) {
			return;
		}

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);

		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
                    (mBorderRect.width() - mBorderWidth) / 2);

		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
				- mBorderWidth, mBorderRect.height() - mBorderWidth);
		mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                    mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
				* mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
				(int) (dy + 0.5f) + mBorderWidth);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                                COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

}
