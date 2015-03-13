package com.enterprise.android.canadainfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class will help to download images from network asynchronously by using
 * a background thread. once the download is over the same will intimate UI
 * thread by using handler
 */
public class LazyImageLoader {
	/** caching images to smooth scrolling **/
	private HashMap<String, SoftReference<Bitmap>> mIconCache;

	public LazyImageLoader() {
		mIconCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public Bitmap loadDrawable(final String imageUrl,
			final ImageCallback imageCallback, final int height) {
		if (mIconCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = mIconCache.get(imageUrl);
			Bitmap drawable = softReference.get();
			if (drawable != null) {

				return drawable;
			}
		}
		/** Update downloaded image in UI **/
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		/** Non UI (background) thread to download Images. **/
		new Thread() {
			@Override
			public void run() {

				Bitmap drawable = loadImageFromUrl(imageUrl, height);

				if (drawable != null) {
					mIconCache.put(imageUrl,
							new SoftReference<Bitmap>(drawable));
				}

				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	/** Downloads image from given URL **/
	public static Bitmap loadImageFromUrl(String url, int height) {

		InputStream inputStream;
		try {
			inputStream = new URL(url).openStream();
		} catch (IOException e) {
			Log.d(LazyImageLoader.class.getName(), "err ***" + e.toString()
					+ url);
			return null;
		}

		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		if (bitmap != null) {
			bitmap = getResizedBitmap(bitmap, height, 100);
		}
		return bitmap;
	}

	/** decodes image and scales it to reduce memory consumption **/
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	/** simple callback to intimate UI thread **/
	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
