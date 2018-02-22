package com.wangcai.wangcai.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class BankBean {

    /**
     * error_code : 200
     * data : ["中国建设银行","中国工商银行"]
     */

    private String error_code;
    private List<String> data;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
