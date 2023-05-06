package org.dromara.hodor.admin.filter;

import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.service.KeySecretService;
import org.dromara.hodor.common.utils.JSONUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoginFilter extends BaseFilter {

    public LoginFilter(KeySecretService keySecretService) {
        super(keySecretService);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter1(ServletRequest req, ServletResponse resp, FilterChain fc)
            throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest) req;
            Object attribute = httpServletRequest.getSession().getAttribute(ServerConfigKeys.USER_SESSION);
            if (attribute != null || "/login".equals(httpServletRequest.getPathInfo())) {
                fc.doFilter(req, resp);
            } else {
                if ("XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"))) {
                    ServletOutputStream os = resp.getOutputStream();
                    os.write(JSONUtils.toJsonStr(new Result<>(false, "登陆已失效"))
                            .getBytes(ServerConfigKeys.CHARSET));
                    os.flush();
                } else
                    httpServletRequest.getRequestDispatcher("/app/login").forward(req, resp);
            }
        } else {
            fc.doFilter(req, resp);
        }
    }


    @Override
    public void init1(FilterConfig fConfig) {

    }

}