package com.xter.slimidea.presentation.activity;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;

import com.xter.slimidea.R;
import com.xter.slimidea.common.util.L;
import com.xter.slimidea.presentation.adapter.QuickItemDecoration;
import com.xter.slimidea.presentation.adapter.QuickRecycleAdapter;
import com.xter.slimidea.presentation.adapter.function.TextViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.xter.slimidea.presentation.adapter.QuickItemDecoration.HORIZONTAL_LIST;

/**
 * Created by XTER on 2019/3/19.
 * recyclerview的测试
 */

public class DemoRecyclerViewActivity extends AppCompatActivity {

	RecyclerView rvDemo;
	TextViewAdapter textViewAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recyclerview);
		initView();
	}

	protected void initView() {
		List<String> dataDemo = new ArrayList<>();
		for (int i = 0; i < 70; i++) {
			dataDemo.add("第" + i + "个");
		}

		rvDemo = findViewById(R.id.rv_demo);
//		rvDemo.setLayoutManager(new GridLayoutManager(this, 3));
//		itemTouchHelper.attachToRecyclerView(rvDemo);

		rvDemo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		rvDemo.addItemDecoration(new QuickItemDecoration(this, HORIZONTAL_LIST));
//		SnapHelper lineSnapHelper = new LinearSnapHelper();
//		snapHelper.attachToRecyclerView(rvDemo);
//		lineSnapHelper.attachToRecyclerView(rvDemo);

//		HorizontalLinearSnapHelper snapHelper = new HorizontalLinearSnapHelper(AlignMode.START, 0, 40f);
//		HorizontalLinearSnapHelper snapHelper = new HorizontalLinearSnapHelper(AlignMode.CENTER, 0, 40f);
		HorizontalLinearSnapHelper snapHelper = new HorizontalLinearSnapHelper(AlignMode.END, 0, 40f);
		snapHelper.attachToRecyclerView(rvDemo);
		rvDemo.addOnScrollListener(snapHelper.scrollListener);

		textViewAdapter = new TextViewAdapter(this, R.layout.item_text, dataDemo);
		rvDemo.setAdapter(textViewAdapter);

//		textViewAdapter.setOnItemClickLitener(new QuickRecycleAdapter.OnItemClickLitener() {
//			@Override
//			public void onItemClick(View view, int position, RecyclerView.ViewHolder holder) {
//			}
//
//			@Override
//			public void onItemLongClick(View view, int position, RecyclerView.ViewHolder holder) {
//				itemTouchHelper.startDrag(holder);
//			}
//		});
	}

	ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
		@Override
		public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
				final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
						ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
				final int swipeFlags = 0;
				return makeMovementFlags(dragFlags, swipeFlags);
			} else {
				final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
				final int swipeFlags = 0;
				return makeMovementFlags(dragFlags, swipeFlags);
			}
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			//得到当拖拽的viewHolder的Position
			int fromPosition = viewHolder.getAdapterPosition();
			//拿到当前拖拽到的item的viewHolder
			int toPosition = target.getAdapterPosition();
			L.i("from:" + fromPosition + ",to" + toPosition);
			if (fromPosition < toPosition) {
				for (int i = fromPosition; i < toPosition; i++) {
					Collections.swap(textViewAdapter.getData(), i, i + 1);
				}
			} else {
				for (int i = fromPosition; i > toPosition; i--) {
					Collections.swap(textViewAdapter.getData(), i, i - 1);
				}
			}
			textViewAdapter.notifyItemMoved(fromPosition, toPosition);
			return true;
		}

		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

		}

		@Override
		public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
			if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
				viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
			}
			super.onSelectedChanged(viewHolder, actionState);
		}

		@Override
		public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
			super.clearView(recyclerView, viewHolder);
			viewHolder.itemView.setBackgroundColor(0);
		}

		@Override
		public boolean isLongPressDragEnabled() {
			return false;
		}
	});

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

	public enum AlignMode {
		START,
		CENTER,
		END
	}

	/**
	 * 自定义的一个粗糙的横向snap辅助类，主要用于治愈强迫症......
	 * 目前可左中右自动对齐，可定制单次滑动最大item数目，可简单改变速度
	 */
	public class HorizontalLinearSnapHelper extends SnapHelper {

		public HorizontalLinearSnapHelper(AlignMode alignMode, int onceFlingItemNums, float speed) {
			this.alignMode = alignMode;
			this.onceFlingItemNums = onceFlingItemNums;
			this.speed = speed;
		}

		public HorizontalLinearSnapHelper(AlignMode alignMode, float speed) {
			this.alignMode = alignMode;
			this.speed = speed;
		}

		public HorizontalLinearSnapHelper(AlignMode alignMode, int onceFlingItemNums) {
			this.alignMode = alignMode;
			this.onceFlingItemNums = onceFlingItemNums;
		}

		public HorizontalLinearSnapHelper(AlignMode alignMode) {
			this.alignMode = alignMode;
		}

		/**
		 * 左中右对齐，一家人就是要整整齐齐
		 */
		private AlignMode alignMode;

		/**
		 * 一次可以划动的最大item数，默认由本身findSnapView测量并
		 */
		private int onceFlingItemNums = -1;

		/**
		 * 可改速度，默认是100f
		 */
		private float speed = 100f;

		/**
		 * 另一个强大的辅助类，自身提供了一些足够用的信息
		 */
		private OrientationHelper orientationHelper;

		/**
		 * 和RecyclerView.NO_POSITION一样用于非法计算结果
		 */
		private static final float INVALID_DISTANCE = 1f;

		/**
		 * 当前参考位置，此坐标用以配合滑动的item数量确定最终可见的items；
		 * 如果不用此属性，参考位置将是ACTION_DOWN所在区域的item的索引position
		 */
		private int currentAbosolutePosition = -1;

		/**
		 * 利用recyclerview可添加多个OnScrollListener的特性，另开一监听器确定其开始滑动时的参考位置
		 */
		public RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == SCROLL_STATE_DRAGGING) {
					switch (alignMode) {
						case START:
							currentAbosolutePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
							break;
						case CENTER:
							currentAbosolutePosition = (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() + ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()) / 2;
							break;
						case END:
							currentAbosolutePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
							break;
					}
					L.w("abosolute=" + currentAbosolutePosition);
				}
				if (newState == SCROLL_STATE_IDLE) {
					L.i("idle");
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}
		};

		private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
			if (orientationHelper == null)
				orientationHelper = OrientationHelper.createHorizontalHelper(layoutManager);
			return orientationHelper;
		}

		@Nullable
		@Override
		public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
			L.i("---------");
			int[] out = new int[2];
			if (layoutManager.canScrollHorizontally()) {
				switch (alignMode) {
					case START:
						out[0] = distanceToStart(layoutManager, targetView, getHorizontalHelper(layoutManager));
						break;
					case CENTER:
						out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager));
						break;
					case END:
						out[0] = distanceToEnd(layoutManager, targetView, getHorizontalHelper(layoutManager));
						break;
				}
			} else {
				out[0] = 0;
			}
			return out;
		}

		private int distanceToStart(RecyclerView.LayoutManager layoutManager, View targetView, OrientationHelper helper) {
			if (layoutManager.getClipToPadding()) {
				return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
			} else {
				return helper.getDecoratedStart(targetView);
			}
		}

		private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager,
		                             @NonNull View targetView, OrientationHelper helper) {
			final int childCenter = helper.getDecoratedStart(targetView)
					+ (helper.getDecoratedMeasurement(targetView) / 2);
			final int containerCenter;
			if (layoutManager.getClipToPadding()) {
				containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
			} else {
				containerCenter = helper.getEnd() / 2;
			}
			return childCenter - containerCenter;
		}

		private int distanceToEnd(RecyclerView.LayoutManager layoutManager, View targetView, OrientationHelper helper) {
			if (layoutManager.getClipToPadding()) {
				return helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
			} else {
				return helper.getDecoratedEnd(targetView) - helper.getEnd();
			}
		}

		/**
		 * attachToRecyclerView时调用一次，滚动结束调用一次
		 *
		 * @param layoutManager m
		 * @return v
		 */
		@Nullable
		@Override
		public View findSnapView(RecyclerView.LayoutManager layoutManager) {
			L.i("-------");
			switch (alignMode) {
				case START:
					return findStartView(layoutManager, getHorizontalHelper(layoutManager));
				case CENTER:
					return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
				case END:
					return findEndView(layoutManager, getHorizontalHelper(layoutManager));
			}
			L.w("snapView=null");
			return null;
		}

		private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			if (layoutManager instanceof LinearLayoutManager) {
				//找出第一个可见的ItemView的位置
				int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
				L.i("findFirstVisibleItemPosition=" + firstChildPosition);
				if (firstChildPosition == RecyclerView.NO_POSITION) {
					return null;
				}
//				ViewInfoPrinter printer = new ViewInfoPrinter(layoutManager, helper, layoutManager.findViewByPosition(firstChildPosition));
//				L.d(printer.toString());
				int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
				if (lastChildPosition == layoutManager.getItemCount() - 1) {
					return null;
				}
				L.i("findLastVisibleItemPosition=" + lastChildPosition);
				View firstChildView = layoutManager.findViewByPosition(firstChildPosition);

				final int start;
				if (layoutManager.getClipToPadding()) {
					start = helper.getStartAfterPadding();
				} else {
					start = 0;
				}
				//超过一半被遮住就考虑下一个，否则就当前
				if (helper.getDecoratedEnd(firstChildView) - start > helper.getDecoratedMeasurement(firstChildView) / 2) {
					return firstChildView;
				} else {
					return layoutManager.findViewByPosition(firstChildPosition + 1);
				}
			} else {
				return null;
			}
		}

		private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			if (layoutManager instanceof LinearLayoutManager) {
				int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
				L.i("findFirstVisibleItemPosition=" + firstChildPosition);
				if (firstChildPosition == RecyclerView.NO_POSITION) {
					return null;
				}
				int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
				L.i("findLastVisibleItemPosition=" + lastChildPosition);
				if (lastChildPosition == layoutManager.getItemCount() - 1) {
					return null;
				}
				return layoutManager.findViewByPosition((firstChildPosition + lastChildPosition) / 2);
			} else {
				return null;
			}
		}

		private View findEndView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			if (layoutManager instanceof LinearLayoutManager) {
				int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
				L.i("findFirstVisibleItemPosition=" + firstChildPosition);
				if (firstChildPosition == RecyclerView.NO_POSITION) {
					return null;
				}
				int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
				L.i("findLastVisibleItemPosition=" + lastChildPosition);
				if (lastChildPosition == layoutManager.getItemCount() - 1) {
					return null;
				}

				View lastChildView = layoutManager.findViewByPosition(lastChildPosition);
				final int end;
				if (layoutManager.getClipToPadding()) {
					end = helper.getEndAfterPadding();
				} else {
					end = helper.getEnd();
				}
				if (end - helper.getDecoratedStart(lastChildView) > helper.getDecoratedMeasurement(lastChildView) / 2) {
					return lastChildView;
				} else {
					return layoutManager.findViewByPosition(lastChildPosition - 1);
				}
			} else {
				return null;
			}
		}

		/**
		 * 决定最终划到某个位置
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
			L.i("find snap = " + layoutManager.getPosition(currentView));

//			final int currentPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
			final int currentPosition = currentAbosolutePosition;
			L.i("current=" + currentPosition);
//			final int currentPosition = layoutManager.getPosition(currentView);
//			if (currentPosition == RecyclerView.NO_POSITION) {
//				return RecyclerView.NO_POSITION;
//			}

			RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
					(RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;

			PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
			if (vectorForEnd == null) {
				return RecyclerView.NO_POSITION;
			}

			int deltaThreshold;
			if (onceFlingItemNums == 0) {
				deltaThreshold = layoutManager.getWidth() / getHorizontalHelper(layoutManager).getDecoratedMeasurement(currentView);
			} else {
				deltaThreshold = onceFlingItemNums;
			}
			L.i("threshod=" + deltaThreshold);

			int deltaJump;
			if (layoutManager.canScrollHorizontally()) {
				deltaJump = estimateNextPositionDiffForFling(layoutManager,
						getHorizontalHelper(layoutManager), velocityX, 0);
				L.w("jump=" + deltaJump);
				if (deltaThreshold != 0) {
					if (deltaJump > deltaThreshold) {
						deltaJump = deltaThreshold;
					}
					if (deltaJump < -deltaThreshold) {
						deltaJump = -deltaThreshold;
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

		@Nullable
		protected LinearSmoothScroller createSnapScroller(final RecyclerView.LayoutManager layoutManager) {
			if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
				return null;
			}
			return new LinearSmoothScroller(rvDemo.getContext()) {
				@Override
				protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
					int[] snapDistances = calculateDistanceToFinalSnap(rvDemo.getLayoutManager(), targetView);
					final int dx = snapDistances[0];
					final int dy = snapDistances[1];
					final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
					if (time > 0) {
						action.update(dx, dy, time, mDecelerateInterpolator);
					}
				}

				@Override
				protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
					return speed / displayMetrics.densityDpi;
				}
			};
		}
	}
}
