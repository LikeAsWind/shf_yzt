package com.atguigu.dao;

import com.atguigu.entity.Dict;

import java.util.List;

public interface DictDao extends BaseDao<Dict> {
    // 根据父id获取该节点下所有的子节点
    List<Dict> findListByParentId(Long parentId);

    // 根据父ID判断该节点是否是父节点
    Integer countIsParent(Long id);


    // 根据id获取name值
    String getNameById(Long id);

    // 根据编码获取Dict对象
    Dict getByDictCode(String dictCode);
}