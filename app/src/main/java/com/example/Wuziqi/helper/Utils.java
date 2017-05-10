package com.example.Wuziqi.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.Wuziqi.MainActivity;
import com.example.Wuziqi.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class Utils {

	private Context _context;

	private MainActivity mainActivity;

	// constructor
	public Utils(Context context) {
		this._context = context;
	}

	/*
	 * Reading file paths from SDCard
	 */
	public ArrayList<String> getFilePaths() {
		ArrayList<String> filePaths = new ArrayList<String>();

		File directory = new File(
				android.os.Environment.getExternalStorageDirectory()
						+ File.separator + AppConstant.PHOTO_ALBUM);

		// check for directory
		if (directory.isDirectory()) {
			// getting list of file paths
			File[] listFiles = directory.listFiles();

			// Check for count
			if (listFiles.length > 0) {

				// loop through all files
				for (int i = 0; i < listFiles.length; i++) {

					// get file path
					String filePath = listFiles[i].getAbsolutePath();

					// check for supported file extension
					if (IsSupportedFile(filePath)) {
						// Add image path to array list
						filePaths.add(filePath);
					}
				}
			} else {
				// image directory is empty
				Toast.makeText(
						_context,
						AppConstant.PHOTO_ALBUM
								+ " is empty. Please load some images in it !",
						Toast.LENGTH_LONG).show();
			}

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(_context);
			alert.setTitle("Error!");
			alert.setMessage(AppConstant.PHOTO_ALBUM
					+ " directory path is not valid! Please set the image directory name AppConstant.java class");
			alert.setPositiveButton("OK", null);
			alert.show();
		}

		return filePaths;
	}

	public String getFileNames(int position){
		String[] FileNames = new String[100];

		String Path = Environment.getExternalStorageDirectory().toString() + "/Wuziqi/Screenshot/";

		File file = new File(Path);
		File[] FileArray = file.listFiles();

		return FileArray[position].getName();
	}


	/*
	 * Check supported file extensions
	 * @returns boolean
	 */
	private boolean IsSupportedFile(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (AppConstant.FILE_EXTN
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	/*
	 * getting screen width
	 */
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}

	public static void soundEffect(int index, Context context){
		SoundPool sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		if (index == 1){
			int music = sp.load(context, R.raw.btn_sound, 1);
			sp.play(music, 1, 1, 0, 0, 1);
		}else{
			int music = sp.load(context, R.raw.piece_sound, 1);
			sp.play(music, 1, 1, 0, 0, 1);
		}
	}
}
