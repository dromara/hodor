package org.dromara.hodor.admin;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.entity.User;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * web测试基础类
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@AutoConfigureMockMvc
public class BaseWebTest extends BaseTest {

    @Resource
    protected MockMvc mvc;

    @Resource
    private ConversionService conversionService;

    protected ThreadLocal<User> USER_HOLDER = ThreadLocal.withInitial(this::mockUser);

    protected String postForm(String uri, Consumer<MockHttpServletRequestBuilder> paramsHandler) throws Exception {
        MockHttpServletRequestBuilder request = post(uri);
        paramsHandler.accept(request);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        String context = execute(request);
        assertResult(context);
        return context;
    }

    protected String postJson(String uri, Object param) throws Exception {
        return postJson(uri, Jsons.toJsonStr(param));
    }

    protected String postJson(String uri, String paramJsonString) throws Exception {
        MockHttpServletRequestBuilder request = post(uri)
            .content(paramJsonString)
            .contentType(MediaType.APPLICATION_JSON);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        String context = execute(request);
        assertResult(context);
        return context;
    }

    protected String getRequest(String uri, Map<String, String> param) throws Exception {
        MockHttpServletRequestBuilder request = get(uri);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        param.forEach(request::param);
        String context = execute(request);
        assertResult(context);
        return context;
    }

    protected String getRequest(String uri) throws Exception {
        return this.getRequest(uri, Collections.emptyMap());
    }

    protected String putRequest(String uri, Object obj) throws Exception {
        return putRequest(uri, Jsons.toJsonStr(obj));
    }
    protected String putRequest(String uri, String content) throws Exception {
        MockHttpServletRequestBuilder request = put(uri)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        String context = execute(request);
        assertResult(context);
        return context;
    }

    protected String deleteRequest(String uri) throws Exception {
        return deleteRequest(uri, Collections.emptyMap());
    }

    protected String deleteRequest(String uri, Map<String, String> param) throws Exception {
        MockHttpServletRequestBuilder request = delete(uri);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        param.forEach(request::param);
        String context = execute(request);
        assertResult(context);
        return context;
    }

    @NonNull
    private String execute(MockHttpServletRequestBuilder request) throws Exception {
        return mvc.perform(request)
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    private static void assertResult(String context) {
        log.info("request result：{}", context);
        JSONObject jsonObject = Jsons.parseObj(context);
        assertEquals(MsgCode.SUCCESS.getCode(), jsonObject.getInt("code"));
    }

    protected User mockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setTenantId(1L);
        mockUser.setUsername("tomgs");
        mockUser.setPassword("123");
        return mockUser;
    }

    protected Map<String, String> toUriParams(Object requestVo) {
        JSONObject json = Jsons.parseObj(requestVo);
        Map<String, String> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            params.put(entry.getKey(), conversionService.convert(entry.getValue(), String.class));
        }
        return params;
    }

}
