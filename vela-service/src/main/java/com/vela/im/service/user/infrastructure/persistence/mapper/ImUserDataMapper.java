package com.vela.im.service.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wanqiu
 */
@Mapper
public interface ImUserDataMapper extends BaseMapper<ImUserDataEntity> {
}
