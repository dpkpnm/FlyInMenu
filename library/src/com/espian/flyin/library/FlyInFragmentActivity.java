package com.espian.flyin.library;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;

/**
 * Wrap of the Android support library's FragmentActivity, supplying it with the
 * ability to use fly-in menus. If you are only targeting Android 3.0 and above,
 * you can use the standard {@link Activity} class by replacing
 * "extends FragmentActivity" with "extends Activity".
 *
 * @author Alex Curran
 */
public abstract class FlyInFragmentActivity extends FragmentActivity implements View.OnTouchListener {

	private FlyInMenu flyMenuView = null;
	private ViewGroup activityHost = null;
	private boolean hasFlyMenu = false;
	private float initialTouchX = -1;
	private float prevTouchX = -1;
	private int offsetX, offsetY;
	private VelocityTracker vTracker;
	private float mMinimumFlingVelocity;

	@Override
	public void onCreate(Bundle saved) {

		super.onCreate(saved);
		flyMenuView = new FlyInMenu(this);
		mMinimumFlingVelocity = ViewConfiguration.get(this).getScaledMinimumFlingVelocity();

	}

	/**
	 * Retrieves the current status bar height.
	 * Note: Needs to be called from a Runnable because the layout is
	 * not initialized in onCreate() yet.
	 *
	 * @return the status bar height
	 */
	private int getStatusBarOffset() {
		Rect rect = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
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
	@Deprecated
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
		FrameLayout superFrame = (FrameLayout) findViewById(android.R.id.content);
		superFrame.addView(flyMenuView, 0);
		activityHost = (ViewGroup) superFrame.getChildAt(1);
		activityHost.setOnTouchListener(FlyInFragmentActivity.this);
		activityHost.setBackgroundColor(Color.BLACK);
		//TODO extend this to work below API 11
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
		if (flyMenuView.isMenuVisible) {
			flyMenuView.toggleMenu();
		} else
			super.onBackPressed();
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Log.d("Slidey", motionEvent.toString());
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			// User has pressed down on OutsideView, record this as the
			// initial X position
			vTracker = VelocityTracker.obtain();
			vTracker.addMovement(motionEvent);
			initialTouchX = motionEvent.getRawX();
			prevTouchX = -1;
		} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
			vTracker.addMovement(motionEvent);
			// User has dragged. We get the delta of X and offset
			// by that amount
			if (prevTouchX < 0) {
				prevTouchX = motionEvent.getRawX();
				return true;
			}
			int xDelta = ((Number) (motionEvent.getRawX() - prevTouchX)).intValue();
			int xDeltaFromInit = ((Number) (motionEvent.getRawX() - initialTouchX)).intValue();
			if (flyMenuView.isMenuVisible) {
				if (Math.abs(xDelta) > flyMenuView.getWidth() || xDeltaFromInit > 0) {
					/* This prevents the user a) dragging further than the
					FIM width or dragging the FIM "out" */
				} else {
					activityHost.offsetLeftAndRight(xDelta);
					activityHost.invalidate();
				}
			} else {
				if (Math.abs(xDelta) > flyMenuView.getWidth() || xDeltaFromInit < 0) {
				/* This prevents the user a) dragging further than the
				FIM width or dragging the FIM "out" */
				} else {
					activityHost.offsetLeftAndRight(xDelta);
					activityHost.invalidate();
				}
			}
			prevTouchX = motionEvent.getRawX();
		} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			// User has finished dragging. If this is less than half
			// the menu's width, finish by closing, else flick open
			float xEndPos = motionEvent.getRawX();
			float xDelta = xEndPos - initialTouchX;
			vTracker.addMovement(motionEvent);
			vTracker.computeCurrentVelocity(1);
			float velX = vTracker.getXVelocity();

			// If the touch delta is greater than half drawer width or
			// a fast flick, open the drawer
			if (xDelta > (flyMenuView.getWidth() / 2) || velX > +0.35f) {
				flyMenuView.showMenu();
			} else flyMenuView.hideMenu();

		}
		return true;
	}
}
