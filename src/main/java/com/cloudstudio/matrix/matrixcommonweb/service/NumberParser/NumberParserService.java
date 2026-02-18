package com.cloudstudio.matrix.matrixcommonweb.service.NumberParser;

import com.cloudstudio.matrix.matrixcommonweb.model.requestBody.dataRequest.ParseRequest;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;

/**
 * @ClassName：NumberParserService
 * @Author: matrix
 * @Date: 2026/2/18 21:24
 * @Description:
 */
public interface NumberParserService {
    WebServerResponse parseNumber(ParseRequest request);
}
