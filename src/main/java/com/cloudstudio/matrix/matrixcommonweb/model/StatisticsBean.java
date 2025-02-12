package com.cloudstudio.matrix.matrixcommonweb.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName：StatisticsBean
 * @Author: matrix
 * @Date: 2024/12/20 22:13
 * @Description:数据统计基类
 */
@Data
public class StatisticsBean implements Serializable {
    private String operator;
    private String num_key;
    private String num_value;
    private String num_count;
    private String push_time;
    private String statistics_result;//统计计算结果
}
