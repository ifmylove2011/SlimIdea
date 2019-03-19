package com.xter.slimidea.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by XTER on 2016/10/27.
 * recycleview--holder
 */
public class ViewHolder extends RecyclerView.ViewHolder {
	//对于int-T的映射map，SparseArray性能更佳
	private SparseArray<View> views;
	private View convertView;

	public ViewHolder(View convertView) {
		super(convertView);
		this.views = new SparseArray<>();
		this.convertView = convertView;
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
