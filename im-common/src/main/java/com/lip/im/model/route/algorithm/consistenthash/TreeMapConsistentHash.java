package com.lip.im.model.route.algorithm.consistenthash;



import com.lip.im.model.enums.UserErrorCode;
import com.lip.im.model.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @description:
 * @author: lld
 * @version: 1.0
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
