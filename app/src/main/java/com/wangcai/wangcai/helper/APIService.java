package com.wangcai.wangcai.helper;


import com.wangcai.wangcai.bean.BankBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2017/1/10.
 */

public interface APIService {
//        String SERVER_IP = "http://10.1.6.245:8087/";
    String SERVER_IP = "https://site.wangcai.site/";


    /**
     * 注册
     *
     * @param phone
     * @param username
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("member/register")
    Call<Object> register(@Field("phone") String phone,
                          @Field("username") String username,
                          @Field("password") String password,
                          @Field("code") String code,
                          @Field("invitation_code") String invitation_code
    );


    /**
     * 登录
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

    /**
     * 绑定银行卡接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST("pay/bindcard")
    Call<Object> bindcard(@Field("token") String token,
                          @Field("code") String code,
                          @Field("card") String card,
                          @Field("truename") String truename,
                          @Field("bank") String bank,
                          @Field("idcard") String idcard,
                          @Field("bankcardimg") String bankcardimg,
                          @Field("idcardimg") String idcardimg
    );

    /**
     * 忘记密码
     *
     * @param phone    手机号
     * @param code     验证码
     * @param password 新密码
     * @return
     */
    @FormUrlEncoded
    @POST("member/forgetpwd")
    Call<Object> forgetpwd(@Field("phone") String phone,
                           @Field("code") String code,
                           @Field("password") String password);


    /**
     * 发送验证码接口
     *
     * @param phone 手机号
     * @param imei  手机识别号
     * @return
     */
    @FormUrlEncoded
    @POST("sms/send")
    Call<Object> send(@Field("phone") String phone,
                      @Field("imei") String imei);


    /**
     * 版本更新接口
     *
     * @return
     */
    @GET("system/versionupdate")
    Call<Object> versionupdate();

    /**
     * 获取银行接口
     *
     * @return
     */
    @GET("pay/getbanks")
    Call<BankBean> getbanks();

    /**
     * 通知
     *
     * @return
     */
    @FormUrlEncoded
    @POST("system/pushsum")
    Call<Object> pushsum(@Field("token") String token);

    /**
     * 弹窗
     *
     * @return
     */
    @FormUrlEncoded
    @POST("api/system/pop_notice")
    Call<Object> pop_notice(@Field("token") String token);

    /**
     * 上传图片接口
     *
     * @param part 上传的文件
     */
    @Multipart
    @POST("system/upload")
    Call<Object> uploadMemberIcon(@Part MultipartBody.Part part, @Part("token") RequestBody token,
                                  @Part("check_id_card") RequestBody check_id_card);
    /**
     * 上传图片接口
     *
     * @param part 上传的文件
     */
    @Multipart
    @POST("system/upload")
    Call<Object> uploadMemberIcon(@Part MultipartBody.Part part);
}