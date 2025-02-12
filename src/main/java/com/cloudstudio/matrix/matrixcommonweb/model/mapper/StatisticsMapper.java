package com.cloudstudio.matrix.matrixcommonweb.model.mapper;

import com.cloudstudio.matrix.matrixcommonweb.model.StatisticsBean;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName：StatisticsMapper
 * @Author: matrix
 * @Date: 2024/12/20 22:15
 * @Description:
 */
@Service
@Mapper
@Repository
public interface StatisticsMapper {
    StatisticsBean pushQuery(Map<String, Object> params);//插入数据前查询是否存在

    List<StatisticsBean> queryStatisticsResult(Map<String, Object> params);//查询统计结果

    boolean insertStatisticsData(Map<String, Object> params);//写入数据库
    boolean updateStatisticsData(Map<String, Object> params);//更新出现次数
}
