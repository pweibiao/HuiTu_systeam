package com.peng.cssys.web.service;

import com.peng.cssys.constants.Constants;
import com.peng.cssys.base.LoginUser;
import com.peng.cssys.redis.RedisCache;
import com.peng.cssys.utils.ServletUtils;
import com.peng.cssys.utils.StringUtils;
import com.peng.cssys.utils.ip.AddressUtils;
import com.peng.cssys.utils.ip.IpUtils;
import com.peng.cssys.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token 验证
 */
@Component
public class TokenService {
    //令牌自定义标识
    @Value("${token.header}")
    private String header;
    //密钥
    @Value("${token.secret}")
    private String secret;
    //有效期
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    protected static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L ;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取请求token
     * @param httpServletRequest
     * @return
     */
    private String getToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)){
            token = token.replace(Constants.TOKEN_PREFIX,"");
        }
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private String getTokenKey(String uuid){
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 刷新令牌有效期
     * @param loginUser
     */
    public void refreshToken(LoginUser loginUser){
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime()+expireTime * MILLIS_MINUTE);
        //根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey,loginUser,expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取用户身份信息
     * @param httpServletRequest
     * @returnb
     */
    public LoginUser getLoginUser(HttpServletRequest httpServletRequest){

        //获取请求
        String token = getToken(httpServletRequest);
        if(StringUtils.isNotEmpty(token)){
            Claims claims = parseToken(token);
            //解析权限和用户信息
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            LoginUser user = redisCache.getCacheObject(userKey);
            return user;
        }
        return null;
    }

    /**
     * 设置用户身份信息
     * @param loginUser
     */
    public void setLoginUser(LoginUser loginUser){
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())){
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     * @param token
     */
    public void delLoginUser(String token){
        if(StringUtils.isNotEmpty(token)){
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 从数据生命生成令牌
     * @param claims
     * @return
     */
    public String createToken(Map<String,Object> claims){
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512,secret).compact();
        return token;
    }

    /**
     * 创建令牌
     * @param loginUser
     * @return
     */
    public String createToken(LoginUser loginUser){
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String,Object> claim = new HashMap<>();
        claim.put(Constants.LOGIN_USER_KEY,token);
        return createToken(claim);
    }

    /**
     * 验证令牌有效期，相差不足20分钟自动刷新
     * @param loginUser
     */
    public void verifyToken(LoginUser loginUser){
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if(expireTime - currentTime <= MILLIS_MINUTE_TEN){
            refreshToken(loginUser);
        }
    }

    public String getUsernameFromToken(String token){
        Claims claims = parseToken(token);
        return claims.getSubject();
    }


    /**
     * 设置用户代理信息
     * @param loginUser
     * @return
     */
    public void setUserAgent(LoginUser loginUser){
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }


}
