package com.tit.university.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseQueryParam {

    private Integer currentPage = 1;


    private Integer pageSize = 10;

    private Long userId; // 教师端查询自己的数据

    private String status; // 按状态筛选
}

