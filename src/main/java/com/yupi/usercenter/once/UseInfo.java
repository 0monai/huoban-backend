package com.yupi.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UseInfo {
    /**
     * 成员昵称
     */
    @ExcelProperty("成员编号")
    private String  planetCode;

    /**
     * 用户昵称
     */

    @ExcelProperty("成员昵称")
    private String username;

}