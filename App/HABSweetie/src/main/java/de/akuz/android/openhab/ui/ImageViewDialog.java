package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.ui.custom.TouchImageView;
import de.akuz.android.openhab.util.ImageLoadHelper;
import de.akuz.android.openhab.util.ImageLoadHelper.ImageLoadListener;

public class ImageViewDialog extends SherlockDialogFragment implements
		ImageLoadListener {

	public static String IMAGE_URL_ARGUMENT = "image.url";

	private String imageUrl;

	private ProgressBar progressBar;
	private TouchImageView imageView;

	@Inject
	ImageLoadHelper imageLoader;

	public static ImageViewDialog create(String imageUrl) {
		Bundle arguments = new Bundle();
		arguments.putString(IMAGE_URL_ARGUMENT, imageUrl);
		ImageViewDialog dialog = new ImageViewDialog();
		dialog.setArguments(arguments);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		imageUrl = arguments.getString(IMAGE_URL_ARGUMENT);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View layout = LayoutInflater.from(getActivity()).inflate(
				R.layout.image_dialog, null, false);

		imageView = (TouchImageView) layout.findViewById(R.id.touchImageView);
		progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

		imageView.setVisibility(View.GONE);
		progressBar.setIndeterminate(true);

		imageLoader.loadImageAsync(imageUrl, this);
		builder.setView(layout);
		
		AlertDialog dialog = builder.create();
		Drawable d = new ColorDrawable(getResources().getColor(R.color.dark_grey));
		d.setAlpha(130);
		dialog.getWindow().setBackgroundDrawable(d);
		return dialog;
	}

	@Override
	public void imageLoaded(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);

	}

	@Override
	public void errorOccured(Throwable t) {
		// TODO Auto-generated method stub

	}

}
