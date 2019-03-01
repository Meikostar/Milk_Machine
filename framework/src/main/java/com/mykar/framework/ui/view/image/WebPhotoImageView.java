package com.mykar.framework.ui.view.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.io.File;

public class WebPhotoImageView extends WebImageView {
	
	public Bitmap mBitmap;
	public Context context;
	public ZoomSize zoomSize;
	
	//constructor
	public WebPhotoImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public WebPhotoImageView(Context context, AttributeSet attSet) {
		super(context, attSet);
		this.context = context;
	}

	public WebPhotoImageView(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		mBitmap = bm;
		setAdjustWidth();
		super.setImageBitmap(mBitmap);
	}
	
	public void setImageBitMap(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int width = options.outWidth;
			int height = options.outHeight;
			options.inJustDecodeBounds = false;
			int zoomWidth = width*70/100;
			int zoomHeight = height*70/100;
			
			options.inSampleSize = width/zoomWidth;
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			super.setImageBitmap(bitmap);
		}
	}
	
	private void setAdjustWidth() {
		if(mBitmap != null)
		{
			calBitmapHeight(mBitmap);
			mBitmap = Bitmap.createScaledBitmap(mBitmap, zoomSize.width, zoomSize.height, true);
		}
	}
	
	//获取手机的高和宽
	public int getWindowHeight()
	{
		Rect frame = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int height = frame.height();// 去掉手机的标题和底部状态的高度（app所显示的高度）;
//		int headerHeight = context.getResources().getDimensionPixelSize(
//				R.dimen.header_height);// app中 顶部导航的高度
//		return height - headerHeight;
		//上面减去app导航栏的高度，放在各自的具体项目中去实现。与framework无关
		return height;
	}
	
	public int getWindowWidth()
	{
		return ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
	}
	
	public ZoomSize calBitmapHeight(Bitmap bitmap)
	{
		zoomSize = new ZoomSize();
		zoomSize.width = getWindowWidth();
		//
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();
		zoomSize.height = bitmapHeight*zoomSize.width/bitmapWidth;
		return zoomSize;
	}
	public static class ZoomSize
	{
		public int width;
		public int height;
	}
}
