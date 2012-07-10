package com.espian.flyin.library;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

/**
 * Wrap of the Android support library's FragmentActivity, supplying it with the
 * ability to use fly-in menus. If you are only targeting Android 3.0 and above,
 * you can use the standard {@link Activity} class by replacing
 * "extends FragmentActivity" with "extends Activity".
 *
 * @author Alex Curran
 */
public abstract class FlyInFragmentActivity extends FragmentActivity implements
		OnFlyInItemClickListener {

	private FlyInMenu flyMenuView = null;
	private boolean hasFlyMenu = false;

	@Override
	public void onCreate(Bundle saved) {

		super.onCreate(saved);
		int statusOffset = getResources().getDimensionPixelSize(
				R.dimen.status_bar_offset);
		flyMenuView = new FlyInMenu(this);
		flyMenuView.setPadding(0, statusOffset, 0, 0);
		flyMenuView.setType(FlyInMenu.FLY_IN_WITH_ACTIVITY);

	}

	/**
	 * Retrieves the {@link FlyInMenu} associated with this Activity
	 *
	 * @return the FlyInMenu, or null if there isn't one
	 */
	protected FlyInMenu getFlyInMenu() {
		return flyMenuView;
	}

	/**
	 * Set the fly-in animation type of the menu. This can be either
	 * {@link FlyInMenu#FLY_IN_WITH_ACTIVITY}, where the fly-in menu pushes the
	 * Activity right; or {@link FlyInMenu#FLY_IN_OVER_ACTIVITY}, where the
	 * fly-in menu slides over the Activity.
	 *
	 * @param type Either {@link FlyInMenu#FLY_IN_WITH_ACTIVITY} or
	 *             {@link FlyInMenu#FLY_IN_OVER_ACTIVITY}
	 */
	protected void setFlyInType(int type) {
		flyMenuView.setType(type);
	}

	/**
	 * Inflate and load a menu resource id for the fly-in menu. This will set
	 * {@link android.app.ActionBar#setDisplayHomeAsUpEnabled(boolean)
	 * ActionBar.setDisplayHomeAsUpEnabled(true)} as well providing the device
	 * is Honeycomb or above. Bear in mind, devices below Honeycomb or themes
	 * with no ActionBar (e.g. Theme.Holo.NoActionBar) will require some way to
	 * manually open the fly-in menu.
	 *
	 * @param menuId menu resource to load
	 */
	protected void loadFlyInMenu(int menuId) {
		flyMenuView.setMenuItems(menuId);
		flyMenuView.setOnFlyInItemClickListener(this);
		((ViewGroup) getWindow().getDecorView()).addView(flyMenuView);
		hasFlyMenu = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			try {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (NullPointerException npe) {
				throw new RuntimeException(
						"SherlockActivity has no Support ActionBar. Ensure you are not using "
								+ "a NoActionBarTheme");
			} catch (Exception ex) {
				Log.w(FlyInFragmentActivity.class.getName(), ex.getClass()
						.getName() + " : Likely running an API lower than 11");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home && hasFlyMenu) {
			this.toggleFlyIn();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Show the {@link FlyInMenu} if it is hidden, and hide it if it is shown.
	 */
	protected void toggleFlyIn() {
		if (flyMenuView != null)
			flyMenuView.toggleMenu();
	}

	@Override
	public void onBackPressed() {
		if (flyMenuView.isMenuVisible()) {
			flyMenuView.toggleMenu();
		} else
			super.onBackPressed();
	}

}
