package com.tit.university.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 分页查询
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProgramQueryParam {

    private Integer currentPage = 1;
    private Integer pageSize = 10;
}
