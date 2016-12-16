package com.sccp.library.widget;

import java.util.List;
import java.util.Map;

import com.sccp.frame.R;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * Draws a line for each page. The current page line is colored differently
 * than the unselected page lines.
 */
public class UnderlinePageIndicator extends View implements PageIndicator {
	
    private static final int INVALID_POINTER = -1;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private CZViewPager viewPager;
    private ViewPager.OnPageChangeListener listener;
    private int scrollState;
	private int currentPage;
	private float positionOffset;
	
	private int touchSlop;
    private int activePointerId = INVALID_POINTER;
    private float lastMotionX = -1;
    private boolean isDragging;
    private OnPageSelectedCallback onPageSelectedCallback;

    public UnderlinePageIndicator(Context context) {
        this(context, null);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vpiUnderlinePageIndicatorStyle);
    }

	public UnderlinePageIndicator(Context context, AttributeSet attrs, int defStyle) {
    	
        super(context, attrs, defStyle);
        setSelectedColor(0xFFefd5d8);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    public int getSelectedColor() {
        return paint.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        paint.setColor(selectedColor);
        invalidate();
    }
    
	public OnPageSelectedCallback getOnPageSelectedCallback() {
		return onPageSelectedCallback;
	}

	public void setOnPageSelectedCallback(
			OnPageSelectedCallback onPageSelectedCallback) {
		this.onPageSelectedCallback = onPageSelectedCallback;
	}

	@Override
    protected void onDraw(Canvas canvas) {
    	
        super.onDraw(canvas);

        if (viewPager == null) {
            return;
        }
        
        final int count = viewPager.getAdapter().getCount();
        
        if (count == 0) {
            return;
        }

        if (currentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        final int paddingLeft = getPaddingLeft();
        final float pageWidth = (getWidth() - paddingLeft - getPaddingRight()) / (1f * count);
        final float left = paddingLeft + pageWidth * (currentPage + positionOffset);
        final float right = left + pageWidth;
        final float top = getPaddingTop();
        final float bottom = getHeight() - getPaddingBottom();
        
        Log.d("onDraw", String.format("currentPage=%d", currentPage));
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void setViewPager(CZViewPager viewPager) {
    	
        if (this.viewPager == viewPager) {
            Log.d("setViewPager", String.format("viewPager is null"));
            return;
        }
        
        if (this.viewPager != null) {
            //Clear us from the old pager.
        	this.viewPager.setOnPageChangeListener(null);
        }
        
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        
        this.viewPager = viewPager;
        this.viewPager.setOnPageChangeListener(this);
        invalidate();
    }

    public void setViewPager(CZViewPager viewPager, int initialPosition) {
        setViewPager(viewPager);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
    	
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        
        viewPager.setCurrentItem(item);
        currentPage = item;
        invalidate();
    }

    public void notifyDataSetChanged() {
        invalidate();
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
        scrollState = state;

        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    	Log.d("onPageScrolled", String.format("positionOffset=%f", positionOffset));
        currentPage = position;
        this.positionOffset = positionOffset;
		final int count = viewPager.getAdapter().getCount();
		
		ImageView viewLeft = (ImageView) viewPager.findViewFromObject(position);
    	float mScale = 0.9f + 0.1f * (1 - positionOffset);
		ViewHelper.setScaleX(viewLeft, mScale);
		ViewHelper.setScaleY(viewLeft, mScale);
    	
		if(position + 1 < count) {
	    	ImageView viewRight = (ImageView) viewPager.findViewFromObject(position + 1);
	    	mScale = 0.9f + 0.1f * positionOffset;
			ViewHelper.setScaleX(viewRight, mScale);
			ViewHelper.setScaleY(viewRight, mScale);
		}
		
        invalidate();

        if (listener != null) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
    	
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
        	Log.d("onPageSelected", String.format("position=%d", position));
            currentPage = position;
            positionOffset = 0;
            invalidate();
        }
        
    	onPageSelectedCallback.onItemSelected(position);
    	
        if (listener != null) {
            listener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.listener = listener;
    }
    
    public void doInvalidate() {
    	postInvalidate();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = currentPage;
        return savedState;
    }

    public int getScrollState() {
		return scrollState;
	}

	public void setScrollState(int scrollState) {
		this.scrollState = scrollState;
	}
	
    public float getPositionOffset() {
		return positionOffset;
	}

	public void setPositionOffset(float positionOffset) {
		this.positionOffset = positionOffset;
	}
	
    public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public interface OnPageSelectedCallback {
		
		void onItemSelected(int position);
	}
	
    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((viewPager == null) || (viewPager.getAdapter().getCount() == 0)) {
            return false;
        }

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                lastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, activePointerIndex);
                final float deltaX = x - lastMotionX;

                if (!isDragging) {
                    if (Math.abs(deltaX) > touchSlop) {
                        isDragging = true;
                    }
                }

                if (isDragging) {
                    lastMotionX = x;
                    if (viewPager.isFakeDragging() || viewPager.beginFakeDrag()) {
                        viewPager.fakeDragBy(deltaX);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isDragging) {
                    final int count = viewPager.getAdapter().getCount();
                    final int width = getWidth();
                    final float halfWidth = width / 2f;
                    final float sixthWidth = width / 6f;

                    if ((currentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            viewPager.setCurrentItem(currentPage - 1);
                        }
                        return true;
                    } else if ((currentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            viewPager.setCurrentItem(currentPage + 1);
                        }
                        return true;
                    }
                }

                isDragging = false;
                activePointerId = INVALID_POINTER;
                if (viewPager.isFakeDragging()) viewPager.endFakeDrag();
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                lastMotionX = MotionEventCompat.getX(ev, index);
                activePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                lastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, activePointerId));
                break;
        }

        return true;
    }

    static class SavedState extends BaseSavedState {
        int currentPage;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}