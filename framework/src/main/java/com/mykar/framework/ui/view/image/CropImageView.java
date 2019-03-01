/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mykar.framework.ui.view.image;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.mykar.framework.utils.Log;


/**
 * The activity can crop specific region of interest from an image.截图？
 */

public class CropImageView extends ImageViewTouchBase
{
	private static final String TAG = "CropImageView";
	
	int mFirstPositionId;
	float mFirstX, mFirstY;
	float mLastX, mLastY;
	float mZoomCenterX;
	float mZoomCenterY;
	double mZoomOriginalDistance;
	double mZoomBaseRatio = 1.0;
	double mZoomLasttimeRatio = 1.0;

	boolean mIsLastimeTwoFingure = false;
	double mScreenWidth = 480;
	

	public CropImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mScreenWidth = getDevicePixelWidth(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
    public CropImageView(Context context, AttributeSet attrs, int defStyle)
    {
	    super(context, attrs, defStyle);
		mScreenWidth = getDevicePixelWidth(context);
    }

	/**
	 * @param context
	 */
    public CropImageView(Context context)
    {
	    super(context);
		mScreenWidth = getDevicePixelWidth(context);
    }
    
	public void handleZoom(float scale, float centerX, float centerY)
	{
		float[] coordinates = new float[]
		{ centerX, centerY };
		getImageMatrix().mapPoints(coordinates);
		zoomTo(scale, coordinates[0], coordinates[1], 1F);
	}

	private void handlePan(float dx, float dy)
	{
		panBy(dx, dy);
	}

	// private

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		Log.d(TAG, "onTouchEvent pointcount =" + event.getPointerCount());
		if (event.getPointerCount() == 1)
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN: // CR: inline case blocks.
				
				mFirstPositionId = event.getPointerId(0);
				mFirstX = event.getX();
				mFirstY = event.getY();
				mLastX = event.getX();
				mLastY = event.getY();
				
				break;
			// CR: vertical space before case blocks.
			case MotionEvent.ACTION_UP:
				mLastX = event.getX();
				mLastY = event.getY();
				
				center(true, true, 200);
				int posid = event.getPointerId(0);
				if(Math.abs(mFirstX - mLastX) < 20 && Math.abs(mFirstY - mLastY) < 20 && posid==mFirstPositionId)
					performClick();
				break;
			case MotionEvent.ACTION_MOVE:

				if(mIsLastimeTwoFingure)
				{
					mLastX = event.getX();
					mLastY = event.getY();
				}
				else
				{
					float panDeltaX = event.getX() - mLastX;
					float panDeltaY = event.getY() - mLastY;
	
					handlePan(panDeltaX, panDeltaY);
					mLastX = event.getX();
					mLastY = event.getY();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				//ViewPager在滑动到头或底部时，会拦截ACTION_UP的事件，但还会发出ACTION_CANCEL
				center(true, true, 200);
				break;
			}

			mIsLastimeTwoFingure = false;
		}
		else if (event.getPointerCount() == 2)
		{
			
			if(mIsLastimeTwoFingure==false)
			{
				mZoomOriginalDistance = Math
				        .sqrt(Math.pow(event.getX(0) - event.getX(1), 2)
                                        + Math.pow(event.getY(0) - event.getY(1), 2));
				mZoomCenterX = (float)((event.getX(0) + event.getX(1))/2.0);
				mZoomCenterY = (float)((event.getY(0) + event.getY(1))/2.0);
				
				mZoomBaseRatio = mZoomLasttimeRatio;
			}
			
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN: // CR: inline case blocks.
				mFirstPositionId = event.getPointerId(0);
				mFirstX = event.getX();
				mFirstY = event.getY();
				break;
			// CR: vertical space before case blocks.
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				{
				double distance = Math.sqrt(Math.pow(event.getX(0)
                                - event.getX(1), 2)
                                + Math.pow(event.getY(0) - event.getY(1), 2));
				int change = (int) (Math.floor(distance - mZoomOriginalDistance));
				if (Math.abs(change) > 4)
				{
					//int power = 2;
					//double signnum = Math.signum(change)*(Math.signum((power%2)-0.5)*-1.0);
					//double ratio = (1.0f + Math.pow(change/400.0f,power)*signnum);
					double ratio = (1.0f + change/(mScreenWidth*0.75));
					ratio = mZoomBaseRatio*ratio;
					ratio = Math.max(ratio, 0.5);
					ratio = Math.min(ratio, 3.0);
					mZoomLasttimeRatio = ratio;
					handleZoom((float)ratio, (float)mZoomCenterX,(float)mZoomCenterY);
					//TCLogger.e("touch", "scale:" + ratio + ",xcenter:"+ mZoomCenterX+ ",ycenter:"+ mZoomCenterY);
				}

				break;
				}
			}
			mIsLastimeTwoFingure = true;
		}
		return this.isEnabled();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
	    super.finalize();
	}

	// Pan the displayed image to make sure the cropping rectangle is visible.
	// private void ensureVisible(HighlightView hv) {
	// Rect r = hv.mDrawRect;
	//
	// int panDeltaX1 = Math.max(0, getLeft() - r.left);
	// int panDeltaX2 = Math.min(0, getRight() - r.right);
	//
	// int panDeltaY1 = Math.max(0, getTop() - r.top);
	// int panDeltaY2 = Math.min(0, getBottom() - r.bottom);
	//
	// int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
	// int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;
	//
	// if (panDeltaX != 0 || panDeltaY != 0) {
	// panBy(panDeltaX, panDeltaY);
	// }
	// }

	// If the cropping rectangle's size changed significantly, change the
	// view's center and scale according to the cropping rectangle.
	// private void centerBasedOnHighlightView(HighlightView hv) {
	// Rect drawRect = hv.mDrawRect;
	//
	// float width = drawRect.width();
	// float height = drawRect.height();
	//
	// float thisWidth = getWidth();
	// float thisHeight = getHeight();
	//
	// float z1 = thisWidth / width * .6F;
	// float z2 = thisHeight / height * .6F;
	//
	// float zoom = Math.min(z1, z2);
	// zoom = zoom * this.getScale();
	// zoom = Math.max(1F, zoom);
	//
	// if ((Math.abs(zoom - getScale()) / zoom) > .1) {
	// float[] coordinates = new float[] { hv.mCropRect.centerX(),
	// hv.mCropRect.centerY() };
	// getImageMatrix().mapPoints(coordinates);
	// zoomTo(zoom, coordinates[0], coordinates[1], 300F); // CR: 300.0f.
	// }
	//
	// ensureVisible(hv);
	// }
	/*
	 * 得到设备高度和宽度的方法
	 * */
	public int getDevicePixelWidth(Context app)
	{
		WindowManager mgr = (WindowManager)app.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		mgr.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}
	
	public int getDevicePixelHeight(Context app)
	{
		WindowManager mgr = (WindowManager)app.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		mgr.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;		
	}
}
