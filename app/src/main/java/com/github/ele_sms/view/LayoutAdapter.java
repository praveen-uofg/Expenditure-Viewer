package com.github.ele_sms.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ele_sms.BR;
import com.github.ele_sms.R;
import com.github.ele_sms.model.Data_Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AT-Praveen on 20/02/18.
 */

public class LayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Data_Model> mList;

    LayoutAdapter() {
        mList = new ArrayList<>();
    }


    void setList(List<Data_Model> list){
        if (list != null && list.size() > 0){
            this.mList.clear();
            this.mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Data_Model data = mList.get(position);

        ((ViewHolder)holder).getBinding().setVariable(BR.dataModel, data);
        ((ViewHolder)holder).getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        private ViewDataBinding getBinding() {
            return binding;
        }
    }
}
