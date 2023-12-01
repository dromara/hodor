package org.dromara.hodor.admin.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class KeySecret {

    private String appKey;

    private String appSecret;

}
