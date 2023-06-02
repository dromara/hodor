package org.dromara.hodor.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.hodor.admin.domain.UserFeedback;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author tomgs
 * @since 1.0
 **/
@Mapper
public interface UserRecommendMapper {

    void insert(UserFeedback userFeedback);

    void delete(@Param("id")int id, @Param("userName")String userName);

    List<UserFeedback> selectByUserName(@Param("userName")String userName,
                                        @Param("pageOffset")int pageOffset,
                                        @Param("pageSize")int pageSize);

    long getSize(@Param("userName")String userName);
}
