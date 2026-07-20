package com.vela.im.service.interfaces.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.vela.im.shared.exception.BaseErrorCode;
import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.types.enums.GateWayErrorCode;
import com.vela.im.shared.exception.ApplicationExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <p>Title: GateWayInterceptor</p>
 * <p>Description: 网关拦截器，校验请求中的 userSign 签名，确保请求合法性。</p>
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
public class GateWayInterceptor implements HandlerInterceptor {

    private final IdentityCheck identityCheck;

    public GateWayInterceptor(IdentityCheck identityCheck) {
        this.identityCheck = identityCheck;
    }


    //appService -》im接口 -》 userSign
    //appService（gen userSig）

    /**
     * 请求前置拦截：校验 userSign 签名是否合法
     *
     * @return true 放行，false 拒绝并返回错误
     * @throws Exception 校验异常时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        if (1 == 1){
//            return true;
//        }

        //获取appId 操作人 userSign
        String appIdStr = request.getParameter("appId");
        if(StringUtils.isBlank(appIdStr)){
            resp(ResponseVO.errorResponse(GateWayErrorCode
            .APPID_NOT_EXIST),response);
            return false;
        }


        // 操作人
        String identifier = request.getParameter("identifier");
        if(StringUtils.isBlank(identifier)){
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .OPERATER_NOT_EXIST),response);
            return false;
        }

        // 用户签名数据
        String userSign = request.getParameter("userSign");
        if(StringUtils.isBlank(userSign)){
            resp(ResponseVO.errorResponse(GateWayErrorCode
                    .USERSIGN_NOT_EXIST),response);
            return false;
        }

        //签名和操作人和appid是否匹配
        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSig(identifier, appIdStr, userSign);
        if(applicationExceptionEnum != BaseErrorCode.SUCCESS){
            resp(ResponseVO.errorResponse(applicationExceptionEnum),response);
            return false;
        }

        return true;
    }


    private void resp(ResponseVO respVo ,HttpServletResponse response){

        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            String resp = JSONObject.toJSONString(respVo);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Credentials","true");
            response.setHeader("Access-Control-Allow-Methods","*");
            response.setHeader("Access-Control-Allow-Headers","*");
            response.setHeader("Access-Control-Max-Age","3600");

            writer = response.getWriter();
            writer.write(resp);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.checkError();
            }
        }

    }
}
