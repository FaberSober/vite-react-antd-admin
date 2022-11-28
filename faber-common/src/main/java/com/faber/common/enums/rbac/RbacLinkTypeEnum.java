package com.faber.common.enums.rbac;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum RbacLinkTypeEnum implements IEnum<Integer> {
    INNER(1, "内部链接"),
    OUT(2, "外部链接");

    @JsonValue
    @EnumValue
    private final Integer value;
    private final String desc;

    RbacLinkTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
