package rts.pptviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

	private PPTViewer pptViewer;
	private final int REQUEST_PERMISSION_ID = 10;
	private IFileHelper fileHelper;
	private static final int PICK_FILE_RESULT_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initialize();

		if(isStoragePermissionsAvailable()) {
			Intent intent = getIntent();
			processFile(intent);
		}
		else{
			requestPermissions();
		}
	}

	private void processFile(Intent intent) {
		String path = copyFileAndGetPath(intent);

		loadPPT(path);

		prepareAds();
	}

	private String copyFileAndGetPath(Intent intent) {
		String path = "";
		if (intent != null) {
            InputStream inputStream = getInputStream(intent);
            path = copyFile(inputStream);
        }
		return path;
	}

	@Nullable
	private InputStream getInputStream(Intent intent) {
		Uri uri = intent.getData();
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
			fileHelper.copyFile(inputStream, path);
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

	private void requestPermissions() {
		List<String> permissionsList = new ArrayList<>();
		permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),REQUEST_PERMISSION_ID);
	}

	private boolean isStoragePermissionsAvailable() {
		boolean permissionsAvailable;

		int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		permissionsAvailable = storagePermission == PackageManager.PERMISSION_GRANTED;
		return permissionsAvailable;
	}

	private void initialize() {
		fileHelper = FileHelper.getInstance();
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		pptViewer = findViewById(R.id.pptviewer);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case REQUEST_PERMISSION_ID:
				if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
					Intent intent = getIntent();
					processFile(intent);
				}
				else{
					requestPermissions();
				}
		}
	}

	private void prepareAds() {
		AdView mAdView = findViewById(R.id.adView);
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void selectFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(getString(R.string.ppt_mime_type));
		startActivityForResult(intent,PICK_FILE_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case PICK_FILE_RESULT_CODE:
				processActivityResult(resultCode, data);
				break;
			default:
				Log.d("MainActivity","Pick file failed");
				break;
		}
	}

	private void processActivityResult(int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
            if(isStoragePermissionsAvailable()) {
				processFile(data);
            }
            else{
                requestPermissions();
            }
        }
	}
}
