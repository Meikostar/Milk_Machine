/*
	Copyright (c) 2011 Rapture In Venice

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */

package com.mykar.framework.ui.view.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mykar.framework.commonlogic.model.BeeCallback;
import com.mykar.framework.ui.view.image.WebImageCache.ImageQuality;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebImageManagerRetriever extends AsyncTask<Void, Integer, Bitmap> {
	private final static String TAG = WebImageManagerRetriever.class
			.getSimpleName();

	// cache
	private WebImageCache mCache;
	// what we're looking for
	private Context mContext;
	private String mURLString;
	private int mDiskCacheTimeoutInSeconds = -1;
	private OnWebImageLoadListener mListener;
	private ImageQuality mImageQuality;

	public void setImageQuality(ImageQuality quality){
		mImageQuality = quality;
	}
	public WebImageManagerRetriever(Context context, String urlString,
			int diskCacheTimeoutInSeconds, OnWebImageLoadListener listener) {
		mContext = context;
		mURLString = urlString;
		mDiskCacheTimeoutInSeconds = diskCacheTimeoutInSeconds;
		mListener = listener;
		mImageQuality = ImageQuality.ImageQualityMiddle;
            mCache =  WebImageCache.shareWebImageCache();
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		// check mem cache first
		Bitmap bitmap = mCache.getBitmapFromMemCache(mURLString);

		// check disk cache first
		if (bitmap == null) {
			mCache.mImageQuality = mImageQuality;
			bitmap = mCache.getBitmapFromDiskCache(mContext, mURLString,
					mDiskCacheTimeoutInSeconds);
			mCache.addBitmapToMemCache(mURLString, bitmap);
		}
		if (bitmap == null) {
			InputStream is = null;
			FlushedInputStream fis = null;

			try {
				URL url = new URL(mURLString);
				URLConnection conn = url.openConnection();

				is = conn.getInputStream();
				int length = is.available();

				BeeCallback.incrementBandwidthUsedInLastSecond(length);

				fis = new FlushedInputStream(is);
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
		        byte[] data=readStream(is);  
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//				BitmapFactory.decodeStream(fis, new Rect(), options);
				int width = options.outWidth;
				int height = options.outHeight;
				int zoomWidth = width;
				int zoomHeight = height;
				switch (mImageQuality) {
				case ImageQualityHight:
					zoomWidth = width;
					zoomHeight = height;
					break;
				case ImageQualityMiddle:
					zoomWidth = width*70/100;
					zoomHeight = height*70/100;
					break;
				case ImageQualityLow:
					zoomWidth = width*50/100;
					zoomHeight = height*50/100;
					break;
				default:
					break;
				}
				options.outHeight = zoomHeight;
				options.outWidth = zoomWidth;
				options.inJustDecodeBounds = false;
				options.inSampleSize = width/zoomWidth;
				
//				bitmap = BitmapFactory.decodeStream(fis,new Rect(),options);
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				

				// cache
				if (bitmap != null) {
					mCache.addBitmapToCache(mContext, mURLString, bitmap);
				}
			} catch (Exception ex) {
				Log.e(TAG, "Error loading image from URL " + mURLString + ": "
                                + ex.toString());
			} finally {
				try {
					is.close();
				} catch (Exception ex) {
				}
			}
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		// complete!
		if (null != mListener) {
			if (null == bitmap) {
				mListener.onWebImageError();
			} else {
				mListener.onWebImageLoad(mURLString, bitmap);
			}
		}
	}


	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;

			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);

				if (bytesSkipped == 0L) {
					int b = read();

					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}

				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public interface OnWebImageLoadListener {
		public void onWebImageLoad(String url, Bitmap bitmap);

		public void onWebImageError();
	}
	
	private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];        
        int len = 0;        
        while( (len=inStream.read(buffer)) != -1){        
            outStream.write(buffer, 0, len);        
        }        
        outStream.close();        
        inStream.close();        
        return outStream.toByteArray();        
    }  
}
