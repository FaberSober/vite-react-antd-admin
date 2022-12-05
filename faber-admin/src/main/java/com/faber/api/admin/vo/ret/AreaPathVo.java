package com.faber.api.admin.vo.ret;

import com.faber.api.admin.entity.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaPathVo implements Serializable {
    List<Area> list;
    List<AreaTree> tree;
}