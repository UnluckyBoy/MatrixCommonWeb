package com.cloudstudio.matrix.matrixcommonweb.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cloudstudio.matrix.matrixcommonweb.model.StatisticsBean;
import com.cloudstudio.matrix.matrixcommonweb.model.mapper.StatisticsMapper;
import com.cloudstudio.matrix.matrixcommonweb.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName：StatisticImpl
 * @Author: matrix
 * @Date: 2024/12/20 22:42
 * @Description:数据统计实现
 */
@Service("StatisticService")
public class StatisticImpl implements StatisticService {
    @Autowired
    StatisticsMapper statisticsMapper;

    @DS("mysql")
    @Override
    public StatisticsBean pushQuery(Map<String, Object> params) {
        return statisticsMapper.pushQuery(params);
    }

    @DS("mysql")
    @Override
    public List<StatisticsBean> queryStatisticsResult(Map<String, Object> params) {
        return statisticsMapper.queryStatisticsResult(params);
    }

    @DS("mysql")
    @Override
    public boolean insertStatisticsData(Map<String, Object> params) {
        return statisticsMapper.insertStatisticsData(params);
    }

    @DS("mysql")
    @Override
    public boolean updateStatisticsData(Map<String, Object> params) {
        return statisticsMapper.updateStatisticsData(params);
    }
}
