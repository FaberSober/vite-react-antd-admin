package com.faber.common.biz;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.faber.common.annotation.SqlSorter;
import com.faber.common.annotation.SqlTreeId;
import com.faber.common.annotation.SqlTreeName;
import com.faber.common.annotation.SqlTreeParentId;
import com.faber.common.bean.BaseDelEntity;
import com.faber.common.constant.CommonConstants;
import com.faber.common.vo.Query;
import com.faber.common.util.TreeUtil;
import com.faber.common.vo.TreeNode;
import com.faber.common.vo.TreePathVo;
import com.faber.common.vo.TreePosChangeVo;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h3>Tree形结构数据的Service业务方法</h3>
 *
 * @param <M>
 * @param <T>
 */
public abstract class BaseTreeBiz<M extends BaseMapper<T>, T> extends BaseBiz<M, T> {

    /**
     * 增强Tree数据查询，有的表可能会有一些自定义字段限制Tree结构的获取，子类可以覆盖重写此方法，来增加自定义字段的查询条件。
     */
    protected void enhanceTreeQuery(QueryWrapper<T> wrapper) {}

    /**
     * 给定选中的value，返回value向上查找的节点路径[1, 1-1, 1-1-1]
     *
     * @param id
     * @return
     */
    public List<T> treePathLine(Serializable id) {
        if (id == null) return new ArrayList<>();
        T entity = getById(id);
        if (entity == null) return new ArrayList<>();

        List<T> list = new ArrayList<>();

        // 递归判断是否到达父节点，这里可以自定义
        if (!this.treeReachRootNode(entity)) {
            Serializable parentId = this.getEntityParentId(entity);
            list.addAll(treePathLine(parentId));
        }

        list.add(entity);
        return list;
    }

    /**
     * 返回根节点，默认parentId=-1{@link CommonConstants#ROOT}为根节点
     *
     * @param parentId
     * @return
     */
    public List<T> treeListLayer(Serializable parentId) {
        // 判断根节点
        if (parentId == null || ObjectUtil.equal(CommonConstants.ROOT + "", parentId.toString())) {
            return this.treeListLayerRoot(parentId);
        }
        // 其他节点
        return this.treeListLayerNormal(parentId);
    }

    /**
     * 返回parentId下还子节点数量
     *
     * @param parentId
     * @return
     */
    public long treeCountLayer(Serializable parentId) {
        if (parentId == null) return 0;
        // 判断根节点
        if (ObjectUtil.equal(CommonConstants.ROOT + "", parentId.toString())) {
            return this.treeCountLayerRoot(parentId);
        }
        // 其他节点
        return this.treeCountLayerNormal(parentId);
    }

    public TreePathVo<T> treeFindPath(Serializable id) {
        List<T> list = this.treePathLine(id);
        List<TreeNode<T>> tree = this.treeGetChildren(list, 0);

        TreePathVo<T> tTreePathVo = new TreePathVo<>();

        List<TreeNode<T>> nodeList = this.transEntityListToNodeList(list);
        tTreePathVo.setList(nodeList);

        tTreePathVo.setTree(tree);
        return tTreePathVo;
    }

    private List<TreeNode<T>> treeGetChildren(List<T> list, int nodeIndex) {
        if (nodeIndex >= list.size()) return null;

        // 当前选中的节点
        T bean = list.get(nodeIndex);

        // 获取选中的节点的兄弟节点
        List<T> childList = this.treeListLayer(this.getEntityParentId(bean));

        List<TreeNode<T>> nodeList = new ArrayList<>();
        childList.forEach(c -> {
            List<TreeNode<T>> children = null;
            // 递归获取选中节点的子节点
            if (ObjectUtil.equal(this.getEntityId(c), this.getEntityId(bean))) {
                children = this.treeGetChildren(list, nodeIndex + 1);
            }
            TreeNode<T> treeNode = this.transEntityToTreeNode(c);
            treeNode.setChildren(children);

            nodeList.add(treeNode);
        });

        return nodeList;
    }

    /**
     * 返回根节点List，可以子类覆盖重写
     *
     * @param parentId
     * @return
     */
    public List<T> treeListLayerRoot(Serializable parentId) {
        return this.treeListLayerNormal(parentId);
    }

    public long treeCountLayerRoot(Serializable parentId) {
        return this.treeCountLayerNormal(parentId);
    }

    public QueryWrapper<T> treeLayerNormalWrapper(Serializable parentId) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq(this.getTreeParentIdFieldColumnName(), parentId);
        this.enhanceTreeQuery(wrapper);
        wrapper.orderByAsc(this.getSortedFieldColumnName());
        return wrapper;
    }

    public List<T> treeListLayerNormal(Serializable parentId) {
        QueryWrapper<T> wrapper = this.treeLayerNormalWrapper(parentId);
        return super.list(wrapper);
    }

    public long treeCountLayerNormal(Serializable parentId) {
        QueryWrapper<T> wrapper = this.treeLayerNormalWrapper(parentId);
        return super.count(wrapper);
    }

    public List<TreeNode<T>> allTree() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        this.enhanceTreeQuery(wrapper);
        wrapper.orderByAsc(this.getSortedFieldColumnName());
        List<T> beanList = super.list(wrapper);
        return this.getMenuTree(beanList, CommonConstants.ROOT + "");
    }

    public List<TreeNode<T>> getTree(Map<String, Object> params) {
        Query query = new Query(params);
        QueryWrapper<T> wrapper = parseQuery(query);
        this.enhanceTreeQuery(wrapper);
        wrapper.orderByAsc(this.getSortedFieldColumnName());
        List<T> beanList = super.list(wrapper);
        return this.getMenuTree(beanList, CommonConstants.ROOT + "");
    }

    /**
     * 从指定节点，返回向下获取所有节点Tree
     * @param id 指定节点ID
     * @return
     */
    public List<TreeNode<T>> allTreeFromNode(Serializable id) {
        List<T> beanList = getAllChildrenFromNode(id);
        List<TreeNode<T>> list = this.getMenuTree(beanList, id);
        return list;
    }

    /**
     * 从指定节点，返回向下获取所有节点List
     * @param id 指定节点ID
     * @return
     */
    public List<T> getAllChildrenFromNode(Serializable id) {
        // 递归查询所有的子节点
        List<T> beanList = this.loopFindChildren(Collections.singletonList(id));
        // 讲查询的顶级结点加入到第一个位置
        beanList.add(0, super.getById(id));
        return beanList;
    }

    /**
     * 递归查找子节点，返回向下所有子节点数组
     * @param parentIds 父节点ID数组
     * @return
     */
    public List<T> loopFindChildren(List<Serializable> parentIds) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.in(getTreeParentIdFieldColumnName(), Arrays.asList(parentIds));
        this.enhanceTreeQuery(wrapper);
        List<T> beanList = super.list(wrapper);

        // 获取当前层级ID集合，作为下级查询的父节点ID集合
        List<Serializable> cParentIds = new ArrayList<>();
        if (beanList != null && !beanList.isEmpty()) {
            for (T o : beanList) {
                cParentIds.add(getEntityId(o));
            }
        }
        if (!cParentIds.isEmpty()) {
            beanList.addAll(this.loopFindChildren(cParentIds));
        }

        return beanList;
    }

    public void changePos(List<TreePosChangeVo> list) {
        if (list == null || list.isEmpty()) return;

        list.forEach(item -> {
            T bean = super.getById(item.getKey());

            ReflectUtil.setFieldValue(bean, this.getSortedFieldName(), item.getIndex());
            ReflectUtil.setFieldValue(bean, this.getTreeParentIdFieldName(), item.getPid());

            super.updateById(bean);
        });
    }

    public void moveUp(Serializable id) {
        T entity = getById(id);
        List<T> list = this.treeListLayerNormal(getEntityParentId(entity));
        List<Serializable> idList = list.stream().map(i -> getEntityId(i)).collect(Collectors.toList());
        int oldIndex = idList.indexOf(id);
        if (oldIndex == 0) return; // 无需移动

        list.add(oldIndex - 1, list.remove(oldIndex));
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            int sort = getEntitySortId(item);
            if (sort != i) {
                setSort(item, i);
                this.updateById(item);
            }
        }
    }

    public void moveDown(Serializable id) {
        T entity = getById(id);
        List<T> list = this.treeListLayerNormal(getEntityParentId(entity));
        List<Serializable> idList = list.stream().map(i -> getEntityId(i)).collect(Collectors.toList());
        int oldIndex = idList.indexOf(id);
        if (oldIndex == list.size() - 1) return; // 无需移动

        list.add(oldIndex + 1, list.remove(oldIndex));
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            int sort = getEntitySortId(item);
            if (sort != i) {
                setSort(item, i);
                this.updateById(item);
            }
        }
    }

    /**
     * 将list转换为tree形结构数据
     * @param beanList
     * @param root
     * @return
     */
    public List<TreeNode<T>> getMenuTree(List<T> beanList, Serializable root) {
        List<TreeNode<T>> trees = new ArrayList<>();
        TreeNode<T> treeNode = null;
        for (T entity : beanList) {
            treeNode = new TreeNode<T>();
            treeNode.setId(this.getEntityId(entity));
            treeNode.setParentId(this.getEntityParentId(entity));
            treeNode.setName(ObjectUtil.toString(this.getEntityName(entity)));
            // 判断节点是否还有子节点
            treeNode.setHasChildren(this.countChildren(beanList, this.getEntityId(entity)) > 0);
            treeNode.setSourceData(entity);
            trees.add(treeNode);
        }
        return TreeUtil.build(trees, root);
    }

    protected long countChildren(List<T> beanList, Serializable id) {
        return beanList.stream().filter(i -> getEntityParentId(i) == id).count();
    }

    /**
     * 判断是否到达根节点
     *
     * @param entity
     * @return
     */
    protected boolean treeReachRootNode(T entity) {
        return ObjectUtil.equal(ObjectUtil.toString(getEntityParentId(entity)), CommonConstants.ROOT + "");
    }

    /**
     * 设置entity的排序，为当前所属层级最大的sort+1
     * @param entity
     */
    protected void setNextSort(T entity) {
        ReflectUtil.setFieldValue(entity, getSortedFieldName(), getMaxSort(getEntityParentId(entity)) + 1);
    }

    /**
     * 设置entity的排序，为当前所属层级最大的sort+1
     * @param entity
     */
    protected void setSort(T entity, int sort) {
        ReflectUtil.setFieldValue(entity, getSortedFieldName(), sort);
    }

    /**
     * 获取最大的排序
     * @param parentId 父节点ID
     * @return
     */
    protected Integer getMaxSort(Object parentId) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq(getTreeParentIdFieldColumnName(), parentId);
        this.enhanceTreeQuery(wrapper);
        wrapper.orderByDesc(this.getSortedFieldColumnName());
        List<T> list = super.page(new Page<>(1, 1), wrapper).getRecords();
        if (list != null && list.size() > 0) {
            return getEntitySortId(list.get(0));
        }
        return -1;
    }

    /**
     * 返回实体的父节点，可以子类覆盖重写
     *
     * @param entity
     * @return
     */
    protected Serializable getEntityId(T entity) {
        return (Serializable) ReflectUtil.getFieldValue(entity, this.getTreeIdFieldName());
    }

    /**
     * 返回实体的父节点，可以子类覆盖重写
     *
     * @param entity
     * @return
     */
    protected Serializable getEntityParentId(T entity) {
        return (Serializable) ReflectUtil.getFieldValue(entity, this.getTreeParentIdFieldName());
    }

    /**
     * 返回实体的父节点，可以子类覆盖重写
     *
     * @param entity
     * @return
     */
    protected Object getEntityName(T entity) {
        return ReflectUtil.getFieldValue(entity, this.getTreeNameFieldName());
    }

    /**
     * 返回实体的父节点，可以子类覆盖重写
     *
     * @param entity
     * @return
     */
    protected Integer getEntitySortId(T entity) {
        Object sortId = ReflectUtil.getFieldValue(entity, this.getSortedFieldName());
        if (sortId == null) return 0;
        int sort = 0;
        try {
            sort = Integer.parseInt(ObjectUtil.toString(sortId));
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
        }
        return sort;
    }


    /**
     * 通用Tree类型数据向下查询
     *
     * @param id 要查询的根节点ID
     * @return 返回要查询的根节点向下所有节点的平铺List（包含id节点）
     */
    public List<T> findAllChildren(Serializable id) {
        List<T> list = new ArrayList<>();

        // 查询顶部节点
        T topItem = super.getById(id);
        if (topItem == null) return new ArrayList<>();

        list.add(topItem);

        Serializable topItemId = getEntityId(topItem);
        List<T> children = this.findChildren(topItemId);
        list.addAll(children);

        return list;
    }

    /**
     * 通用Tree类型数据向下递归查询
     *
     * @param parentId 要查询的父节点ID
     * @return 返回要查询的根节点向下所有节点的平铺List（不包含parentId节点）
     */
    public List<T> findChildren(Serializable parentId) {
        List<T> list = new ArrayList<>();

        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq(getTreeParentIdFieldColumnName(), parentId);

        List<T> children = super.list(wrapper);
        if (children != null && !children.isEmpty()) {
            list.addAll(children);
            children.forEach(child -> {
                Serializable childItemId = getEntityId(child);
                list.addAll(findChildren(childItemId));
            });
        }
        return list;
    }

    protected TreeNode<T> transEntityToTreeNode(T entity) {
        return this.transEntityToTreeNode(entity, true);
    }

    /**
     * 将实体T转换为TreeNode节点
     * @param entity 实体
     * @param countChildren 是否统计自节点数量
     * @return
     */
    protected TreeNode<T> transEntityToTreeNode(T entity, boolean countChildren) {
        TreeNode<T> treeNode = new TreeNode<>();
        treeNode.setId(this.getEntityId(entity));
        treeNode.setParentId(this.getEntityParentId(entity));
        treeNode.setName(ObjectUtil.toString(this.getEntityName(entity)));
        // 判断节点是否还有子节点
        if (countChildren) {
            treeNode.setHasChildren(this.treeCountLayer(this.getEntityId(entity)) > 0);
        }
        treeNode.setSourceData(entity);
        return treeNode;
    }

    public List<TreeNode<T>> transEntityListToNodeList(List<T> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return list.stream().map(this::transEntityToTreeNode).collect(Collectors.toList());
    }

    protected String getSortedFieldColumnName() {
        return StrUtil.toUnderlineCase(this.getAnnotationFieldName(SqlSorter.class));
    }

    protected String getSortedFieldName() {
        return this.getAnnotationFieldName(SqlSorter.class);
    }

    protected String getTreeIdFieldName() {
        return this.getAnnotationFieldName(SqlTreeId.class);
    }

    protected String getTreeParentIdFieldName() {
        return this.getAnnotationFieldName(SqlTreeParentId.class);
    }

    protected String getTreeParentIdFieldColumnName() {
        return StrUtil.toUnderlineCase(this.getAnnotationFieldName(SqlTreeParentId.class));
    }

    protected String getTreeNameFieldName() {
        return this.getAnnotationFieldName(SqlTreeName.class);
    }

    protected String getTreeNameFieldColumnName() {
        return StrUtil.toUnderlineCase(this.getAnnotationFieldName(SqlTreeName.class));
    }

    /**
     * 获取注解对应的实体字段名称
     *
     * @param annotationClass {@link SqlSorter}\{@link SqlTreeId}\{@link SqlTreeParentId}\{@link SqlTreeName}
     * @param <AT>
     * @return
     */
    protected <AT extends Annotation> String getAnnotationFieldName(Class<AT> annotationClass) {
        // 设置排序
        String findFieldName = null;
        for (Field field : getEntityClass().getDeclaredFields()) {
            AT annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                findFieldName = field.getName();
            }
        }
        if (findFieldName == null) {
            _logger.error("{}类未设置@{}注解，未能查找到排序字段，请确认代码。", getEntityClass().getName(), annotationClass.getName());
        }
        return findFieldName;
    }

}
