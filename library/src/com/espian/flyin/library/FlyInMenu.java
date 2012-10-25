package com.espian.flyin.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.*;
import com.nineoldandroids.animation.ObjectAnimator;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class FlyInMenu extends LinearLayout implements View.OnTouchListener, AdapterView.OnItemClickListener {

	public static final int FLY_IN_WITH_ACTIVITY = 0;
	@Deprecated
	public static final int FLY_IN_OVER_ACTIVITY = 1;

	private ListView mListView;
	private LinearLayout mMenuHolder;
	private ViewStub mCustomStub;
	private View mCustomView;
	private View mWrappedSearchView;
	private boolean hasSearchView = false;
	public boolean isMenuVisible = false;
	private int flyType;
	private int animStartFlyType;
	private Activity mAct;
	private float mFlyInWidth;
	private View mActivityContentInstance;

	private final Interpolator decel = AnimationUtils.loadInterpolator(getContext(),
			android.R.anim.decelerate_interpolator);

	private OnFlyInItemClickListener callback;

	private static ArrayList<FlyInMenuItem> menuItems;

	public FlyInMenu(Context context) {
		super(context);
		mAct = (Activity) context;
		load();
	}

	public FlyInMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAct = (Activity) context;
		load();
	}

	public void setType(int type) {
		flyType = type;
	}

	private void load() {

		if (isInEditMode())
			return;

		inflateLayout();
		initUi();

	}

	private void inflateLayout() {

		try {
			LayoutInflater.from(getContext()).inflate(R.layout.fly_menu, this,
					true);
		} catch (Exception e) {

		}

	}

	private void initUi() {

		mListView = (ListView) findViewById(R.id.fly_listview);
		mCustomStub = (ViewStub) findViewById(R.id.fly_custom);
		mMenuHolder = (LinearLayout) findViewById(R.id.fly_menu_holder);
		mFlyInWidth = mAct.getResources().getDimension(R.dimen.rbm_menu_width);

	}

	public void setOnFlyInItemClickListener(OnFlyInItemClickListener callback) {
		this.callback = callback;
	}

	/**
	 * Enabled the SearchView at the top of the Fly-in menu, providing the SDK
	 * is API level 11 (Honeycomb) or above. The SearchView can be accessed with
	 * {@link #getSearchView()}.
	 */
	public void enableSearchView() {
		if (Build.VERSION.SDK_INT >= 11) {
			SearchView s = new SearchView(getContext());
			s.setIconifiedByDefault(false);
			s.setSubmitButtonEnabled(true);
			(mWrappedSearchView = s).setId(R.id.fly_searchview);
			mMenuHolder.removeView(findViewById(R.id.fly_searchstub));
			mMenuHolder.addView(mWrappedSearchView, 0);
			hasSearchView = true;
		}
	}

	/**
	 * Fetches the SearchView associated with the Fly-in menu. Note that calling
	 * this on an API level pre-11 (Honeycomb) will crash, so your logic must
	 * include a check on the API level if you target such platforms. The
	 * SearchView must first be initialised using {@link #enableSearchView}.
	 *
	 * @return the SearchView instance, or null if it hasn't been initialised
	 */
	public SearchView getSearchView() {
		if (!hasSearchView)
			return null;
		return (SearchView) mWrappedSearchView;
	}

	/**
	 * Fetch the custom view at the bottom of the fly-in menu.
	 *
	 * @return the custom view, or null
	 */
	public View getCustomView() {
		return mCustomView;
	}

	/**
	 * Set the custom view at the bottom of the fly-in menu, from a resource
	 *
	 * @param rid layout resource id of the view to inflate
	 */
	public void setCustomView(int rid) {
		mCustomStub.setLayoutResource(rid);
		mCustomView = mCustomStub.inflate();
		mCustomView.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
		requestLayout();
	}

	/**
	 * Set the custom view at the bottom of the fly-in menu
	 *
	 * @param view the view to set
	 */
	public void setCustomView(View view) {
		mMenuHolder.removeView(mCustomStub);
		mMenuHolder.addView(mCustomView = view);
		mCustomView.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
		requestLayout();
	}

	/**
	 * Set the menu items associated with the fly-in menu
	 *
	 * @param menu resource id of the menu to be inflated
	 */
	public void setMenuItems(int menu) {
		setMenuItems(menu, -1);
	}

	public void setMenuItems(int menu, int resourceId) {

		parseXml(menu);
		if (menuItems != null && menuItems.size() > 0) {
			mListView.setAdapter(new Adapter());
			mListView.setOnItemClickListener(this);
		}

	}

	/**
	 * Sets the background of the fly-in menu
	 */
	public void setBackgroundResource(int resource) {
		mMenuHolder.setBackgroundResource(resource);
	}

	private int getInteger(int id) {
		return getResources().getInteger(id);
	}

	public void showMenu() {

		if (flyType > 1)
			flyType = 0;

		if (mActivityContentInstance == null) {
			mActivityContentInstance = ((ViewGroup) mAct.findViewById(android.R.id.content)).getChildAt(1);
		}

		ObjectAnimator activity = ObjectAnimator.ofFloat(mActivityContentInstance, "X", mFlyInWidth);
		activity.setInterpolator(decel);

		animStartFlyType = flyType;
		if (flyType == FLY_IN_WITH_ACTIVITY) {

			activity.setDuration(300).start();

		} else {

			//flyIn.setInterpolator(decel);
			//flyIn.setDuration(300).start();

		}

		isMenuVisible = true;

	}

	public void hideMenu() {


		if (mActivityContentInstance == null) {
			mActivityContentInstance = ((ViewGroup) mAct.findViewById(android.R.id.content)).getChildAt(1);
		}

		ObjectAnimator activity = ObjectAnimator.ofFloat(mActivityContentInstance, "X", 0);
		activity.setInterpolator(decel);

		if (animStartFlyType == FLY_IN_WITH_ACTIVITY) {

			activity.setDuration(300).start();

		}

		isMenuVisible = false;
		mActivityContentInstance.offsetLeftAndRight(0);

	}

	/**
	 * Toggle the menu visibility: show it if it is hidden, and hide if it is
	 * shown.
	 */
	public void toggleMenu() {
		if (isMenuVisible)
			hideMenu();
		else
			showMenu();

	}

	private void parseXml(int menu) {

		menuItems = new ArrayList<FlyInMenuItem>();

		try {
			XmlResourceParser xpp = getResources().getXml(menu);

			xpp.next();
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {

					String elemName = xpp.getName();

					if (elemName.equals("item")) {

						String textId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"title");
						String iconId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"icon");
						String resId = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"id");
						String enabled = xpp.getAttributeValue(
								"http://schemas.android.com/apk/res/android",
								"enabled");

						FlyInMenuItem item = new FlyInMenuItem();
						item.setItemId(Integer.valueOf(resId.replace("@", "")));
						item.setTitle(resourceIdToString(textId));
						if (iconId != null)
							item.setIcon(Integer.valueOf(iconId.replace("@", "")));
						else
							item.setIcon(-1);
						// item.setEnabled(Boolean.parseBoolean(enabled));

						menuItems.add(item);

					}

				}

				eventType = xpp.next();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String resourceIdToString(String text) {

		if (!text.contains("@")) {
			return text;
		} else {

			String id = text.replace("@", "");
			int rid = Integer.valueOf(id);
			return getResources().getString(rid);

		}

	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		if (ss.bShowMenu)
			showMenu();
		else
			hideMenu();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.bShowMenu = isMenuVisible;

		return ss;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if (callback != null) {
			boolean result = callback.onFlyInItemClick(menuItems.get(i), i);
			if (result == true)
				hideMenu();
		}
	}

	static class SavedState extends BaseSavedState {
		boolean bShowMenu;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			bShowMenu = (in.readInt() == 1);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(bShowMenu ? 1 : 0);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	private class Adapter extends BaseAdapter {

		private LayoutInflater inflater;

		public Adapter() {
			inflater = LayoutInflater.from(getContext());
		}

		@Override
		public int getCount() {
			return menuItems.size();
		}

		@Override
		public Object getItem(int position) {
			return menuItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null || convertView instanceof TextView)
				convertView = inflater.inflate(R.layout.fly_item_2, null);

			ImageView icon = (ImageView) convertView
					.findViewById(R.id.rbm_item_icon);
			TextView text = (TextView) convertView
					.findViewById(R.id.rbm_item_text);
			FlyInMenuItem item = menuItems.get(position);
			text.setText(item.getTitle());
			if (item.getIconId() > 0)
				icon.setImageResource(item.getIconId());
			else
				icon.setVisibility(View.INVISIBLE);
			convertView.setEnabled(item.isEnabled());

			return convertView;

		}

	}

}
