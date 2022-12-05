package com.faber.api.admin.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.faber.core.annotation.FaModalName;
import com.faber.core.annotation.SqlEquals;
import com.faber.core.annotation.SqlSearch;
import com.faber.core.annotation.SqlSorter;
import com.faber.core.bean.BaseDelEntity;
import lombok.Data;


/**
 * 字典值
 */
@FaModalName(name = "字典值")
@TableName("base_dict")
@Data
public class Dict extends BaseDelEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @SqlEquals
    @ExcelProperty("字典分组")
    private Integer type;

    @SqlEquals
    @ExcelProperty("字典类型：0-文本/1-文件")
    private Integer category;

    @SqlSearch
    @ExcelProperty("字典文本")
    private String text;

    @SqlSearch
    @ExcelProperty("字典值")
    private String value;

    @ExcelProperty("颜色")
    private String color;

    @SqlSorter
    @ExcelProperty("排序")
    private Integer sort;

    @ExcelProperty("描述")
    private String description;

    @ExcelIgnore
    @TableField(exist = false)
    private DictType dictType;

}