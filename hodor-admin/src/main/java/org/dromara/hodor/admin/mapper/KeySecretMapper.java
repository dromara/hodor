package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.hodor.admin.domain.KeySecret;

public interface KeySecretMapper {

    KeySecret selectByKey(@Param("key") String key);
}
