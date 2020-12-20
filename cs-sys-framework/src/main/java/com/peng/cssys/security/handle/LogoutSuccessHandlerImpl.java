package com.peng.cssys.security.handle;

import com.alibaba.fastjson.JSON;
import com.peng.cssys.constants.Constants;
import com.peng.cssys.constants.HttpStatus;
import com.peng.cssys.base.AjaxResult;
import com.peng.cssys.base.LoginUser;
import com.peng.cssys.manager.AsyncManager;
import com.peng.cssys.manager.factory.AsyncFactory;
import com.peng.cssys.utils.ServletUtils;
import com.peng.cssys.utils.StringUtils;
import com.peng.cssys.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 自定义退出处理类
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    /**
     * 退出操作
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            //删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            //记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatus.SUCCESS,"退出成功")));
    }
}
