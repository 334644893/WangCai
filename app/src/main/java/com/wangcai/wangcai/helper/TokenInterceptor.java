package com.wangcai.wangcai.helper;



import com.wangcai.wangcai.App;
import com.wangcai.wangcai.utils.DateUtil;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;

/**
 * Created by Administrator on 2017/3/28.
 */

public class TokenInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String errcode = "300";//TOKEN过期时的code
    Calendar cal = Calendar.getInstance();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        //获取自定义code
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String bodyString = buffer.clone().readString(charset);
        String code = GsonUtils.getStringV(bodyString, GsonUtils.ERROR_CODE);
        LogUtil.d("errcode---------->" + code);
        if (errcode.equals(code)) {
            //根据和服务端的约定判断token过期
            //同步请求方式，获取最新的Token
            final String username = SharedPreferencesUtils.getParam(App.getInstance(), "phone", "") + "";
            final String password = SharedPreferencesUtils.getParam(App.getInstance(), "token_password", "") + "";
            Call<Object> weather = App.getInstance().getTokenAPIService().login(
                    username,
                    password
            );
            retrofit2.Response<Object> execute = weather.execute();
            App.token = GsonUtils.getResultData(execute.body()).optString("token");
            LogUtil.d("-------重新获取的token---------", App.token + "时间" + cal.getTimeInMillis());
            //使用新的Token，创建新的请求
            // create a new request and modify it accordingly using the new token
            FormBody.Builder newFormBody = new FormBody.Builder();
            FormBody oidFormBody = (FormBody) request.body();
            for (int i = 0; i < oidFormBody.size(); i++) {
                if ("token".equals(oidFormBody.encodedName(i))) {
                    newFormBody.addEncoded(oidFormBody.encodedName(i), App.token);
                } else {
                    newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
                }
            }
            Request newRequest = chain.request()
                    .newBuilder()
                    .method(request.method(), newFormBody.build())
                    .build();

            LogUtil.d("-------再次请求的token---------", App.token + "时间" + cal.getTimeInMillis());
            response = chain.proceed(newRequest);
            //log
            ResponseBody responseBody1 = response.body();
            BufferedSource source1 = responseBody1.source();
            source1.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer1 = source1.buffer();
            Charset charset1 = UTF8;
            MediaType contentType1 = responseBody1.contentType();
            if (contentType != null) {
                charset1 = contentType1.charset(UTF8);
            }
            String bodyString1 = buffer1.clone().readString(charset1);
            LogUtil.d("时间----------->" + DateUtil.stampToDate(cal.getTimeInMillis() / 1000 + "", DateUtil.DATE_1));
            LogUtil.d("request---------->" + request);
            LogUtil.d("数据集2---------->" + bodyString1);
            return response;
        }
        //log
        ResponseBody responseBody1 = response.body();
        BufferedSource source1 = responseBody1.source();
        source1.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer1 = source1.buffer();
        Charset charset1 = UTF8;
        MediaType contentType1 = responseBody1.contentType();
        if (contentType != null) {
            charset1 = contentType1.charset(UTF8);
        }
        String bodyString1 = buffer1.clone().readString(charset1);
        LogUtil.d("时间----------->" + DateUtil.stampToDate(cal.getTimeInMillis() / 1000 + "", DateUtil.DATE_1));
        LogUtil.d("request---------->" + request);
        LogUtil.d("数据集---------->" + bodyString1);
        return response;
    }

}