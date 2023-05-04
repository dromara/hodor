package org.dromara.hodor.admin.filter;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCodeEnum;
import org.dromara.hodor.admin.core.OpenApiConstants;
import org.dromara.hodor.admin.core.OpenApiResult;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.KeySecret;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.exception.ValidationException;
import org.dromara.hodor.admin.service.KeySecretService;
import org.dromara.hodor.common.utils.DateUtils;
import org.dromara.hodor.common.utils.MD5Util;
import org.dromara.hodor.common.utils.MapSortUtils;
import org.dromara.hodor.common.utils.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseFilter implements Filter {
    /**
     * 需要排除的页面
     */
    private String[] excludedPageArray;

    private final KeySecretService keySecretService;

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc) throws IOException, ServletException {
        boolean isExcludedPage = false;
        String contextPath = ((HttpServletRequest) req).getContextPath();
        for (String page : excludedPageArray) {// 判断是否在过滤url之外
            if (((HttpServletRequest) req).getRequestURI().equals(contextPath + page)) {
                isExcludedPage = true;
                break;
            }
        }
        if (!isExcludedPage) {
            doFilter1(req, resp, fc);
            return;
        }
        // 在过滤url之外
        try {
            if (((HttpServletRequest) req).getPathInfo().contains("/freeService")) {
                User user = new User();
                user.setUsername("admin");
                user.setRoleId(0);
                ((HttpServletRequest) req).getSession().setAttribute(ServerConfigKeys.USER_SESSION, user);
                fc.doFilter(req, resp);
                ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
            } else {
                if (checkRequestLegal(req, resp)) {
                    User user = new User();
                    user.setUsername("admin");
                    user.setRoleId(0);
                    ((HttpServletRequest) req).getSession().setAttribute(ServerConfigKeys.USER_SESSION, user);
                    fc.doFilter(req, resp);
                    ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
                }
            }
        } catch (ValidationException e) {
            ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
            log.error("请求出错", e);
        } catch (Exception e) {
            e.printStackTrace();
            ((HttpServletRequest) req).getSession().removeAttribute(ServerConfigKeys.USER_SESSION);
            OpenApiResult r = new OpenApiResult(MsgCodeEnum.RESPONSE_ERROR);
            ajaxResponse(resp, r);
        }
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        try {
            excludedPageArray = excludedPageArray();
        } catch (Exception e) {
            log.error("初始化出错", e);
        }

        String excludedPageStr = fConfig.getInitParameter("excludedPages");
        if (StringUtils.isNoneEmpty(excludedPageStr)) {
            String[] excludedPages = excludedPageStr.split(",");
            if (excludedPages.length > 0) {
                int dest = excludedPageArray.length;
                int src = excludedPages.length;
                excludedPageArray = Arrays.copyOf(excludedPageArray, dest + src);
                System.arraycopy(excludedPages, 0, excludedPageArray, dest, dest + src);
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
     * @param req request
     * @param resp response
     * @return check result
     * @throws ValidationException check exception
     */
    private boolean checkRequestLegal(ServletRequest req, ServletResponse resp) throws ValidationException, ParseException, NoSuchAlgorithmException {
        String appKey = req.getParameter(OpenApiConstants.APP_KEY);
        String sign = req.getParameter(OpenApiConstants.SIGN);
        // 验证key_app值是否合法（即我方数据库是否存储该值）
        KeySecret keySecret = keySecretService.selectByKey(appKey);
        if (keySecret == null) {
            OpenApiResult r = new OpenApiResult(MsgCodeEnum.INVALID_APPKEY);
            ajaxResponse(resp, r);
            throw new ValidationException("没有找到相应的appKey 请求失败！");
        }
        // 验证时间是否过期（标准从开始请求到我方接受到请求不能超过60s）
        Date timeOut = DateUtils.parseByYYYYMMDDPatten(req.getParameter(OpenApiConstants.TIMEOUT));
        if (timeOut == null || Math.abs((new Date().getTime() - timeOut.getTime())) > 60000) {
            OpenApiResult r = new OpenApiResult(MsgCodeEnum.REQUEST_TIMEOUT);
            ajaxResponse(resp, r);
            throw new ValidationException("请求时间超时！");
        }
        // 验证加密的方式是否正确（secret + parameters）进行md5加密
        Enumeration<String> paramNames = req.getParameterNames();
        Set<String> paramKeys = MapSortUtils.sortByKey(paramNames);
        StringBuilder sb = new StringBuilder(keySecret.getAppSecret());
        for (String paramName : paramKeys) {
            Object paramValue = req.getParameter(paramName);
            if (paramName.equals(OpenApiConstants.APP_KEY) || paramName.equals(OpenApiConstants.SIGN)
                    || paramValue == null) {
                continue;
            }
            sb.append(paramName).append(paramValue);
        }
        String enSign = MD5Util.MD5Encode(sb.toString(), StandardCharsets.UTF_8);
        if (!Objects.requireNonNull(enSign).equals(sign)) {
            OpenApiResult r = new OpenApiResult(MsgCodeEnum.INVALID_SIGN);
            ajaxResponse(resp, r);
            throw new ValidationException("验签不通过！");
        }
        return true;
    }

    /**
     * ajax响应
     */
    protected void ajaxResponse(ServletResponse response, OpenApiResult result) {
        try (PrintWriter out = response.getWriter();) {
            out.print(JSONUtil.toJsonStr(result));
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
    private String[] excludedPageArray() {
        List<String> excludePageList = new ArrayList<>();
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
        final String[] strings = new String[excludePageList.size()];
        return excludePageList.toArray(strings);
    }

}
