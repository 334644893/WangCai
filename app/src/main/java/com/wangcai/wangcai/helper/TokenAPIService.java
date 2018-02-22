package com.wangcai.wangcai.helper;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/1/10.
 */
public interface TokenAPIService {
    /**
     * 登录接口获取新token
     *
     * @param phone
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("member/login")
    Call<Object> login(@Field("phone") String phone,
                       @Field("password") String password
    );
}