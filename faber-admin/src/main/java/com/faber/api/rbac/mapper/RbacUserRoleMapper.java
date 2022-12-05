package com.faber.api.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.faber.api.rbac.entity.RbacUserRole;
import com.faber.api.rbac.vo.RbacUserRoleRetVo;
import com.faber.api.rbac.vo.query.RbacUserRoleQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BASE-用户角色关联表
 * 
 * @author Farando
 * @email faberxu@gmail.com
 * @date 2022-09-19 11:40:40
 */
public interface RbacUserRoleMapper extends BaseMapper<RbacUserRole> {

    List<RbacUserRoleRetVo> pageVo(RbacUserRoleQueryVo query);

    int countByUserIdAndLinkUrl(@Param("userId") String userId, @Param("linkUrl") String linkUrl);
	
}