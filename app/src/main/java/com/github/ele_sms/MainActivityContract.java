package com.github.ele_sms;

import com.github.ele_sms.model.Data_Model;

import java.util.List;

/**
 * Created by AT-Praveen on 21/02/18.
 */

public interface MainActivityContract {
     interface Presenter {
        void fetchData();
        void onDestroy();
    }

     interface View {
        void showProgress();
        void showData(List<Data_Model> dataModelList);
        void showError();
    }

}
