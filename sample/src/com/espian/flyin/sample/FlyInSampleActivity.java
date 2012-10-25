package com.espian.flyin.sample;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.espian.flyin.library.FlyInFragmentActivity;
import com.espian.flyin.library.FlyInMenuItem;
import com.espian.flyin.library.OnFlyInItemClickListener;

public class FlyInSampleActivity extends FlyInFragmentActivity implements OnFlyInItemClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		loadFlyInMenu(R.menu.ribbon_menu);
		Button b = new Button(this);
		b.setText("Click me!");
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(FlyInSampleActivity.this, "Custom view clicked",
						Toast.LENGTH_SHORT).show();
			}

		});
		getFlyInMenu().setCustomView(b);
		//getFlyInMenu().enableSearchView();
		getFlyInMenu().setOnFlyInItemClickListener(this);
	}

	@Override
	public boolean onFlyInItemClick(FlyInMenuItem menuItem, int position) {
		Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
		return position != 1;
	}

	public void showBackup(View v) {
		getFlyInMenu().toggleMenu();
	}

}