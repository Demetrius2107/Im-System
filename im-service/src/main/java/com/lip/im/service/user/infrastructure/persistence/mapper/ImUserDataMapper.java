package com.lip.im.service.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lip.im.service.user.domain.entity.ImUserDataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wanqiu
 */
@Mapper
public interface ImUserDataMapper extends BaseMapper<ImUserDataEntity> {
}
