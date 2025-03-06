package com.lip.im.imservice.user.controller;


import com.lip.im.imservice.user.model.req.DeleteUserReq;
import com.lip.im.imservice.user.model.req.ImportUserReq;
import com.lip.im.imservice.user.model.resp.ImportUserResp;
import com.lip.im.imservice.user.service.ImUserService;
import com.lip.im.model.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shukun.Li
 */
@RestController
@RequestMapping("v1/user")
public class ImUserController {

    @Autowired
    ImUserService imUserService;

    @PostMapping("/import")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req, Integer appId) {
        return imUserService.importUser(req);
    }

    @DeleteMapping("/delete")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

}
