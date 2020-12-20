package com.peng.cssys.web.service;

import com.peng.cssys.constants.Constants;
import com.peng.cssys.base.LoginUser;
import com.peng.cssys.exception.CustomException;
import com.peng.cssys.exception.user.CaptchaException;
import com.peng.cssys.exception.user.CaptchaExpireException;
import com.peng.cssys.exception.user.UserPasswordNotMatchException;
import com.peng.cssys.manager.AsyncManager;
import com.peng.cssys.manager.factory.AsyncFactory;
import com.peng.cssys.redis.RedisCache;
import com.peng.cssys.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登陆校验
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    /**
     * 登陆验证
     * @param username
     * @param password
     * @param code
     * @param uuid
     * @return
     */
    public String login(String username,String password , String code,String uuid){
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null ){
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)){
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
        //用户验证
        Authentication authentication = null;
        try {
            //该方法会去调用UserDetailServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username,password));
        }
        catch (Exception e) {
            if (e instanceof BadCredentialsException){
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,Constants.LOGIN_FAIL,MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,Constants.LOGIN_FAIL,e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,Constants.LOGIN_SUCCESS,MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //生成token
        return tokenService.createToken(loginUser);
    }
}
