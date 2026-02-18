package com.cloudstudio.matrix.matrixcommonweb.controller.Common;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.dataRequest.ParseRequest;
import com.cloudstudio.matrix.matrixcommonweb.service.NumberParser.NumberParserService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.NumberPaeser.NumberParser;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName：ParserController
 * @Author: matrix
 * @Date: 2026/2/18 20:50
 * @Description:
 */
@Controller
@RequestMapping("/commonApi")
public class ParserController {
    @Autowired
    NumberParserService numberParserService;

    private static final Gson gson=new Gson();

    @PostMapping("/parse")
    public void getParse(HttpServletResponse response,@RequestBody ParseRequest request) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(numberParserService.parseNumber(request)));
    }
}
