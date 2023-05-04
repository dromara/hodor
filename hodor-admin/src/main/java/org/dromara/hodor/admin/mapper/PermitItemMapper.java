package org.dromara.hodor.admin.mapper;

import org.dromara.hodor.admin.domain.PermitItem;
import java.util.List;

public interface PermitItemMapper {

    List<PermitItem> getAllPermitIterm();

    PermitItem selectById(String id);

    PermitItem selectByUri(String uri);

}
