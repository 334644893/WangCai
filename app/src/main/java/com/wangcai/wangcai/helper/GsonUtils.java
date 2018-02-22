package com.wangcai.wangcai.helper;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/27.
 * error_code  错误码
 * data 数据集
 */

public class GsonUtils {
    public static final int SUCCESSFUL = 200;
    public static final int TOKEN_OVERDUE = 300;
    //    public static final String SUCCESSFUL="0";
    public static final String ERROR_CODE = "error_code";
    public static final String DATA = "data";
    public static final String ERRMSG = "errmsg";
    public static Gson gson = new Gson();

    public static JSONObject getResultData(Object responseBody) {
        JSONObject result = null;
        JSONObject resultdata = null;

        String json = gson.toJson(responseBody);
        try {
            result = new JSONObject(json);//转换为JSONObject
            resultdata = result.getJSONObject(DATA);//获取JSONArray

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultdata;
    }

    public static String getErrmsg(Object responseBody) {
        String Errmsg = "";

        try {
            Errmsg = new JSONObject(gson.toJson(responseBody)).optString(ERRMSG);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Errmsg;

    }

    public static int getError_code(Object responseBody) {
        int resultError_code = 0;

        try {
            resultError_code = new JSONObject(gson.toJson(responseBody)).optInt(ERROR_CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultError_code;

    }

    public static String getStringV(String jsonString, String v) {

        try {
            return new JSONObject(jsonString).optString(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String getMsg(Object responseBody) {
        String msg = "";

        try {
            msg = new JSONObject(gson.toJson(responseBody)).optString("msg");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;

    }

}
