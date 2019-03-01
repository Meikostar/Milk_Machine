/*
 * ImageProgressTouchView.java
 * AppWeChatLife
 * 
 * Description: 
 * 
 * Created by graysonsun on 2013-1-10.  
 * Copyright (c) 2012 Tencent. All rights reserved.
 *
 * Change Logs:
 * 1.2013-1-10	graysonsun		create this file
 *
 */
package com.mykar.framework.ui.view.image;



public class ImageProgressTouchView
{
	
}
/**
 * @author graysonsun
 *
 */
//public class ImageProgressTouchView extends RelativeLayout implements IImageAccessDelegate
//{
//
//	protected ViewGroup mProgressLayout;
//	protected ProgressBar mProgressBar;
//	protected TextView mProgressTextView;
//	protected ImageView mProgressImageView;
//	protected CropImageView mCropImageView;
//	
//	protected String mImageUrl;
//	protected Bitmap mBitmap;
//	
//	protected boolean mIsUrlImage = false;
//	
//	protected boolean mIsGettingImage = false;
//	protected boolean mEnableGif = true; //是否允许gif图片播放
//	
//	protected int mMaxBitmapWidth;
//	protected int mMaxBitmapHeight;
//
//	/**
//	 * @param context
//	 * @param attrs
//	 * @param defStyle
//	 */
//    public ImageProgressTouchView(Context context, AttributeSet attrs,
//            int defStyle)
//    {
//	    super(context, attrs, defStyle);
//	    init();
//    }
//
//	/**
//	 * @param context
//	 */
//    public ImageProgressTouchView(Context context)
//    {
//	    super(context);
//	    init();
//    }
//
//	/**
//	 * @param context
//	 * @param attrs
//	 */
//    public ImageProgressTouchView(Context context, AttributeSet attrs)
//    {
//	    super(context, attrs);
//	    init();
//    }
//    
//    protected void init()
//    {
//    	mMaxBitmapWidth = ApplicationHolder.getmMaxBitmapWidth();
//    	mMaxBitmapHeight = ApplicationHolder.getmMaxBitmapHeight();
//    }
//    
//    public void setImageUrl(String url, boolean start)
//    {
//    	mImageUrl = url;
//    	if(start)
//    	{
//    		startGetImage();
//    	}
//    	
//    }
//    public void setBitmap(Bitmap bitmap)
//    {
//    	mIsUrlImage = false;
//    	mBitmap = bitmap;
//    	showImage(bitmap);
//    }
//    
//    public Bitmap getBitmap()
//    {
//    	return mBitmap;
//    }
//    
//    public void startGetImage()
//    {
//    	if(mImageUrl==null)
//    		return;
//    	ImageManager.GetImageAccess().getImageWithUrl(mImageUrl, true, this);
//    	mIsGettingImage = true;
//    }
//    
//    public void stopGetImage()
//    {
//    	if(mImageUrl!=null)
//    	{
//    		ImageManager.GetImageAccess().cancelGetImage(mImageUrl, this, true);
//    	}
//    	mIsGettingImage = false;
//    }
//    
//	/**
//	 * @return the mEnableGIF
//	 */
//	public boolean ismEnableGif()
//	{
//		return mEnableGif;
//	}
//
//	/**
//	 * @param mEnableGIF the mEnableGIF to set
//	 */
//	public void setmEnableGif(boolean mEnableGIF)
//	{
//		this.mEnableGif = mEnableGIF;
//	}
//
//    /* (non-Javadoc)
//     * @see android.view.View#onFinishInflate()
//     */
//    @Override
//    protected void onFinishInflate()
//    {
//        super.onFinishInflate();
//        mProgressLayout = (ViewGroup)findViewById(R.id.id_imageviewer_progresslayout);
//        mProgressBar = (ProgressBar)findViewById(R.id.id_imageviewer_progressbar);
//        mProgressTextView = (TextView)findViewById(R.id.id_imageviewer_progresstext);
//        mCropImageView = (CropImageView)findViewById(R.id.id_imageviewer_cropimage);
//        mProgressImageView = (ImageView)findViewById(R.id.id_imageviewer_progressimage);
//    }
//    
//    
//    protected void showGetImageFail()
//    {
//    	mProgressBar.setVisibility(View.INVISIBLE);
//    	mProgressTextView.setVisibility(View.INVISIBLE);
//    	mProgressImageView.setVisibility(View.VISIBLE);
//    	mProgressImageView.setImageResource(R.drawable.imageviewer_loading_failure);
//    	mProgressLayout.setVisibility(View.VISIBLE);
//    	
//    	mCropImageView.setVisibility(View.INVISIBLE);
//    }
//    
//    protected void showGetImageProgress(int percentage)
//    {
//    	mProgressBar.setVisibility(View.VISIBLE);
//    	mProgressBar.setProgress(percentage);
//    	mProgressTextView.setVisibility(View.VISIBLE);
//    	//
//    	mProgressImageView.setVisibility(View.VISIBLE);
//    	mProgressImageView.setImageResource(R.drawable.imageviewer_loading);
//    	mProgressLayout.setVisibility(View.VISIBLE);
//    	
//    	mCropImageView.setVisibility(View.INVISIBLE);
//    }
//    
//    protected void showImage(Bitmap bitmap)
//    {
//    	mProgressLayout.setVisibility(View.GONE);
//        mCropImageView.setVisibility(View.VISIBLE);
//    
//        //获取本地缓存
//		String path = ImageCachePathCaculator.getUrlLocalPath(mImageUrl);
//		byte[] bytes = FileUtil.readByteFromPath(path);
//		
//		if (bytes == null || !mEnableGif)
//        {
//			bitmap = processMaxBitmap(bitmap);
//			mCropImageView.setImageBitmapResetBase(bitmap, true);
//			return;
//        }
//		
//		//检查图片类型
//		ByteBuffer buffer = ByteBuffer.wrap(bytes);
//		eIMAGE_TYPE type = ImageTypeChecker.checkImageDataType(buffer);
//
//		if (type == eIMAGE_TYPE.kGIF_Format)
//		{
//			mCropImageView.setGifImage(bytes);
//		}
//		else
//		{
//			bitmap = processMaxBitmap(bitmap);
//			mCropImageView.setImageBitmapResetBase(bitmap, true);
//		}
//    }
//    
//    /**
//     * 防止显示过大的图片，超出系统支持
//     * @param bitmap
//     * @return
//     */
//    private Bitmap processMaxBitmap(Bitmap bitmap)
//    {
//    	if(mMaxBitmapWidth > 0)
//    	{
//    		int width = bitmap.getWidth();
//    		int height = bitmap.getHeight();
//    		float ratio = ImageUtil.checkMaxWidthByCanvas(mMaxBitmapWidth, mMaxBitmapHeight, width, height);
//    		if(ratio > 0 && ratio <1)
//    		{
//	    		width = (int) (ratio * width);
//	    		height = (int) (ratio * height);
//	    		bitmap = ImageUtil.getScaledBitmap(bitmap, width, height);
//    		}
//    	}
//    	return bitmap;
//    }
//
//	/* (non-Javadoc)
//	 * @see com.tencent.ibg.commonlogic.imagemanager.IImageAccessDelegate#onGetImageFail(java.lang.String, int)
//	 */
//    @Override
//    public void onGetImageFail(String url, int errorCode)
//    {
//    	mIsGettingImage = false;
//    	mIsUrlImage = false;
//    	
//    	if (mCropImageView == null)
//        {
//	        return;
//        }
//    	
//    	showGetImageFail();
//    }
//
//
//	/* (non-Javadoc)
//	 * @see com.tencent.ibg.commonlogic.imagemanager.IImageAccessDelegate#onGetImageSucc(java.lang.String, android.graphics.Bitmap)
//	 */
//    @Override
//    public void onGetImageSucc(String url, Bitmap image)
//    {
//    	mIsGettingImage = false;
//    	mIsUrlImage = true;
//    	mBitmap = image;
//
//    	if (mCropImageView == null)
//        {
//	        return;
//        }
//    	
//    	mBitmap = processMaxBitmap(mBitmap);
//    	showImage(image);
//    }
//
//
//	/* (non-Javadoc)
//	 * @see com.tencent.ibg.commonlogic.imagemanager.IImageAccessDelegate#onGetImageProgress(java.lang.String, long, long)
//	 */
//    @Override
//    public void onGetImageProgress(String url, long total, long downloaded)
//    {
//    	if (mCropImageView == null)
//        {
//	        return;
//        }
//    	
//    	int progress = 0;
//    	if(total!=0 && downloaded>0)
//    		progress = (int)(downloaded*100/total);
//    	showGetImageProgress(progress);
//    }
//
//
//	/* (non-Javadoc)
//	 * @see com.tencent.ibg.commonlogic.imagemanager.IImageAccessDelegate#onGetUpdatedImage(java.lang.String, android.graphics.Bitmap)
//	 */
//    @Override
//    public void onGetUpdatedImage(String url, Bitmap image)
//    {
//    	mIsGettingImage = false;
//    	
//    	if (mCropImageView == null)
//        {
//	        return;
//        }
//    	
//    	showImage(image);
//    }
//    
//	/* (non-Javadoc)
//	 * @see android.view.View#setOnClickListener(android.view.View.OnClickListener)
//	 */
//	@Override
//	public void setOnClickListener(OnClickListener l)
//	{
//		mCropImageView.setOnClickListener(l);
//	}
//    
//	/**
//	 * 释放图片，退出时不主动释放可能会有内存泄漏问题
//	 */
//	public void recycleUrlImage()
//	{
//		if (mIsGettingImage)
//        {
//			stopGetImage();
//        }
//		
//		if (mCropImageView != null)
//		{
//			mCropImageView.destroy();
//			mCropImageView = null;
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#finalize()
//	 */
//	@Override
//	protected void finalize() throws Throwable
//	{
//		if (mIsGettingImage)
//        {
//			stopGetImage();
//        }
//		
//		if (mIsUrlImage && mCropImageView != null)
//        {
//	        mCropImageView.needRecycle();
//        }
//		mBitmap = null;
//		mCropImageView = null;
//	    super.finalize();
//	}
//}
