package com.github.ele_sms;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ele_sms.model.Data_Model;

import java.util.List;

/**
 * Created by AT-Praveen on 20/02/18.
 */

public class LayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Data_Model> mList;

    LayoutAdapter(Context context, List<Data_Model> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Data_Model data = mList.get(position);

        ((ViewHolder)holder).getBinding().setVariable(BR.dataModel, data);
        ((ViewHolder)holder).getBinding().executePendingBindings();
        /*((ViewHolder)holder).mTransAmount.setText(data.getTranAmount());
        ((ViewHolder)holder).mTransDate.setText(data.getTransDate());
        ((ViewHolder)holder).mBankName.setText(data.getBankName());
        ((ViewHolder)holder).mSmsDate.setText(data.getSmsDate());
        ((ViewHolder)holder).mCardNumber.setText(data.getCardNumber());*/
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTransAmount;
        private TextView mTransDate;
        private TextView mBankName;
        private TextView mSmsDate;
        private TextView mCardNumber;

        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);/*
            mTransAmount = itemView.findViewById(R.id.trans_amount);
            mTransDate = itemView.findViewById(R.id.trans_date);
            mBankName = itemView.findViewById(R.id.bank_name);
            mSmsDate = itemView.findViewById(R.id.sms_date);
            mCardNumber = itemView.findViewById(R.id.card_number);*/
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
