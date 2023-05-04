package org.dromara.hodor.admin.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.Result;
import org.dromara.hodor.admin.core.ServerConfigKeys;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.service.impl.PermitService;

@Slf4j
@RequiredArgsConstructor
public class AuthorizeFilter implements Filter {

    private final PermitService permitService;

    private Map<String, Map<String, Boolean>> authCacheMap;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc)
            throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            if ("/login".equals(request.getPathInfo())) {
                fc.doFilter(req, resp);
                return;
            }
            Object userSessionObj = request.getSession().getAttribute(ServerConfigKeys.USER_SESSION);
            if (userSessionObj instanceof User) {
                User user = (User) userSessionObj;
                String username = user.getUsername();
                String permitItem = request.getPathInfo();
                if (authCacheMap.containsKey(username) && authCacheMap.get(username).containsKey(permitItem)) {
                    if (authCacheMap.get(username).get(permitItem) == Boolean.TRUE) {
                        fc.doFilter(req, resp);
                    } else {
                        backNoOperatePermit(username, permitItem, request, resp);
                    }
                } else {
                    boolean hasPermit = permitService.hasPermit(user, permitItem);
                    if (authCacheMap.containsKey(username)) {
                        authCacheMap.get(username).put(permitItem, hasPermit);
                    } else {
                        Map<String, Boolean> itemMap = new ConcurrentHashMap<String, Boolean>();
                        itemMap.put(permitItem, hasPermit);
                        authCacheMap.put(username, itemMap);
                    }
                    if (hasPermit) {
                        fc.doFilter(req, resp);
                    } else {
                        backNoOperatePermit(username, permitItem, request, resp);
                    }
                }
            }
        } else {
            fc.doFilter(req, resp);
        }
    }

    private void backNoOperatePermit(String username, String permitItem, HttpServletRequest request, ServletResponse resp) {
        try {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                ServletOutputStream os = resp.getOutputStream();
                os.write(new Gson().toJson(new Result(false, "NO OPERATION PERMISSION")).getBytes(ServerConfigKeys.CHARSET));
                os.flush();
            } else {
                request.getRequestDispatcher("/app/page/permit/nopermission").forward(request, resp);
            }
        } catch (ServletException | IOException e) {
            log.error("AuthorizeFilter backNoOperatePermit - " + e.getMessage());
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        Cache<String, Map<String, Boolean>> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(1000).build();
        authCacheMap = cache.asMap();
    }
}
