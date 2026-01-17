package com.cloudstudio.matrix.matrixcommonweb.service.Ai;

import com.cloudstudio.matrix.matrixcommonweb.model.AiBean.ChatRequest;
import com.cloudstudio.matrix.matrixcommonweb.webtool.Ai.BaiduApiUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName：AiServiceImpl
 * @Author: matrix
 * @Date: 2026/1/17 11:56
 * @Description:Ai服务实现类
 */
@Service("AiService")
public class AiServiceImpl implements AiService {
    @Autowired
    private BaiduApiUtil baiduApiUtil;

    /**
     * 聊天逻辑
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public WebServerResponse chat(ChatRequest request) throws IOException {
        if(request==null){
            System.out.println(TimeUtil.GetTime(true)+"收到请求--->>>参数异常!");
            return WebServerResponse.paramError();
        }
        String result=baiduApiUtil.callQianfanAPI(request.getContent());
        if(result==null){
            return WebServerResponse.failure("获取数据异常!");
        }
        System.out.println(TimeUtil.GetTime(true)+"收到请求--->>>参数:"+request+"返回结果:"+result);
        return WebServerResponse.success(result);
    }
}
