package com.vela.im.service.message.infrastructure.persistence.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vela.im.service.message.domain.entity.ImMessageBodyEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ImMessageBodyMapper extends BaseMapper<ImMessageBodyEntity> {
}
