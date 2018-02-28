package com.github.eletransactionviewer.monthlyTransactions;

import android.util.Log;

import com.github.eletransactionviewer.BR;
import com.github.eletransactionviewer.BaseLayoutAdapter;
import com.github.eletransactionviewer.R;
import com.github.eletransactionviewer.model.MonthWiseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AT-Praveen on 28/02/18.
 */

public class MonthWiseAdaptor extends BaseLayoutAdapter {

    private List<MonthWiseModel> dataList;

    public MonthWiseAdaptor() {
        //Log.e("MonthWiseAdaptor", "constructor called");
        dataList = new ArrayList<>();
    }

    public void setDataList(List<MonthWiseModel> list) {
        if (list != null && list.size() > 0){
           // Log.e("MonthWiseAdapter", "setList : " + list.size());
            this.dataList.clear();
            this.dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getDataAtPosition(int position) {
       // Log.e("MonthWiseAdapter", "getDataAtPosition :  : " + position);
        return dataList.get(position);
    }

    @Override
    public int getLayoutIdForType(int viewType) {
        //Log.e("MonthWiseAdapter", "getLayoutIdForType :  : " + viewType);
        return R.layout.fragment_month_itemview;
    }

    @Override
    public int getBindedDataObjectResourceId() {
        return BR.dataModel;
    }


    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }
}
