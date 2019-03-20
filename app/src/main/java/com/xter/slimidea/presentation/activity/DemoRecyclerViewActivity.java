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
import com.xter.slimidea.presentation.adapter.QuickRecycleAdapter;
import com.xter.slimidea.presentation.adapter.function.TextViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
//		SnapHelper lineSnapHelper = new LinearSnapHelper();
//		snapHelper.attachToRecyclerView(rvDemo);
//		lineSnapHelper.attachToRecyclerView(rvDemo);

		SnapHelper snapHelper = new HorizontalLinearSnapHelper(AlignMode.START,0,40f);
		snapHelper.attachToRecyclerView(rvDemo);


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

	public enum AlignMode {
		START,
		CENTER,
		END
	}

	public class HorizontalLinearSnapHelper extends SnapHelper {

		public HorizontalLinearSnapHelper(AlignMode alignMode, int onceFlingItemNums, float speed) {
			this.alignMode = alignMode;
			this.onceFlingItemNums = onceFlingItemNums;
			this.speed = speed;
		}


		private AlignMode alignMode;

		private int onceFlingItemNums = -1;

		private float speed = 100f;

		private OrientationHelper orientationHelper;

		private static final float INVALID_DISTANCE = 1f;


		public OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
			if (orientationHelper == null)
				orientationHelper = OrientationHelper.createHorizontalHelper(layoutManager);
			return orientationHelper;
		}

		@Nullable
		@Override
		public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
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
				return helper.getDecoratedStart(targetView);
			} else {
				return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
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
				return helper.getDecoratedEnd(targetView);
			} else {
				return helper.getDecoratedEnd(targetView) + helper.getEndAfterPadding();
			}
		}

		@Nullable
		@Override
		public View findSnapView(RecyclerView.LayoutManager layoutManager) {
			switch (alignMode) {
				case START:
					return findStartView(layoutManager, getHorizontalHelper(layoutManager));
				case CENTER:
					return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
				case END:
					return findEndView(layoutManager, getHorizontalHelper(layoutManager));
			}
			return null;
		}

		private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			if (layoutManager instanceof LinearLayoutManager) {
				//找出第一个可见的ItemView的位置
				int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
				if (firstChildPosition == RecyclerView.NO_POSITION) {
					return null;
				}
				int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
				if (lastChildPosition == layoutManager.getItemCount() - 1) {
					return null;
				}

				View firstChildView = layoutManager.findViewByPosition(firstChildPosition);

				//超过一半被遮住就考虑下一个，否则就当前
				if (helper.getDecoratedEnd(firstChildView) >= helper.getDecoratedMeasurement(firstChildView) / 2 && helper.getDecoratedEnd(firstChildView) > 0) {
					return firstChildView;
				} else {
					return layoutManager.findViewByPosition(firstChildPosition + 1);
				}
			} else {
				return null;
			}
		}

		private View findCenterView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
			if (firstChildPosition == RecyclerView.NO_POSITION) {
				return null;
			}
			int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
			if (lastChildPosition == layoutManager.getItemCount() - 1) {
				return null;
			}
			return layoutManager.findViewByPosition((firstChildPosition + lastChildPosition) / 2);
		}

		private View findEndView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
			if (firstChildPosition == RecyclerView.NO_POSITION) {
				return null;
			}
			int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
			View lastChildView = layoutManager.findViewByPosition(lastChildPosition);
			if ((layoutManager.getClipToPadding() ? helper.getEndAfterPadding() : helper.getEnd()) - helper.getDecoratedStart(lastChildView) > (helper.getDecoratedMeasurement(lastChildView)) / 2) {
				return lastChildView;
			} else {
				layoutManager.findViewByPosition(lastChildPosition - 1);
			}
			return null;
		}

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

			final int currentPosition = layoutManager.getPosition(currentView);
			if (currentPosition == RecyclerView.NO_POSITION) {
				return RecyclerView.NO_POSITION;
			}

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
			L.w("threshod="+deltaThreshold);

			int deltaJump;
			if (layoutManager.canScrollHorizontally()) {
				deltaJump = estimateNextPositionDiffForFling(layoutManager,
						getHorizontalHelper(layoutManager), velocityX, 0);
				L.w("jump="+deltaJump);
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

	SnapHelper snapHelper = new SnapHelper() {

		private OrientationHelper orientationHelper;

		@Nullable
		@Override
		public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
			int[] out = new int[2];
			if (layoutManager.canScrollHorizontally()) {
				out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
//				out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager));
			} else {
				out[0] = 0;
			}
			return out;
		}

		public OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
			if (orientationHelper == null)
				orientationHelper = OrientationHelper.createHorizontalHelper(layoutManager);
			return orientationHelper;
		}

		//targetView的start坐标与RecyclerView的paddingStart之间的差值
		//就是需要滚动调整的距离
		private int distanceToStart(View targetView, OrientationHelper helper) {
			L.d("decoratedStart=" + helper.getDecoratedStart(targetView) + ",startAfterPadding=" + helper.getStartAfterPadding());
			return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
		}

		private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager,
		                             @NonNull View targetView, OrientationHelper helper) {
			L.d("decoratedStart=" + helper.getDecoratedStart(targetView) + ",startAfterPadding=" + helper.getStartAfterPadding());
			L.d("decoratedEnd=" + helper.getDecoratedEnd(targetView) + ",endAfterPadding=" + helper.getEndAfterPadding());
			L.d("measurement=" + helper.getDecoratedMeasurement(targetView));
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

		@Nullable
		@Override
		public View findSnapView(RecyclerView.LayoutManager layoutManager) {
			return findStartView(layoutManager, getHorizontalHelper(layoutManager));
		}

		private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
			if (layoutManager instanceof LinearLayoutManager) {
				//找出第一个可见的ItemView的位置
				int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
				if (firstChildPosition == RecyclerView.NO_POSITION) {
					return null;
				}
				int lastChildPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
				int lastCompleteChildPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
				//找到最后一个完全显示的ItemView，如果该ItemView是列表中的最后一个
				//就说明列表已经滑动最后了，这时候就不应该根据第一个ItemView来对齐了
				//要不然由于需要跟第一个ItemView对齐最后一个ItemView可能就一直无法完全显示，
				//所以这时候直接返回null表示不需要对齐
				if (lastChildPosition == layoutManager.getItemCount() - 1 && lastCompleteChildPosition == layoutManager.getItemCount() - 1) {
					return null;
				}


				View firstChildView = layoutManager.findViewByPosition(firstChildPosition);
//				View lastChildView = layoutManager.findViewByPosition(lastChildPosition);
//
//				return layoutManager.findViewByPosition((firstChildPosition + lastChildPosition) / 2);

				//如果第一个ItemView被遮住的长度没有超过一半，就取该ItemView作为snapView
				//超过一半，就把下一个ItemView作为snapView
				if (helper.getDecoratedEnd(firstChildView) >= helper.getDecoratedMeasurement(firstChildView) / 2 && helper.getDecoratedEnd(firstChildView) > 0) {
					return firstChildView;
				} else {
					return layoutManager.findViewByPosition(firstChildPosition + 1);
				}
			} else {
				return null;
			}
		}

		private View findCenterView(RecyclerView.LayoutManager layoutManager,
		                            OrientationHelper helper) {
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
				int childCenter = helper.getDecoratedStart(child)
						+ (helper.getDecoratedMeasurement(child) / 2);
				int absDistance = Math.abs(childCenter - center);

				/** if child center is closer than previous closest, set it as closest  **/
				if (absDistance < absClosest) {
					absClosest = absDistance;
					closestChild = child;
				}
			}
			return closestChild;
		}

		private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
		                                      OrientationHelper helper) {
			View minPosView = null;
			View maxPosView = null;
			int minPos = Integer.MAX_VALUE;
			int maxPos = Integer.MIN_VALUE;
			int childCount = layoutManager.getChildCount();
			if (childCount == 0) {
				return 1f;
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
				return 1f;
			}
			int start = Math.min(helper.getDecoratedStart(minPosView),
					helper.getDecoratedStart(maxPosView));
			int end = Math.max(helper.getDecoratedEnd(minPosView),
					helper.getDecoratedEnd(maxPosView));
			int distance = end - start;
			if (distance == 0) {
				return 1f;
			}
			return 1f * distance / ((maxPos - minPos) + 1);
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
			return (int) Math.round(distance / distancePerChild);
		}

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

			final int currentPosition = layoutManager.getPosition(currentView);
			if (currentPosition == RecyclerView.NO_POSITION) {
				return RecyclerView.NO_POSITION;
			}

			RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
					(RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;

			PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
			if (vectorForEnd == null) {
				return RecyclerView.NO_POSITION;
			}

			//计算一屏的item数
			int deltaThreshold = layoutManager.getWidth() / getHorizontalHelper(layoutManager).getDecoratedMeasurement(currentView);

			int deltaJump;
			if (layoutManager.canScrollHorizontally()) {
				deltaJump = estimateNextPositionDiffForFling(layoutManager,
						getHorizontalHelper(layoutManager), velocityX, 0);
				L.w("jump="+deltaJump+",threshold="+deltaThreshold);
				if (deltaJump > deltaThreshold) {
					deltaJump = deltaThreshold;
				}
				if (deltaJump < -deltaThreshold) {
					deltaJump = -deltaThreshold;
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
			if (targetPos < 0) {
				targetPos = 0;
			}
			if (targetPos >= itemCount) {
				targetPos = itemCount - 1;
			}
			return targetPos;
		}

//		@Nullable
//		@Override
//		protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
//			if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
//				return null;
//			}
//			return new LinearSmoothScroller(rvDemo.getContext()) {
//				@Override
//				protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
//					int[] snapDistances = calculateDistanceToFinalSnap(rvDemo.getLayoutManager(), targetView);
//					final int dx = snapDistances[0];
//					final int dy = snapDistances[1];
//					final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
//					if (time > 0) {
//						action.update(dx, dy, time, mDecelerateInterpolator);
//					}
//				}
//
//				@Override
//				protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//					return 160f / displayMetrics.densityDpi;
//				}
//			};
//		}

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
					return
							100f / displayMetrics.densityDpi;
				}
			};
		}
	};
}
