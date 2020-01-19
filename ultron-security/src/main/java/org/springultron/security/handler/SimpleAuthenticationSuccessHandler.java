package org.springultron.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springultron.core.result.ApiResult;
import org.springultron.core.utils.Maps;
import org.springultron.core.utils.WebUtils;
import org.springultron.security.jwt.JwtProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 认证成功处理器
 *
 * @author brucewuu
 * @date 2020/1/10 11:06
 */
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;

    public SimpleAuthenticationSuccessHandler(JwtProcessor jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String token = jwtProcessor.generateToken(username);
        HashMap<String, String> hashMap = Maps.newHashMap(2);
        hashMap.put("token", token);
        WebUtils.renderJson(response, ApiResult.success(hashMap, "login success"));
    }
}
