package com.cloudstudio.matrix.matrixcommonweb.service;

import com.cloudstudio.matrix.matrixcommonweb.model.StatisticsBean;

import java.util.List;
import java.util.Map;

/**
 * @ClassName：StatisticService
 * @Author: matrix
 * @Date: 2024/12/20 22:41
 * @Description:数据统计服务接口
 */
public interface StatisticService {
    StatisticsBean pushQuery(Map<String, Object> params);

    List<StatisticsBean> queryStatisticsResult(Map<String, Object> params);

    boolean insertStatisticsData(Map<String, Object> params);
    boolean updateStatisticsData(Map<String, Object> params);
}
