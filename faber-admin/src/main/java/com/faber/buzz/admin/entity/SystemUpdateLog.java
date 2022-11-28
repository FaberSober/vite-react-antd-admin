package com.faber.buzz.admin.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.faber.common.annotation.FaModalName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * BASE-系统版本更新日志表
 * 
 * @author Farando
 * @email faberxu@gmail.com
 * @date 2022-08-17 17:10:02
 */
@FaModalName(name = "BASE-系统版本更新日志表")
@TableName("base_system_update_log")
@Data
public class SystemUpdateLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @ExcelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ExcelProperty("版本号")
    // @Column(name = "ver")
    private Integer ver;

    @ExcelProperty("版本编码")
    // @Column(name = "ver_no")
    private String verNo;

    @ExcelProperty("备注信息")
    // @Column(name = "remark")
    private String remark;

    @ExcelProperty("升级日期")
    // @Column(name = "crt_time")
    private Date crtTime;

}
