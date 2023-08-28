package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.hodor.admin.dto.PermitItem;
import java.util.List;

@Mapper
public interface PermitItemMapper {

    List<PermitItem> getAllPermitIterm();

    PermitItem selectById(String id);

    PermitItem selectByUri(String uri);

}
