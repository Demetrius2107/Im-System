package com.hua.im.app.server.model.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SearchUserReq {

    @NotBlank(message = "关键字不能为空")
    private String keyWord;

    @NotNull(message = "搜索方式不能为空")
    private Integer searchType;

}
