package com.xter.slimidea.presentation.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.xter.slimidea.R;
import com.xter.slimidea.common.util.L;
import com.xter.slimidea.presentation.adapter.QuickRecycleAdapter;
import com.xter.slimidea.presentation.adapter.ViewHolder;
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
		for (int i = 0; i < 20; i++) {
			dataDemo.add("第" + i + "个");
		}

		rvDemo = findViewById(R.id.rv_demo);
		rvDemo.setLayoutManager(new GridLayoutManager(this, 3));

		final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
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
		helper.attachToRecyclerView(rvDemo);

		textViewAdapter = new TextViewAdapter(this, R.layout.item_text, dataDemo);
		rvDemo.setAdapter(textViewAdapter);

		textViewAdapter.setOnItemClickLitener(new QuickRecycleAdapter.OnItemClickLitener() {
			@Override
			public void onItemClick(View view, int position, RecyclerView.ViewHolder holder) {
			}

			@Override
			public void onItemLongClick(View view, int position, RecyclerView.ViewHolder holder) {
				helper.startDrag(holder);
			}
		});
	}
}
