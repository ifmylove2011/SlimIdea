package com.xter.slimidea.presentation.adapter.function;

import android.content.Context;
import android.widget.TextView;

import com.xter.slimidea.R;
import com.xter.slimidea.presentation.adapter.QuickRecycleAdapter;
import com.xter.slimidea.presentation.adapter.ViewHolder;

import java.util.List;

/**
 * Created by XTER on 2019/3/19.
 * 简单的文字
 */
public class TextViewAdapter extends QuickRecycleAdapter<String> {
	public TextViewAdapter(Context context, int res, List<String> data) {
		super(context, res, data);
	}

	@Override
	public void bindView(ViewHolder holder, int position) {
		TextView tvDemo = holder.getView(R.id.tv_demo);
		tvDemo.setText(data.get(position));
	}
}
