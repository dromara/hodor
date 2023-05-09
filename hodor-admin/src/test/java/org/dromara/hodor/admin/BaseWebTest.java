package org.dromara.hodor.admin;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.Status;
import org.dromara.hodor.admin.core.UserContext;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.common.utils.JSONUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    protected ThreadLocal<User> USER_HOLDER = ThreadLocal.withInitial(this::mockUser);

    protected String postForm(String uri, Consumer<MockHttpServletRequestBuilder> paramsHandler) throws Exception {
        MockHttpServletRequestBuilder request = post(uri);
        paramsHandler.accept(request);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        String context = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertResult(context);
        return context;
    }

    private static void assertResult(String context) {
        log.info("request result：{}", context);
        JSONObject jsonObject = JSONUtils.parseObj(context);
        assertEquals(Status.SUCCESS.getCode(), jsonObject.getInt("code"));
    }

    protected String postJson(String uri, Object param) throws Exception {
        return postJson(uri, JSONUtils.toJsonStr(param));
    }

    protected String postJson(String uri, String paramJsonString) throws Exception {
        MockHttpServletRequestBuilder request = post(uri)
                .content(paramJsonString)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        String context = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertResult(context);
        return context;
    }


    protected String doGetResponse(String uri, Map<String, String> param) throws Exception {
        MockHttpServletRequestBuilder request = get(uri);
        request.sessionAttr(UserContext.USER_KEY, USER_HOLDER.get());
        param.forEach(request::param);
        String context = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertResult(context);
        return context;
    }

    protected String doGetResponse(String uri) throws Exception {
        return this.doGetResponse(uri, Collections.emptyMap());
    }

    protected User mockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        return mockUser;
    }

    @Resource
    private ConversionService conversionService;

    public Map<String, String> toUriParams(Object requestVo) {
        JSONObject json = JSONUtils.parseObj(requestVo);
        Map<String, String> params = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            params.put(entry.getKey(), conversionService.convert(entry.getValue(), String.class));
        }
        return params;
    }

}
