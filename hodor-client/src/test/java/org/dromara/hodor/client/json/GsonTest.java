package org.dromara.hodor.client.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;

/**
 * @author tomgs
 * @since 2021/3/5
 */
public class GsonTest<T extends RequestBody> {

    public static void main(String[] args) {
        GsonTest<KillRunningJobRequest> gsonTest = new GsonTest<>();
        KillRunningJobRequest request = gsonTest.convert();
        System.out.println(request.getRequestId());
    }

    public T convert() {
        Gson gson = GsonUtils.getGson();
        String json = "{\"requestId\": 123}";
        Type type = new TypeToken<T>(){}.getType();
        T body = gson.fromJson(json, type);
        System.out.println(body);
        return body;
    }
}
