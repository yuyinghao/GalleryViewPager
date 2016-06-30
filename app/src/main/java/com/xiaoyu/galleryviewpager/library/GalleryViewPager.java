package com.xiaoyu.galleryviewpager.library;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.xiaoyu.galleryviewpager.library.adapter.AbstractAdapter;
import com.xiaoyu.galleryviewpager.library.interfaces.OnCurrentPageContentChangeListener;
import com.xiaoyu.galleryviewpager.library.interfaces.OnCurrentPageTextChangeListener;
import com.xiaoyu.galleryviewpager.library.interfaces.OnPagerFocusChangeListener;

public class GalleryViewPager extends ViewPager {

	private static final int OFFSCREENPAGELIMIT = 2;
	private static final int ANIMATIONDURATION = 300;

	private OnCurrentPageTextChangeListener pageTextChangeListener;
	private OnCurrentPageContentChangeListener pageContentChangeListener;
	private OnPagerFocusChangeListener pagerFocusChangeListener;

	private Runnable myRun = new Runnable() {

		@Override
		public void run() {
			prepare();
		}
	};

	public GalleryViewPager(Context context) {
		super(context);
		initView();
	}

	public GalleryViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {

		setClipChildren(false);
		setOffscreenPageLimit(OFFSCREENPAGELIMIT);
		changeViewPageScroller();
		setPageTransformer(true, new GalleryPageTransformer());
		setOnPageChangeListener(new GalleryPageChangeListener());
		post(myRun);
	}

	private void changeViewPageScroller() {
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(),
					new LinearInterpolator());
			scroller.setmDuration(ANIMATIONDURATION);
			mField.set(this, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepare() {
		int first = 1;// currentPosition - 1;
		int end = 3;// currentPosition + 1;
		View demoView;
		if (first >= 0) {
			demoView = getChildAt(first);
			if (demoView != null) {
				demoView.setScaleX(0.6f);
				demoView.setScaleY(0.6f);
				demoView.setAlpha(0.2f);
			}
		}
		if (end < getChildCount()) {
			demoView = getChildAt(end);
			if (demoView != null) {
				demoView.setScaleX(0.6f);
				demoView.setScaleY(0.6f);
				demoView.setAlpha(0.2f);
			}
		}

	}

	private int mDirection;
	@Override
	public View focusSearch(int direction) {
		mDirection = direction;
		if (direction == FOCUS_LEFT || direction == FOCUS_RIGHT) {
			return this;
		}

		return super.focusSearch(direction);
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if(pagerFocusChangeListener != null){
			pagerFocusChangeListener.onPagerFocusChange(this, mDirection, gainFocus);
		}
	}

	public void setOnCurrentPageTextChangeListener(
			OnCurrentPageTextChangeListener pageTextChangeListener) {
		this.pageTextChangeListener = pageTextChangeListener;
	}

	public void setOnCurrentPageContetChangeListener(
			OnCurrentPageContentChangeListener pageContentChangeListener) {
		this.pageContentChangeListener = pageContentChangeListener;
	}
	
	public void setOnPagerFocusChangeListener(OnPagerFocusChangeListener listener){
		this.pagerFocusChangeListener = listener;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		//不让pager的滑动速度太快
		if((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) && event.getAction() == KeyEvent.ACTION_DOWN){
			if(event.getRepeatCount() % 3 != 0){
				return true;
			}
		}
		
		return super.dispatchKeyEvent(event);
	}

	class FixedSpeedScroller extends Scroller {
		private int mDuration;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		public void setmDuration(int time) {
			mDuration = time;
		}

		public int getmDuration() {
			return mDuration;
		}

	}

	class GalleryPageTransformer implements PageTransformer {

		@Override
		public void transformPage(View view, float position) {
			if (position < -1) {
				view.setScaleX(0.6f);
				view.setScaleY(0.6f);
				view.setAlpha(0.2f);
			} else if (position <= 0) {
				float scaleUnit = 1 + (0.4f * position);
				view.setScaleX(scaleUnit);
				view.setScaleY(scaleUnit);
				view.setAlpha((1 + position) * 0.8f + 0.2f);

			} else if (position <= 1) {
				float scaleUnit = 1 - (0.4f * position);
				view.setScaleX(scaleUnit);
				view.setScaleY(scaleUnit);
				view.setAlpha((1 - position) * 0.8f + 0.2f);

			} else {
				view.setScaleX(0.6f);
				view.setScaleY(0.6f);
				view.setAlpha(0.2f);
			}
		}
	}

	class GalleryPageChangeListener implements OnPageChangeListener {

		private int selecte;
		private boolean isSelected = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {

			if (arg0 == 0 && isSelected) {
				int truthCount = -1;
				if (pageTextChangeListener != null) {
					truthCount = getTruthCount();
					pageTextChangeListener.currentPageTextChange(selecte
							% truthCount);
				}
				if (pageContentChangeListener != null) {
					if (truthCount == -1) {
						truthCount = getTruthCount();
					}
					pageContentChangeListener.currentPageContentChange(selecte
							% truthCount);
				}
				isSelected = false;
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			selecte = arg0;
			isSelected = true;
		}

	}

	public int getTruthCount() {
		int truthCount = 0;
		PagerAdapter tempAdapter = getAdapter();
		if (tempAdapter instanceof AbstractAdapter) {
			AbstractAdapter mAdapter = (AbstractAdapter) tempAdapter;
			truthCount = mAdapter.getTruthCount();
		} else {
			truthCount = tempAdapter.getCount();
		}

		return truthCount;
	}

	public void onViewDestroy() {
		PagerAdapter tempAdapter = getAdapter();
		if (tempAdapter instanceof AbstractAdapter) {
			((AbstractAdapter) tempAdapter).onViewDestroy(this);
		} else {
			removeAllViews();
		}
	}
}
