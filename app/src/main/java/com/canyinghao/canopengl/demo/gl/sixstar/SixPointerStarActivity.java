package com.canyinghao.canopengl.demo.gl.sixstar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SixPointerStarActivity extends AppCompatActivity {

	SixSurfaceView mview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		mview = new SixSurfaceView(this);

		setContentView(mview);


	}



	@Override
	protected void onResume() {
		super.onResume();
		mview.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mview.onPause();
	}
	
	

}
