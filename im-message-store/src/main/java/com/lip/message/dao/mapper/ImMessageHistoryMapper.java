package com.lip.message.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lip.message.dao.ImMessageHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author: Elon
 * @title: ImMessageHistoryMapper
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/3 19:29
 */
@Repository
public interface ImMessageHistoryMapper extends BaseMapper<ImMessageHistoryEntity> {

    /**
     * 批量插入（mysql）
     * @param entityList
     * @return
     */
    Integer insertBatchSomeColumn(Collection<ImMessageHistoryEntity> entityList);

}
