package com.enterprise.android.canadainfo;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enterprise.android.canadainfo.ContentsActivity.ContentItems;
import com.enterprise.android.canadainfo.LazyImageLoader.ImageCallback;

class ContentAdaptor extends ArrayAdapter<ContentItems> {

	private LazyImageLoader asyncImageLoader;

	public ContentAdaptor(Context context, ArrayList<ContentItems> listitems) {
		super(context, 0, listitems);

		asyncImageLoader = new LazyImageLoader();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final ContentItems item = getItem(position);

		final ViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.content_row, parent, false);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.desc = (TextView) convertView
					.findViewById(R.id.description);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.imageicon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(item.title);
		if (item.describtion == null || item.describtion == "null") {

			item.describtion = "Inofrmation not available";
		}

		viewHolder.desc.setText(item.describtion);
		// reset image to avoid repaint issue.
		viewHolder.icon.setImageBitmap(null);

		// Dynamicallay finding textview height
		/*viewHolder.desc
				.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

					String url = item.imagHrf;

					@Override
					public void onLayoutChange(View v, int left, int top,
							int right, int bottom, int oldLeft, int oldTop,
							int oldRight, int oldBottom) {
						// TODO Auto-generated method stub
						// getting "null" as value
						if (url != null && !url.equals("null")) {
							Bitmap cachedImage = asyncImageLoader.loadDrawable(
									url, new ImageCallback() {
										public void imageLoaded(
												Bitmap imageDrawable,
												String imageUrl) {
											viewHolder.icon
													.setImageBitmap(imageDrawable);
										}
									}, bottom);
							viewHolder.icon.setImageBitmap(cachedImage);
						}
					}
				});
*/
		String url = item.imagHrf;
		if (url != null && !url.equals("null")) {
			Bitmap cachedImage = asyncImageLoader.loadDrawable(url,
					new ImageCallback() {
						public void imageLoaded(Bitmap imageDrawable,
								String imageUrl) {

							viewHolder.icon.setImageBitmap(imageDrawable);
						}
					}, 100);
			viewHolder.icon.setImageBitmap(cachedImage);
		}

		return convertView;
	}

	// for faaster inflation list itmes
	private static class ViewHolder {
		TextView title;
		TextView desc;
		ImageView icon;
	}

}
