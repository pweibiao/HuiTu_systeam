package com.peng.cssys.security.handle;

import com.alibaba.fastjson.JSON;
import com.peng.cssys.constants.HttpStatus;
import com.peng.cssys.base.AjaxResult;
import com.peng.cssys.utils.ServletUtils;
import com.peng.cssys.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestedSessionId());
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code,msg)));
    }
}
