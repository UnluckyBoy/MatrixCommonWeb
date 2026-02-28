package com.cloudstudio.matrix.matrixcommonweb.service.NumberParser.Impl;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.dataRequest.ParseRequest;
import com.cloudstudio.matrix.matrixcommonweb.service.NumberParser.NumberParserService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser.MatrixNumberParser;
import com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser.NumberParser;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName：NumberParserServiceImpl
 * @Author: matrix
 * @Date: 2026/2/18 21:26
 * @Description:
 */
@Service("NumberParserService")
public class NumberParserServiceImpl implements NumberParserService {

    @Override
    public final WebServerResponse parseNumber(ParseRequest request) {
        // Map<Integer, Double> result = NumberParser.processMultiLineString(request.getInput());
        Map<Integer, Double> result = MatrixNumberParser.processMultiLineString(request.getInput());
        // NumberParser.printResult(result);
        if(!result.isEmpty()){
            System.out.println(TimeUtil.GetTime(true)+"解析成功");
            NumberParser.printResult(result);

            // 提取键和值到数组
            Integer[] keys = result.keySet().toArray(new Integer[0]);
            Double[] values = result.values().toArray(new Double[0]);
            // 封装响应数据
            Map<String, Object> mData =new HashMap<>();
            mData.put("keys", keys);
            mData.put("values", values);
            System.out.println(TimeUtil.GetTime(true)+"\t 返回封装数据:"+mData);
            return WebServerResponse.success(mData);
        }
        System.out.println(TimeUtil.GetTime(true)+"解析异常");
        return WebServerResponse.failure("解析异常");
    }
}
