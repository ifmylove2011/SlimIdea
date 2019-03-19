package com.xter.slimidea.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by XTER on 2017/11/10.
 * 底部视图，一般用于加载更多的标识
 */
public class FootViewHolder extends RecyclerView.ViewHolder {

	private SparseArray<View> views = new SparseArray<>();
	private View convertView;

	public FootViewHolder(View convertView) {
		super(convertView);
		this.convertView = convertView;
	}

	public View getConvertView() {
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int resId) {
		View v = views.get(resId);
		if (null == v) {
			v = convertView.findViewById(resId);
			views.put(resId, v);
		}
		return (T) v;
	}
}
