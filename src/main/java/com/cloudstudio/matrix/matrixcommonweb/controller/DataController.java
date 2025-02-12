package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.model.StatisticsBean;
import com.cloudstudio.matrix.matrixcommonweb.service.StatisticService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName：DataController
 * @Author: matrix
 * @Date: 2024/12/20 21:30
 * @Description:数据控制类
 */
@Controller
@RequestMapping("/dataApi")
public class DataController {
    @Autowired
    StatisticService statisticService;

    private static final Gson gson=new Gson();
    private static final Gson gsonConfig= new GsonBuilder().serializeNulls().create();//强制返回空数据

    /**
     * 查询当日所有上传的数据统计
     * @param response
     * @throws IOException
     */
    @RequestMapping("queryCurrentStatistic")
    public void queryCurrentStatistic(HttpServletResponse response,
                                      @RequestParam("operator") String operator,
                                      @RequestParam("push_time") String push_time) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("operator",operator);
        resultMap.put("push_time",push_time);
        System.out.println(TimeUtil.GetTime(true)+" 查询请求参数:"+resultMap);
        List<StatisticsBean> resultList=statisticService.queryStatisticsResult(resultMap);
        if(resultList==null|| resultList.isEmpty()){
            System.out.println(TimeUtil.GetTime(true)+" 查询结果失败---");
            response.getWriter().write(gson.toJson(WebServerResponse.failure("查询结果失败")));
        }else{
            System.out.println(TimeUtil.GetTime(true)+" 查询结果成功"+resultList);
            response.getWriter().write(gson.toJson(WebServerResponse.success("查询结果成功",resultList)));
        }
    }

    /**
     * 写入数据库并返回所有当日统计结果
     * @param response
     * @param operator
     * @param numKey
     * @param numValue
     * @param numCount
     * @throws IOException
     */
    @RequestMapping("/pushNumber")
    public void pushNumber(HttpServletResponse response,
                           @RequestParam("operator") String operator,
                           @RequestParam("numKey") String numKey,
                           @RequestParam("numValue") int numValue,
                           @RequestParam("numCount") int numCount,
                           @RequestParam("push_time") String push_time) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        //String createTime=TimeUtil.GetTime(true);
        Map<String,Object> requestParamMap=new HashMap<>();
        requestParamMap.put("operator",operator);
        requestParamMap.put("num_key",numKey);
        requestParamMap.put("num_value",numValue);
        requestParamMap.put("num_count",numCount);
        requestParamMap.put("push_time",push_time);
        System.out.println(TimeUtil.GetTime(true)+" 请求参数:"+requestParamMap);
        StatisticsBean insertQueryBean=statisticService.pushQuery(requestParamMap);

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("operator",operator);
        resultMap.put("push_time",push_time);
        if(insertQueryBean==null){
            boolean insertKey=statisticService.insertStatisticsData(requestParamMap);
            if(insertKey){
                List<StatisticsBean> resultList=statisticService.queryStatisticsResult(resultMap);
                System.out.println(TimeUtil.GetTime(true)+" 更新次数成功---参数:"+requestParamMap+" 返回结果"+resultList);
                response.getWriter().write(gson.toJson(WebServerResponse.success("写入成功",resultList)));
            }else{
                System.out.println(TimeUtil.GetTime(true)+" 写入失败---参数:"+requestParamMap);
                response.getWriter().write(gson.toJson(WebServerResponse.failure("写入失败")));
            }
        }else{
            Map<String,Object> updateMap=new HashMap<>();
            updateMap.put("operator",operator);
            updateMap.put("num_key",numKey);
            updateMap.put("num_value",numValue);
            updateMap.put("num_count",Integer.parseInt(insertQueryBean.getNum_count())+numCount);
            updateMap.put("push_time",insertQueryBean.getPush_time());
            boolean updateKey=statisticService.updateStatisticsData(updateMap);
            if(updateKey){
                List<StatisticsBean> resultList=statisticService.queryStatisticsResult(resultMap);
                System.out.println(TimeUtil.GetTime(true)+" 更新次数成功---参数:"+updateMap+" 返回结果"+resultList);
                response.getWriter().write(gson.toJson(WebServerResponse.success("更新次数成功",resultList)));
            }else{
                System.out.println(TimeUtil.GetTime(true)+" 更新次数失败---参数:"+updateMap);
                response.getWriter().write(gson.toJson(WebServerResponse.failure("更新次数失败")));
            }
        }
    }
}
