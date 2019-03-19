package com.xter.slimidea.presentation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter 快速使用基类
 * 适用于RecycleView
 *
 * @author XTER
 */
public abstract class QuickRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	/**
	 * 第一种ViewType，正常的item
	 */
	private static final int NORMAL = 0;
	/**
	 * 第二种ViewType，底部的提示View
	 */
	private static final int FOOT = 1;

	protected Context context;

	/**
	 * 数据
	 */
	protected List<T> data;
	/**
	 * 视图资源ID
	 */
	private int res;
	/**
	 * 【加载更多】资源ID
	 */
	private int footRes;

	/**
	 * 是否有更多数据，子类可凭此标记做额外的逻辑
	 */
	protected boolean hasMore = true;

	/**
	 * 点击与长按监听接口
	 */
	public interface OnItemClickLitener {
		void onItemClick(View view, int position, RecyclerView.ViewHolder holder);

		void onItemLongClick(View view, int position, RecyclerView.ViewHolder holder);
	}

	/**
	 * 【加载更多】视图的监听接口
	 */
	public interface FootItemClickListener {
		void onFootClick();
	}

	private OnItemClickLitener itemClickLitener;
	private FootItemClickListener footItemClickListener;

	public void setOnItemClickLitener(OnItemClickLitener itemClickLitener) {
		this.itemClickLitener = itemClickLitener;
	}

	public void setFootItemClickListener(FootItemClickListener footItemClickListener) {
		this.footItemClickListener = footItemClickListener;
	}

	public QuickRecycleAdapter(Context context, int res, List<T> data) {
		this.context = context;
		//避免data为null
		this.data = new ArrayList<>();
		if (data != null)
			this.data.addAll(data);
		this.res = res;
	}

	public QuickRecycleAdapter(Context context, int res, List<T> data, int footRes) {
		this.context = context;
		//避免data为null
		this.data = new ArrayList<>();
		if (data != null)
			this.data.addAll(data);
		this.res = res;
		this.footRes = footRes;
	}

	@Override
	public int getItemCount() {
		//如有底部视图，则占位+1
		return footRes == 0 ? data.size() : data.size() + 1;
	}

	public T getItem(int position) {
		if (position > getItemCount() - 1)
			return null;
		return data.get(position);
	}

	@Override
	public int getItemViewType(int position) {
		//确定底部视图的位置
		if (footRes != 0 && position == getItemCount() - 1) {
			return FOOT;
		} else {
			return NORMAL;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//尽量使用LayoutInflater.from(context).inflate(...)方式，而非View.inflate(...)，可避免item的一些布局问题，例如无法控制item的宽度高度
		if (footRes != 0 && viewType == FOOT) {
			return new FootViewHolder(LayoutInflater.from(context).inflate(footRes, parent, false));
		} else {
			return new ViewHolder(LayoutInflater.from(context).inflate(res, parent, false));
		}
	}

	/**
	 * item的绘制，子类必须覆写
	 *
	 * @param holder   视图容器
	 * @param position 索引
	 */
	public abstract void bindView(ViewHolder holder, int position);

	/**
	 * 底部视图绘制，当footres不为0时覆写方能生效
	 *
	 * @param holder 视图容器
	 */
	protected void bindFootView(FootViewHolder holder) {
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if (footRes != 0 && holder instanceof FootViewHolder) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (footItemClickListener != null)
						footItemClickListener.onFootClick();
				}
			});
			bindFootView((FootViewHolder) holder);
		} else {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClickLitener != null)
						itemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition(),holder);
				}
			});
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (itemClickLitener != null)
						itemClickLitener.onItemLongClick(holder.itemView, holder.getLayoutPosition(),holder);
					return true;
				}
			});
			bindView((ViewHolder) holder, position);
		}
	}

	public List<T> getData() {
		return data;
	}

	public void add(T ele) {
		if (data.contains(ele)) {
			data.remove(ele);
		}
		data.add(ele);
		notifyDataSetChanged();
	}

	public void addFirst(T ele) {
		if (data.contains(ele)) {
			data.remove(ele);
		}
		data.add(0, ele);
		notifyDataSetChanged();
	}

	public void addAll(List<T> elem) {
		if (elem != null && elem.size() > 0) {
			data.addAll(elem);
			hasMore = true;
		} else {
			hasMore = false;
		}
		notifyDataSetChanged();
	}

	public void remove(T elem) {
		data.remove(elem);
		notifyDataSetChanged();
	}

	public void remove(int index) {
		data.remove(index);
		notifyItemRemoved(index);
		notifyDataSetChanged();
	}

	public void replace(T elem) {
		if (elem == null)
			return;
		int index = data.indexOf(elem);
		getData().set(index, elem);
		notifyDataSetChanged();
	}

	public void replaceAll(List<T> elem) {
		if (elem == null) {
			data.clear();
			notifyDataSetChanged();
			return;
		}
		data.clear();
		data.addAll(elem);

		notifyDataSetChanged();
	}
}
