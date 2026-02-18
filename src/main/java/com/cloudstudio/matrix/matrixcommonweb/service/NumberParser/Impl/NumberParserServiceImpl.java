package com.cloudstudio.matrix.matrixcommonweb.service.NumberParser.Impl;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.dataRequest.ParseRequest;
import com.cloudstudio.matrix.matrixcommonweb.service.NumberParser.NumberParserService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser.NumberParser;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.stereotype.Service;

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
    public WebServerResponse parseNumber(ParseRequest request) {
        // 将多行字符串转换为 List<String>
        List<String> lines = request.getInput().lines().map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toList());
        // 调用 NumberParser 处理
        Map<Integer, Double> result= NumberParser.processStrings(lines);
        NumberParser.printResult(result);
        if(!result.isEmpty()){
            System.out.println(TimeUtil.GetTime(true)+"分割成功");
            NumberParser.printResult(result);
            return WebServerResponse.success(result);
        }
        System.out.println(TimeUtil.GetTime(true)+"分割异常");
        return WebServerResponse.failure("分割异常");
    }
}
