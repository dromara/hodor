package org.dromara.hodor.admin.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.ApiConstants;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ResultUtil;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.KeySecret;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.exception.ValidationException;
import org.dromara.hodor.admin.service.SecretService;
import org.dromara.hodor.common.utils.DateUtils;
import org.dromara.hodor.common.utils.JSONUtils;
import org.dromara.hodor.common.utils.MD5Util;
import org.dromara.hodor.common.utils.MapSortUtils;
import org.dromara.hodor.common.utils.StringUtils;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseFilter implements Filter {

    private String[] excludedPaths = new String[] {};

    private final SecretService secretService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc) throws IOException, ServletException {
        boolean isExcludedPage = false;
        String contextPath = ((HttpServletRequest) req).getContextPath();
        for (String page : excludedPaths) {// 判断是否在过滤url之外
            if (((HttpServletRequest) req).getRequestURI().equals(contextPath + page)) {
                isExcludedPage = true;
                break;
            }
        }
        if (!isExcludedPage) {
            doFilter1(req, resp, fc);
            return;
        }
        fc.doFilter(req, resp);
        // 在过滤url之外
        /*try {
            if (((HttpServletRequest) req).getRequestURI().contains("/freeService")) {
                User user = new User();
                user.setUsername("admin");
                //user.setRoleId(0);
                ((HttpServletRequest) req).getSession().setAttribute(ServerConfigKeys.USER_SESSION, user);
                fc.doFilter(req, resp);
                ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
            } else {
                if (checkRequestLegal(req, resp)) {
                    User user = new User();
                    user.setUsername("admin");
                    //user.setRoleId(0);
                    ((HttpServletRequest) req).getSession().setAttribute(ServerConfigKeys.USER_SESSION, user);
                    fc.doFilter(req, resp);
                    ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
                }
            }
        } catch (Exception e) {
            log.error("doFilter error, msg {}", e.getMessage(), e);
            ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
            ajaxResponse(resp, ResultUtil.errorWithArgs(MsgCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }*/
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        excludedPaths = excludedPaths();
        String excludedPageStr = fConfig.getInitParameter("excludedPages");
        if (StringUtils.isNoneEmpty(excludedPageStr)) {
            String[] excludedPages = excludedPageStr.split(",");
            if (excludedPages.length > 0) {
                int dest = excludedPaths.length;
                int src = excludedPages.length;
                excludedPaths = Arrays.copyOf(excludedPaths, dest + src);
                System.arraycopy(excludedPages, 0, excludedPaths, dest, dest + src);
            }
        }
        init1(fConfig);
    }

    /**
     * 校验请求是否合法 原则为：
     * 1：请求方先把出参数key_app和sign以外的参数拼接起来，前缀我方生成的secret值进行md5加密，并发送请求
     * 2：我方验证分以下步骤
     * ------1：验证key_app值是否合法（即我方数据库是否存储该值）
     * ------2：验证时间是否过期（标准从开始请求到我方接受到请求不能超过60s）
     * ------3：验证加密的方式是否正确（secret+paramters）进行md5加密
     *
     * @param req  request
     * @param resp response
     * @return check result
     * @throws ValidationException check exception
     */
    private boolean checkRequestLegal(ServletRequest req, ServletResponse resp) throws ValidationException, ParseException, NoSuchAlgorithmException {
        String appKey = req.getParameter(ApiConstants.APP_KEY);
        String sign = req.getParameter(ApiConstants.SIGN);
        // 验证key_app值是否合法（即我方数据库是否存储该值）
        KeySecret keySecret = secretService.selectByKey(appKey);
        if (keySecret == null) {
            throw new ValidationException("没有找到相应的appKey 请求失败！");
        }
        // 验证时间是否过期（标准从开始请求到我方接受到请求不能超过60s）
        Date timeOut = DateUtils.parseByYYYYMMDDPatten(req.getParameter(ApiConstants.TIMEOUT));
        if (timeOut == null || Math.abs((new Date().getTime() - timeOut.getTime())) > 60000) {
            throw new ValidationException("请求时间超时！");
        }
        // 验证加密的方式是否正确（secret + parameters）进行md5加密
        Enumeration<String> paramNames = req.getParameterNames();
        Set<String> paramKeys = MapSortUtils.sortByKey(paramNames);
        StringBuilder sb = new StringBuilder(keySecret.getAppSecret());
        for (String paramName : paramKeys) {
            Object paramValue = req.getParameter(paramName);
            if (paramName.equals(ApiConstants.APP_KEY) || paramName.equals(ApiConstants.SIGN)
                || paramValue == null) {
                continue;
            }
            sb.append(paramName).append(paramValue);
        }
        String enSign = MD5Util.MD5Encode(sb.toString(), StandardCharsets.UTF_8);
        if (!Objects.requireNonNull(enSign).equals(sign)) {
            throw new ValidationException("验签不通过！");
        }
        return true;
    }

    /**
     * ajax响应
     */
    protected void ajaxResponse(ServletResponse response, Result<String> result) {
        try (PrintWriter out = response.getWriter();) {
            out.print(JSONUtils.toJsonStr(result));
        } catch (IOException e) {
            log.error("response error, {}", e.getMessage(), e);
        }

    }

    public abstract void doFilter1(ServletRequest req, ServletResponse resp, FilterChain fc) throws IOException,
        ServletException;

    public abstract void init1(FilterConfig fConfig) throws ServletException;

    /**
     * 获得Controller中有OpenApi注解的方法
     * 找出其路径加到过滤页数组中
     */
    private String[] excludedPaths() {
        List<String> excludePaths = new ArrayList<>();
        /*Map<String, Object> beanMap = SpringWorkFactory.getController();
        for (String key : beanMap.keySet()) {
            String classUrl = "";
            Class<?> clazz = AopTargetUtils.getTarget(beanMap.get(key)).getClass();
            RequestMapping classMapping = clazz.getAnnotation(RequestMapping.class);
            if (classMapping != null) {
                classUrl = classMapping.value()[0];
            }
            Method[] deckaredMethods = clazz.getDeclaredMethods();
            for (Method method : deckaredMethods) {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                if (methodMapping == null) {
                    continue;
                }
                OpenApi ann = method.getAnnotation(OpenApi.class);
                if (methodMapping.value().length > 0 && ann != null) {
                    for (String url : methodMapping.value()) {
                        excludePageList.add(ann.basePath() + classUrl + url + ann.suffix());
                    }
                }
            }
        }*/
        excludePaths.add("/login");
        excludePaths.add("/logout");
        final String[] strings = new String[excludePaths.size()];
        return excludePaths.toArray(strings);
    }

}
