package org.dromara.hodor.admin.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.admin.domain.PermitItem;
import org.dromara.hodor.admin.mapper.PermitItemMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("dbPermitItemService")
public class PermitItemService {

    private final PermitItemMapper permitItemMapper;

    public List<PermitItem> getAllPermit() {
        return permitItemMapper.getAllPermitIterm();
    }

    public PermitItem getPermitById(String id) {
        return permitItemMapper.selectById(id);
    }

    public PermitItem getPermitByUri(String uri) {
        return permitItemMapper.selectByUri(uri);
    }

}
