package com.vela.im.shared.route.algorithm.consistenthash;



import com.vela.im.shared.types.enums.UserErrorCode;
import com.vela.im.shared.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private TreeMap<Long,String> treeMap = new TreeMap<>();

    private static final int NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {
        // 创建虚拟节点 保证哈希均匀
        for (int i = 0; i < NODE_SIZE; i++) {
            // 调用父类的哈希算法 虚拟节点
            treeMap.put(super.hash("node" + key +i),value);
        }
        // 存入哈希值和key
        treeMap.put(key,value);
    }

    
    @Override
    protected String getFirstNodeValue(String value) {

        Long hash = super.hash(value);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if(!last.isEmpty()){
            return last.get(last.firstKey());
        }

        if (treeMap.size() == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE) ;
        }

        return treeMap.firstEntry().getValue();
    }

    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}
