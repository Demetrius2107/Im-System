package com.vela.im.service.interfaces.interceptor;

import com.alibaba.fastjson.JSONObject;

import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.service.user.domain.service.ImUserService;
import com.vela.im.shared.exception.BaseErrorCode;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.config.AppConfig;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.types.enums.GateWayErrorCode;
import com.vela.im.shared.types.enums.ImUserTypeEnum;
import com.vela.im.shared.exception.ApplicationExceptionEnum;
import com.vela.im.shared.utils.SigAPI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: IdentityCheck</p>
 * <p>Description: 用户签名校验工具，校验 userSign 的合法性及有效期，防止签名伪造。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2025-03-06
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Component
public class IdentityCheck {

    private static final Logger logger = LoggerFactory.getLogger(IdentityCheck.class);

    private final ImUserService imUserService;
    private final AppConfig appConfig;
    private final StringRedisTemplate stringRedisTemplate;

    public IdentityCheck(ImUserService imUserService,
                         AppConfig appConfig,
                         StringRedisTemplate stringRedisTemplate) {
        this.imUserService = imUserService;
        this.appConfig = appConfig;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 校验 userSign 签名
     * <p>检查缓存签名 → 解密 userSig → 验证 appId/identifier/过期时间 → 缓存结果。</p>
     *
     * @param identifier 用户标识
     * @param appId      应用ID
     * @param userSig    用户签名
     * @return 校验结果（SUCCESS 或 具体错误码）
     */
    // 用户鉴权
    public ApplicationExceptionEnum checkUserSig(String identifier,
                                                 String appId, String userSig){

        String cacheUserSig = stringRedisTemplate.opsForValue()
                .get(appId + ":" + Constants.RedisConstants.userSign + ":"
                + identifier + userSig);
        if(!StringUtils.isBlank(cacheUserSig) && Long.valueOf(cacheUserSig)
         >  System.currentTimeMillis() / 1000){
            this.setIsAdmin(identifier,Integer.valueOf(appId));
            return BaseErrorCode.SUCCESS;
        }

        //获取秘钥
        String privateKey = appConfig.getPrivateKey();

        //根据appid + 秘钥创建sigApi
        SigAPI sigAPI = new SigAPI(Long.valueOf(appId), privateKey);

        //调用sigApi对userSig解密
        JSONObject jsonObject = sigAPI.decodeUserSig(userSig);

        //取出解密后的appid 和 操作人 和 过期时间做匹配，不通过则提示错误
        Long expireTime = 0L;
        Long expireSec = 0L;
        Long time = 0L;
        String decoerAppId = "";
        String decoderidentifier = "";

        try {
            decoerAppId = jsonObject.getString("TLS.appId");
            decoderidentifier = jsonObject.getString("TLS.identifier");
            String expireStr = jsonObject.get("TLS.expire").toString();
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            time = Long.valueOf(expireTimeStr);
            expireSec = Long.valueOf(expireStr);
            expireTime = Long.valueOf(expireTimeStr) + expireSec;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("checkUserSig-error:{}",e.getMessage());
        }

        if(!decoderidentifier.equals(identifier)){
            return GateWayErrorCode.USERSIGN_OPERATE_NOT_MATE;
        }

        if(!decoerAppId.equals(appId)){
            return GateWayErrorCode.USERSIGN_IS_ERROR;
        }

        if(expireSec == 0L){
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        if(expireTime < System.currentTimeMillis() / 1000){
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        //appid + "xxx" + userId + sign
        String genSig = sigAPI.genUserSig(identifier, expireSec,time,null);
        if (genSig.toLowerCase().equals(userSig.toLowerCase()))
        {
            String key = appId + ":" + Constants.RedisConstants.userSign + ":"
                    +identifier + userSig;

            Long etime = expireTime - System.currentTimeMillis() / 1000;
            stringRedisTemplate.opsForValue().set(
                    key,expireTime.toString(),etime, TimeUnit.SECONDS
            );
            this.setIsAdmin(identifier,Integer.valueOf(appId));
            return BaseErrorCode.SUCCESS;
        }

        return GateWayErrorCode.USERSIGN_IS_ERROR;
    }


    /**
     * 根据appid,identifier判断是否App管理员,并设置到RequestHolder
     * @param identifier
     * @param appId
     * @return
     */
    public void setIsAdmin(String identifier, Integer appId) {
        //去DB或Redis中查找, 后面写
        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(identifier, appId);
        if(singleUserInfo.isOk()){
            RequestHolder.set(singleUserInfo.getData().getUserType() == ImUserTypeEnum.APP_ADMIN.getCode());
        }else{
            RequestHolder.set(false);
        }
    }
}
