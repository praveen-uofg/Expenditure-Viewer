package com.github.eletransactionviewer;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by AT-Praveen on 27/02/18.
 */

public abstract  class BaseLayoutAdapter<T>  extends RecyclerView.Adapter<BaseLayoutAdapter.ViewHolder>{
    //private final List<T> items = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj, int resId) {
            Log.e("BaseAdapter", "bind() : resId-" + resId);
            binding.setVariable(resId, obj);
            binding.executePendingBindings();
        }
    }

    public BaseLayoutAdapter() {
        Log.e("BaseLayoutAdapter", "constructor called");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("BaseAdapter", "onCreateViewHolder");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getLayoutIdForType(viewType), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BaseLayoutAdapter.ViewHolder holder, int position) {
        Log.e("BaseAdapter", "onBindViewHolder : position: " + position + "| data: " + getDataAtPosition(position).toString());
        holder.bind(getDataAtPosition(position), getBindedDataObjectResourceId());
    }


    public abstract Object getDataAtPosition(int position);

    public abstract int getLayoutIdForType(int viewType);

    public abstract int getBindedDataObjectResourceId();
}
