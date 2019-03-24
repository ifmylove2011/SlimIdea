package com.xter.slimidea.presentation.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

import com.xter.slimidea.common.util.L;

import java.util.HashMap;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;


/**
 * 自定义的一个粗糙的横向snap辅助类，主要用于治愈强迫症......
 * 目前可左中右自动对齐，可定制单次滑动最大item数目，可简单改变速度
 */
public class HorizontalLinearSnapHelper extends SnapHelper {

	public HorizontalLinearSnapHelper(Context context) {
		this.context = context;
	}

	/**
	 * @param context                主要用于创建SmoothScroller
	 * @param alignMode              对齐方式，目前提供左中右外加一种沙雕对齐方式
	 * @param maxFlingItems          最大滑动Item数目
	 * @param speedFactor            速度因子，为100时代表DPI为480的屏幕滑动1080像素需要670ms
	 * @param enableAbsolutePosition 是否开启固定位置，开启会根据对齐方式，固定currentPosition，配合maxFlingItems可固定滑动结束位置
	 */
	public HorizontalLinearSnapHelper(Context context, int alignMode, int maxFlingItems, float speedFactor, boolean enableAbsolutePosition) {
		this.context = context;
		this.alignMode = alignMode;
		this.maxFlingItems = maxFlingItems;
		this.speedFactor = speedFactor;
		this.enableAbsolutePosition = enableAbsolutePosition;
		recyclerViewLocationMap = new SparseIntArray();
	}

	public static final int ALIGN_START = 1;
	public static final int ALIGN_CENTER = 2;
	public static final int ALIGN_END = 3;
	public static final int ALIGN_SPECIAL = 4;

	/**
	 * 主要用于创建自有的scroller
	 */
	private Context context;

	/**
	 * 左中右对齐，一家人就是要整整齐齐
	 */
	private int alignMode = ALIGN_CENTER;

	/**
	 * 一次可以划动的最大item数，默认由本身findSnapView测量并;
	 * -1为默认由系统计算，0为自适应一屏item数目，大于0的整数则为自定数目
	 */
	private int maxFlingItems = -1;

	/**
	 * 可改速度，官方默认是100f
	 */
	private float speedFactor = 100f;

	/**
	 * 另一个强大的辅助类，自身提供了一些足够用的信息
	 */
	private OrientationHelper orientationHelper;

	/**
	 * 和RecyclerView.NO_POSITION一样用于非法计算结果
	 */
	private static final float INVALID_DISTANCE = 1f;

	/**
	 * 启用参考位置，使currentAbosolutePosition参数生效
	 */
	private boolean enableAbsolutePosition = false;

	/**
	 * 当前参考位置，此坐标用以配合滑动的item数量确定最终可见的items；
	 * 如果不用此属性，参考位置将由findSnapView提供索引position
	 */
	private int currentAbosolutePosition = -1;


	/**
	 * 为了添加另一个scrollListener于是覆写了此方法
	 * {@link SnapHelper#attachToRecyclerView(RecyclerView)}
	 */
	@Override
	public void attachToRecyclerView(@Nullable final RecyclerView recyclerView) throws IllegalStateException {
		super.attachToRecyclerView(recyclerView);
		if (recyclerView != null) {
			orientationHelper = OrientationHelper.createHorizontalHelper(recyclerView.getLayoutManager());
			if (enableAbsolutePosition) {
				recyclerView.addOnScrollListener(scrollListener);
			}
			recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					initData(recyclerView.getLayoutManager(), orientationHelper);
				}
			});
		}
	}

	/**
	 * RecyclerView的参考位置
	 */
	private SparseIntArray recyclerViewLocationMap;

	/**
	 * 须注意attach时，helper或layoutManager是无法得到准确信息的，所以得换个地方初始化，
	 * 最好是找一个比较靠后的监听器
	 *
	 * @param layoutManager l
	 * @param helper        h
	 */
	private void initData(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
		switch (alignMode) {
			case ALIGN_START:
				if (layoutManager.getClipToPadding()) {
					recyclerViewLocationMap.put(alignMode, helper.getStartAfterPadding());
				} else {
					recyclerViewLocationMap.put(alignMode, 0);
				}
				break;
			case ALIGN_CENTER:
				if (layoutManager.getClipToPadding()) {
					recyclerViewLocationMap.put(alignMode, helper.getStartAfterPadding() + helper.getTotalSpace() / 2);
				} else {
					recyclerViewLocationMap.put(alignMode, helper.getEnd() / 2);
				}
				break;
			case ALIGN_END:
				if (layoutManager.getClipToPadding()) {
					recyclerViewLocationMap.put(alignMode, helper.getEndAfterPadding());
				} else {
					recyclerViewLocationMap.put(alignMode, helper.getEnd());
				}
				break;
		}
		L.d(recyclerViewLocationMap.toString());
	}

	/**
	 * 利用recyclerview可添加多个OnScrollListener的特性，另开一监听器确定其开始滑动时的参考位置
	 */
	public RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			//滚动开始时，此时界面中可见的Item是最可靠的参考标准
			if (newState == SCROLL_STATE_DRAGGING) {
				switch (alignMode) {
					case ALIGN_START:
						currentAbosolutePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
						break;
					case ALIGN_CENTER:
						currentAbosolutePosition = (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() + ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()) / 2;
						break;
					case ALIGN_END:
						currentAbosolutePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
						break;
				}
				L.w("abosolutePosition=" + currentAbosolutePosition);
			}
			L.i("state=" + newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
		}
	};

	@Nullable
	@Override
	public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
		L.i("---------");
		int[] out = new int[2];
		if (layoutManager.canScrollHorizontally()) {
			switch (alignMode) {
				case ALIGN_START:
					out[0] = distanceToStart(layoutManager, targetView, orientationHelper);
					break;
				case ALIGN_CENTER:
					out[0] = distanceToCenter(layoutManager, targetView, orientationHelper);
					break;
				case ALIGN_END:
					out[0] = distanceToEnd(layoutManager, targetView, orientationHelper);
					break;
				case ALIGN_SPECIAL:
					out[0] = distanceToSpecial(layoutManager, targetView, orientationHelper);
					break;
			}
		} else {
			out[0] = 0;
		}
		L.d("out=" + out[0]);
		return out;
	}

	private int distanceToStart(RecyclerView.LayoutManager layoutManager, View targetView, OrientationHelper helper) {
//		if (layoutManager.getClipToPadding()) {
//			return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
//		} else {
//			return helper.getDecoratedStart(targetView);
//		}
		return helper.getDecoratedStart(targetView) - recyclerViewLocationMap.get(ALIGN_START);
	}

	private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager,
	                             @NonNull View targetView, OrientationHelper helper) {
		final int childCenter = helper.getDecoratedStart(targetView)
				+ (helper.getDecoratedMeasurement(targetView) / 2);
//		final int containerCenter;
//		if (layoutManager.getClipToPadding()) {
//			containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
//		} else {
//			containerCenter = helper.getEnd() / 2;
//		}
//		return childCenter - containerCenter;
		return childCenter - recyclerViewLocationMap.get(ALIGN_CENTER);
	}

	private int distanceToEnd(RecyclerView.LayoutManager layoutManager, View targetView, OrientationHelper helper) {
//		if (layoutManager.getClipToPadding()) {
//			return helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
//		} else {
//			return helper.getDecoratedEnd(targetView) - helper.getEnd();
//		}
		return helper.getDecoratedEnd(targetView) - recyclerViewLocationMap.get(ALIGN_END);
	}

	private int distanceToSpecial(RecyclerView.LayoutManager layoutManager, View targetView, OrientationHelper helper) {
		final int childStart = helper.getDecoratedStart(targetView);
		final int containerCenter;
		if (layoutManager.getClipToPadding()) {
			containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
		} else {
			containerCenter = helper.getEnd() / 2;
		}
		return childStart - containerCenter;
	}

	/**
	 * attachToRecyclerView时调用一次（会中断），滚动结束调用一次
	 *
	 * @param layoutManager m
	 * @return v
	 */
	@Nullable
	@Override
	public View findSnapView(RecyclerView.LayoutManager layoutManager) {
		L.i("-------");
		switch (alignMode) {
			case ALIGN_START:
				return findStartView(layoutManager, orientationHelper);
			case ALIGN_CENTER:
				return findCenterView(layoutManager, orientationHelper);
			case ALIGN_END:
				return findEndView(layoutManager, orientationHelper);
			case ALIGN_SPECIAL:
				return findSpecialView(layoutManager, orientationHelper);
		}
		return null;
	}


	private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return null;
		}

		View closestChild = null;

		final int start = recyclerViewLocationMap.get(ALIGN_START);

		int absClosest = Integer.MAX_VALUE;

		for (int i = 0; i < childCount; i++) {
			final View child = layoutManager.getChildAt(i);
			int childStart = helper.getDecoratedStart(child);
			int absDistance = Math.abs(childStart - start);

			if (absDistance < absClosest) {
				absClosest = absDistance;
				closestChild = child;
			}
		}
		return closestChild;
	}

	private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return null;
		}

		View closestChild = null;

		final int center = recyclerViewLocationMap.get(ALIGN_CENTER);

		int absClosest = Integer.MAX_VALUE;

		for (int i = 0; i < childCount; i++) {
			final View child = layoutManager.getChildAt(i);
			int childCenter = helper.getDecoratedStart(child)
					+ (helper.getDecoratedMeasurement(child) / 2);
			int absDistance = Math.abs(childCenter - center);

			if (absDistance < absClosest) {
				absClosest = absDistance;
				closestChild = child;
			}
		}
		return closestChild;
	}


	private View findEndView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return null;
		}

		View closestChild = null;

		final int end = recyclerViewLocationMap.get(ALIGN_END);

		int absClosest = Integer.MAX_VALUE;

		for (int i = 0; i < childCount; i++) {
			final View child = layoutManager.getChildAt(i);
			int childEnd = helper.getDecoratedEnd(child);
			int absDistance = Math.abs(childEnd - end);

			if (absDistance < absClosest) {
				absClosest = absDistance;
				closestChild = child;
			}
		}
		return closestChild;
	}


	private View findSpecialView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return null;
		}

		View closestChild = null;
		final int center;
		if (layoutManager.getClipToPadding()) {
			center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
		} else {
			center = helper.getEnd() / 2;
		}
		int absClosest = Integer.MAX_VALUE;

		for (int i = 0; i < childCount; i++) {
			final View child = layoutManager.getChildAt(i);
			int childStart = helper.getDecoratedStart(child);
			int absDistance = Math.abs(childStart - center);

			if (absDistance < absClosest) {
				absClosest = absDistance;
				closestChild = child;
			}
		}
		return closestChild;
	}

	/**
	 * 决定最终滑动到某个位置，可对比着看
	 * {@link android.support.v7.widget.LinearSnapHelper#findTargetSnapPosition(RecyclerView.LayoutManager, int, int)}
	 *
	 * @param layoutManager lm
	 * @param velocityX     vx
	 * @param velocityY     vy
	 * @return pos
	 */
	@Override
	public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
		if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
			return RecyclerView.NO_POSITION;
		}

		final int itemCount = layoutManager.getItemCount();
		if (itemCount == 0) {
			return RecyclerView.NO_POSITION;
		}

		final View currentView = findSnapView(layoutManager);
		if (currentView == null) {
			return RecyclerView.NO_POSITION;
		}
		L.w("find snap = " + layoutManager.getPosition(currentView));

		//如果启用参考位置，则findSnapView不再作为标准，而是根据ALIGN以当前可见的【最左|中间|最右】的View为标准
		final int currentPosition;
		if (enableAbsolutePosition) {
			currentPosition = currentAbosolutePosition;
		} else {
			currentPosition = layoutManager.getPosition(currentView);
		}

		L.i("current=" + currentPosition);

		if (currentPosition == RecyclerView.NO_POSITION) {
			return RecyclerView.NO_POSITION;
		}

		RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
				(RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
		//只会返回1和-1，1表示正向，-1表示反向
		PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);

		if (vectorForEnd == null) {
			return RecyclerView.NO_POSITION;
		}
		L.w("vector.x=" + vectorForEnd.x + ",vector.y=" + vectorForEnd.y);

		int deltaJumpMax;
		if (maxFlingItems > 0) {
			deltaJumpMax = maxFlingItems;
		} else {
			if (maxFlingItems == 0) {
				deltaJumpMax = orientationHelper.getTotalSpace() / orientationHelper.getDecoratedMeasurement(currentView);
			} else {
				deltaJumpMax = 0;
			}
		}
		L.i("jumpMax=" + deltaJumpMax);

		// deltaJump代表着将要Fling的item数目，显然与fling的滑动速度相关，
		// 如果想控制fling的item数目，就得控制这个变量
		int deltaJump;
		if (layoutManager.canScrollHorizontally()) {
			deltaJump = estimateNextPositionDiffForFling(layoutManager,
					orientationHelper, velocityX, 0);
			L.w("jump=" + deltaJump);

			if (deltaJumpMax > 0) {
				if (deltaJump > deltaJumpMax) {
					deltaJump = deltaJumpMax;
				}
				if (deltaJump < -deltaJumpMax) {
					deltaJump = -deltaJumpMax;
				}
			}
			if (vectorForEnd.x < 0) {
				deltaJump = -deltaJump;
			}
		} else {
			deltaJump = 0;
		}

		if (deltaJump == 0) {
			return RecyclerView.NO_POSITION;
		}
		int targetPos = currentPosition + deltaJump;
		L.w("current=" + currentPosition + ",delta=" + deltaJump + ",target=" + targetPos);

		if (targetPos < 0) {
			targetPos = 0;
		}
		if (targetPos >= itemCount) {
			targetPos = itemCount - 1;
		}
		return targetPos;
	}

	/**
	 * 直接copy
	 * {@link android.support.v7.widget.LinearSnapHelper#estimateNextPositionDiffForFling(RecyclerView.LayoutManager, OrientationHelper, int, int)}  }
	 */
	private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager,
	                                             OrientationHelper helper, int velocityX, int velocityY) {
		int[] distances = calculateScrollDistance(velocityX, velocityY);
		float distancePerChild = computeDistancePerChild(layoutManager, helper);
		if (distancePerChild <= 0) {
			return 0;
		}
		int distance =
				Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
		return Math.round(distance / distancePerChild);
	}

	/**
	 * 直接copy
	 * {@link android.support.v7.widget.LinearSnapHelper#computeDistancePerChild(RecyclerView.LayoutManager, OrientationHelper)}  }
	 */
	private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
	                                      OrientationHelper helper) {
		View minPosView = null;
		View maxPosView = null;
		int minPos = Integer.MAX_VALUE;
		int maxPos = Integer.MIN_VALUE;
		int childCount = layoutManager.getChildCount();
		if (childCount == 0) {
			return INVALID_DISTANCE;
		}

		for (int i = 0; i < childCount; i++) {
			View child = layoutManager.getChildAt(i);
			final int pos = layoutManager.getPosition(child);
			if (pos == RecyclerView.NO_POSITION) {
				continue;
			}
			if (pos < minPos) {
				minPos = pos;
				minPosView = child;
			}
			if (pos > maxPos) {
				maxPos = pos;
				maxPosView = child;
			}
		}
		if (minPosView == null || maxPosView == null) {
			return INVALID_DISTANCE;
		}
		int start = Math.min(helper.getDecoratedStart(minPosView),
				helper.getDecoratedStart(maxPosView));
		int end = Math.max(helper.getDecoratedEnd(minPosView),
				helper.getDecoratedEnd(maxPosView));
		int distance = end - start;
		if (distance == 0) {
			return INVALID_DISTANCE;
		}
		return 1f * distance / ((maxPos - minPos) + 1);
	}

	/**
	 * 只为修改一个参数，其他copy
	 * {@link SnapHelper#createSnapScroller(RecyclerView.LayoutManager)}
	 */
	@Nullable
	protected LinearSmoothScroller createSnapScroller(final RecyclerView.LayoutManager layoutManager) {
		if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
			return null;
		}
		return new LinearSmoothScroller(context) {
			@Override
			protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
				int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
				final int dx = snapDistances[0];
				final int dy = snapDistances[1];
				final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
//				final int time = 1000;
				if (time > 0) {
					L.w("dx=" + dx + ",time=" + time);
					action.update(dx, dy, time, mDecelerateInterpolator);
				}
			}

			@Override
			protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
				L.w("speedFator=" + speedFactor + ",dpi=" + displayMetrics.densityDpi);
				return speedFactor / displayMetrics.densityDpi;
			}
		};
	}

	public class ViewInfoPrinter {
		private RecyclerView.LayoutManager layoutManager;
		private OrientationHelper orientationHelper;
		private View view;

		public ViewInfoPrinter(RecyclerView.LayoutManager layoutManager, OrientationHelper orientationHelper, View view) {
			this.layoutManager = layoutManager;
			this.orientationHelper = orientationHelper;
			this.view = view;
		}

		@Override
		public String toString() {
			return "ViewInfoPrinter{" +
					"\n getEnd=" + orientationHelper.getEnd() +
					",\n getTotalSpace=" + orientationHelper.getTotalSpace() +
					",\n layout_getWidth=" + layoutManager.getWidth() +
					",\n getDecoratedStart=" + orientationHelper.getDecoratedStart(view) +
					",\n getDecoratedEnd=" + orientationHelper.getDecoratedEnd(view) +
					",\n layout_getDecoratedLeft=" + layoutManager.getDecoratedLeft(view) +
					",\n layout_getDecoratedRight=" + layoutManager.getDecoratedRight(view) +
					",\n getDecoratedMeasurement=" + orientationHelper.getDecoratedMeasurement(view) +
					",\n layout_getDecoratedMeasuredWidth=" + layoutManager.getDecoratedMeasuredWidth(view) +
					",\n view_getMeasuredWidth=" + view.getMeasuredWidth() +
					",\n getEndAfterPadding=" + orientationHelper.getEndAfterPadding() +
					",\n getStartAfterPadding=" + orientationHelper.getStartAfterPadding() +
					",\n layout_getPaddingLeft=" + layoutManager.getPaddingLeft() +
					",\n layout_getPaddingRight=" + layoutManager.getPaddingRight() +
					'}';
		}
	}
}