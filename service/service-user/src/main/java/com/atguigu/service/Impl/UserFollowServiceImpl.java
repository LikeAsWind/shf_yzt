package com.atguigu.service.Impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.BaseDao;
import com.atguigu.dao.UserFollowDao;
import com.atguigu.entity.UserFollow;
import com.atguigu.service.DictService;
import com.atguigu.service.UserFollowService;
import com.atguigu.vo.UserFollowVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = UserFollowService.class)
@Transactional
public class UserFollowServiceImpl extends BaseServiceImpl<UserFollow> implements UserFollowService {

    @Autowired
    private UserFollowDao userFollowDao;

    @Reference
    private DictService dictService;

    @Override
    protected BaseDao<UserFollow> getEntityDao() {
        return userFollowDao;
    }

    @Override
    public void follow(Long userId, Long houseId) {
        //创建一个UserFollow对象
        UserFollow userFollow = new UserFollow();
        userFollow.setUserId(userId);
        userFollow.setHouseId(houseId);
        //调用userFollowDao中添加的方法
        userFollowDao.insert(userFollow);

    }

    // 查询是否关注该房源的方法
    @Override
    public Boolean isFollowed(Long userId, Long houseId) {
        Integer count = userFollowDao.countByUserIdAndHouserId(userId, houseId);
        if (count.intValue() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public PageInfo<UserFollowVo> findListPage(int pageNum, int pageSize, Long userId) {
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        //调用userFollowDao中的分页方法
        Page<UserFollowVo> page = userFollowDao.findListPage(userId);
        //遍历userFollowVo
        for (UserFollowVo userFollowVo : page) {
            //获取房屋的户型
            String houseTypeName = dictService.getNameById(userFollowVo.getHouseTypeId());
            //获取楼层
            String floorName = dictService.getNameById(userFollowVo.getFloorId());
            //获取朝向
            String directionName = dictService.getNameById(userFollowVo.getDirectionId());
            userFollowVo.setHouseTypeName(houseTypeName);
            userFollowVo.setFloorName(floorName);
            userFollowVo.setDirectionName(directionName);

        }
        return new PageInfo<>(page, 5);
    }

  
    public void cancelFollowed(Long id) {
        //掉userfollowDao中删除的方法
        userFollowDao.delete(id);
    }


}
