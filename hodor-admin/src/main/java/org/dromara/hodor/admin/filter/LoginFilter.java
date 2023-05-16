package org.dromara.hodor.admin.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.SecretService;
import org.dromara.hodor.common.utils.JSONUtils;

public class LoginFilter extends BaseFilter {

    public LoginFilter(SecretService secretService) {
        super(secretService);
    }

    @Override
    public void doFilter1(ServletRequest req, ServletResponse resp, FilterChain fc)
        throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            Object attribute = httpServletRequest.getSession().getAttribute(ServerConfigKeys.USER_SESSION);
            if (attribute != null || httpServletRequest.getRequestURI().contains("/login")) {
                if (attribute instanceof User) {
                    UserContext.setUser((User) attribute);
                }
                fc.doFilter(req, resp);
            } else {
                if ("XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"))) {
                    ServletOutputStream os = resp.getOutputStream();
                    os.write(JSONUtils.toJsonStr(ResultUtil.error(MsgCode.LOGIN_EXPIRED))
                        .getBytes(ServerConfigKeys.CHARSET));
                    os.flush();
                } else {
                    httpServletRequest.getRequestDispatcher("/app/login").forward(req, resp);
                }
            }
        } else {
            fc.doFilter(req, resp);
        }
    }


    @Override
    public void init1(FilterConfig fConfig) {

    }

}
