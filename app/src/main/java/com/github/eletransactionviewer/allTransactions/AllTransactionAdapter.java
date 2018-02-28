package com.github.eletransactionviewer.allTransactions;

import android.util.Log;

import com.github.eletransactionviewer.BR;
import com.github.eletransactionviewer.BaseLayoutAdapter;
import com.github.eletransactionviewer.R;
import com.github.eletransactionviewer.model.Data_Model;
import com.github.eletransactionviewer.model.MonthWiseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AT-Praveen on 28/02/18.
 */

public class AllTransactionAdapter extends BaseLayoutAdapter {
    private List<Data_Model> dataList;
    public AllTransactionAdapter () {
        dataList = new ArrayList<>();
    }

    public void setDataList(List<Data_Model> list) {
        if (list != null && list.size() > 0){
            this.dataList.clear();
            this.dataList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    public Object getDataAtPosition(int position) {
        return dataList.get(position);
    }

    @Override
    public int getLayoutIdForType(int viewType) {
        return R.layout.fragment_all_transaction_itemview;
    }

    @Override
    public int getBindedDataObjectResourceId() {
        return BR.allTransactionData;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }
}
