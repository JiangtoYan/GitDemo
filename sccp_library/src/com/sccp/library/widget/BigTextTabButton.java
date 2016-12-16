package com.sccp.library.widget;

import com.sccp.frame.R;
import com.sccp.library.util.ScreenUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BigTextTabButton extends HorizontalScrollView {
	
	private static final int DEF_TEXT_COLOR   = 0xFF000000;
	private static final int DEF_TEXT_SELECTED_COLOR   = 0xFFFF0000;
	private static final int DEF_TAB_PADDING  = 10;
	private static final float DEF_TEXT_SIZE = 14f;
	private static final float DEF_TEXT_SELECTED_SIZE = 18f;
	
	private int screenWidth;
	
	private LinearLayout mLinearLayout;
	private ViewPager mViewPager;
	
	private int mButtonBackground;	// 按钮背景
	private float mTextSize;		// 字体大小
	private int mTextColor;			// 字体颜色
	private float mSelectedTextSize;		// 当前选项字体大小
	private int mSelectedTextColor;			// 当前选项字体颜色
	
	private int tabSize;			// 选项卡的个数
	private int tabPadding;			// 选项卡的边距
	private int tabWidth;
	private int pageSelect;			// 当前选中的页面
	private int lastIndex = 0;

	private float scrollOffset;		// 滑块已经滑动过的宽度

	private TabsButtonOnClickListener onClickListener;

	public BigTextTabButton(Context context) {
		
		super(context);
		init(context, null);
	}

	public BigTextTabButton(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		init(context, attrs);
	}

	public BigTextTabButton(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int layoutHeight = getHeight();
		final int layoutWidth  = getWidth();	
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		super.onLayout(changed, l, t, r, b);
		
		// 当布局确定大小的时候，初始化滑块的位置。
		computeItemPosition(pageSelect, 0);
		changeItemStyle(pageSelect, 0);
	}

	private void init(Context context, AttributeSet attrs) {
		
		TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.BigTextTabButton);
		mButtonBackground = tArray.getResourceId(R.styleable.BigTextTabButton_bigTextTabButtonBackground, R.drawable.big_text_tab_button_background);
		tabPadding		  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_TAB_PADDING, getResources().getDisplayMetrics());
//		tabWidth		= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
		tabWidth		= 0;
		
		float defTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP, DEF_TEXT_SIZE, getResources().getDisplayMetrics());
		float defSelectedTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP, DEF_TEXT_SELECTED_SIZE, getResources().getDisplayMetrics());
		
		mTextSize  = tArray.getDimension(R.styleable.BigTextTabButton_unSelectedTextSize, defTextSize);
		mTextColor = tArray.getColor(R.styleable.BigTextTabButton_unSelectedTextColor, DEF_TEXT_COLOR);

		mSelectedTextSize  = tArray.getDimension(R.styleable.BigTextTabButton_selectedTextSize, defSelectedTextSize);
		mSelectedTextColor = tArray.getColor(R.styleable.BigTextTabButton_selectedTextColor, DEF_TEXT_SELECTED_COLOR);
		
		tArray.recycle();
		
		if (isInEditMode()) {
			return;
		}
		
		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//		mLinearLayout.setGravity(Gravity.CENTER);
		addView(mLinearLayout);

		setWillNotDraw(false);
		setHorizontalScrollBarEnabled(false);
	}

	public void setViewPager(ViewPager vp) {
		
		this.mViewPager = vp;
		vp.setOnPageChangeListener(getOnPageChangeListener());
		PagerAdapter pagerAdapter = vp.getAdapter();
		
		int count = pagerAdapter.getCount();
		tabWidth = getResources().getDisplayMetrics().widthPixels / count;
		
		for (int i = 0; i < count; i++) {
			addTab(newTextTab(pagerAdapter.getPageTitle(i)));
		}
	}

	@SuppressWarnings("deprecation")
	public View newTextTab(CharSequence text) {
		
		TextView tv = new TextView(getContext());
		tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
		tv.setGravity(Gravity.CENTER);
		tv.setWidth(tabWidth);
//		tv.setPadding(tabPadding, 0, tabPadding, 0);
		tv.setBackgroundResource(mButtonBackground);
		tv.setOnClickListener(buttonOnClick);
		tv.setTextColor(mTextColor);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		tv.setText(text);
		return tv;
	}

	public void addTab(View view) {
		view.setId(tabSize++);
		mLinearLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
	}

	public void setTab(String... strings) {
		for (String text : strings) {
			addTab(newTextTab(text));
		}
	}
	
	private void changeItemStyle(int index, float scroll) {

		if(mLinearLayout.getChildAt(0) == null) {
			return;
		}
		
		TextView tv = (TextView) mLinearLayout.getChildAt(index);
		tv.setTextColor(mSelectedTextColor);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
		
		if(index != lastIndex) {
			TextView preTv = (TextView) mLinearLayout.getChildAt(lastIndex);
			preTv.setTextColor(mTextColor);
			preTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		}
		
		lastIndex = index;
	}
	
	private void computeItemPosition(int index, float scroll){
		
		if(mLinearLayout.getChildAt(0) == null) {
			return;
		}
		
		int itemWidth = mLinearLayout.getChildAt(index).getWidth();
		// 滑块的长度。ratio
		if (index < tabSize-1) {
			int add = mLinearLayout.getChildAt(index+1).getWidth()-itemWidth;
//			sliderWidth = (int) (scroll * add + itemWidth  + 0.5f);
		}
		// 滑块已经滑动的距离。
		scrollOffset = mLinearLayout.getChildAt(index).getLeft() + scroll * itemWidth;
		// 控件宽度。
		screenWidth = getWidth();
		// 滑块中间点距离控件左边的距离。
		int half = (int) (scrollOffset + itemWidth / 2);
		// 当滑块中间点大于控件宽度一半或水平滚动量大于零时，让滑块保持在控件中间。
		if( half > (screenWidth/2) || computeHorizontalScrollOffset() !=0 )
			scrollTo(half - screenWidth/2, 0);
//		invalidate();
	}

	public OnPageChangeListener getOnPageChangeListener() {
		return new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				pageSelect = arg0;
				changeItemStyle(pageSelect, 0);
			}

			public void onPageScrolled(int index, float arg1, int scroll) {
				
				if (scroll != 0) {
					computeItemPosition(index, arg1);
				}
			}

			public void onPageScrollStateChanged(int state) {
				
				if(state == ViewPager.SCROLL_STATE_IDLE) {
					computeItemPosition(pageSelect, 0);
				}
			}
		};
	}

	private OnClickListener buttonOnClick = new OnClickListener() {

		public void onClick(View v) {
			
			if (onClickListener != null) {
				onClickListener.tabsButtonOnClick(v.getId(), v);
			}
			else if (mViewPager!= null){
				mViewPager.setCurrentItem(v.getId());
			}
		}
	};
	
	/**
	 * 监听按钮条被点击的接口。
	 * 
	 */
	public interface TabsButtonOnClickListener {
		void tabsButtonOnClick(int id, View v);
	}
	
	public void setTabsButtonOnClickListener(TabsButtonOnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
	
	/**
	 * 保存滑块的当前位置。当横竖屏切换或Activity被系统杀死时调用。
	 */
	protected Parcelable onSaveInstanceState() {
		Parcelable parcelable = super.onSaveInstanceState();
		TabsSaveState tabsSaveState = new TabsSaveState(parcelable);
		tabsSaveState.select = pageSelect;
		return tabsSaveState;
	}
	
	/**
	 * 还原滑块的位置。
	 */
	protected void onRestoreInstanceState(Parcelable state) {
		
		TabsSaveState tabsSaveState = (TabsSaveState) state;
		super.onRestoreInstanceState(tabsSaveState.getSuperState());
		
		if(mLinearLayout.getChildAt(0) == null) {
			//return;
		}

		pageSelect = tabsSaveState.select;
		computeItemPosition(pageSelect, 0);
		changeItemStyle(pageSelect, 0);
	}
	
	static class TabsSaveState extends BaseSavedState {
		
		private int select;

		public TabsSaveState(Parcelable arg0) {
			super(arg0);
		}

		public TabsSaveState(Parcel arg0) {
			super(arg0);
			select = arg0.readInt();
		}

		public int describeContents() {
			return super.describeContents();
		}

		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(select);
		}

		public static final Parcelable.Creator<BigTextTabButton.TabsSaveState> CREATOR = new Parcelable.Creator<BigTextTabButton.TabsSaveState>() {

			public TabsSaveState createFromParcel(Parcel source) {
				return new TabsSaveState(source);
			}

			public TabsSaveState[] newArray(int size) {
				return new TabsSaveState[size];
			}
		};
	}
}