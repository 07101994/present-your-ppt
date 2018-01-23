package rts.pptviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.arrowsappstudios.pptviewer.helpers.FileHelper;
import com.arrowsappstudios.pptviewer.helpers.IFileHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itsrts.pptviewer.PPTViewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	final int HEADER_HIDE_ANIM_DURATION = 500;
	Toolbar toolbar;
	PPTViewer pptViewer;
	final int REQUEST_PERMISSION_ID = 10;
	IFileHelper fileHelper;
	private static final int PICK_FILE_RESULT_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Initialize();

		if(isStoragePermissionsAvailable()) {
			String path = null;
			Intent i = getIntent();
			if (i != null) {
				InputStream inputStream = getInputStream(i);
				path = copyFile(inputStream);
			}

			loadPPT(path);

			prepareAds();
		}
		else{
			RequestPermissions();
		}
	}

	@Nullable
	private InputStream getInputStream(Intent i) {
		Uri uri = i.getData();
		InputStream inputStream = null;
		if (uri != null) {
			try {
				inputStream = this.getContentResolver().openInputStream(uri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			inputStream = getResources().openRawResource(R.raw.presentyourppt);
		}
		return inputStream;
	}

	@NonNull
	private String copyFile(InputStream inputStream) {
		String path;
		path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.pptFileName);
		try {
			fileHelper.CopyFile(inputStream, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	private void loadPPT(String path) {
		pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
				.setSettings_img(R.drawable.settings)
				.setZoomin_img(R.drawable.zoomin)
				.setZoomout_img(R.drawable.zoomout);
		pptViewer.loadPPT(this, path);
	}

	private void RequestPermissions() {
		List<String> permissionsList = new ArrayList<>();
		permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),REQUEST_PERMISSION_ID);
	}

	private boolean isStoragePermissionsAvailable() {
		boolean permissionsAvailable = false;

		int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		permissionsAvailable = storagePermission == PackageManager.PERMISSION_GRANTED;
		return permissionsAvailable;
	}

	private void Initialize() {
		fileHelper = FileHelper.getInstance();
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		pptViewer = (PPTViewer) findViewById(R.id.pptviewer);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case REQUEST_PERMISSION_ID:
				if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
					String path = null;
					Intent i = getIntent();
					if (i != null) {
						InputStream inputStream = getInputStream(i);
						path = copyFile(inputStream);
					}

					loadPPT(path);

					prepareAds();
				}
				else{
					RequestPermissions();
				}
		}
	}

	private void prepareAds() {
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.open_file:
				selectFile();
				return true;
			case R.id.fullscreen:
				hideToolbarWithAnimation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void hideToolbarWithAnimation() {
		toolbar.animate()
                .translationY(0)
                .alpha(1).setDuration(HEADER_HIDE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator());
	}

	private void selectFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/vnd.ms-powerpoint");
		startActivityForResult(intent,PICK_FILE_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case PICK_FILE_RESULT_CODE:
				if(resultCode==RESULT_OK){
					if(isStoragePermissionsAvailable()) {
						String path = null;
						if (data != null) {
							InputStream inputStream = getInputStream(data);
							path = copyFile(inputStream);
						}

						loadPPT(path);

						prepareAds();
					}
					else{
						RequestPermissions();
					}
				}
				break;
		}
	}
}
