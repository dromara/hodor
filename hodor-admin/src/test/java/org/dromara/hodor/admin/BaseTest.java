package org.dromara.hodor.admin;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * springboot测试基类
 *
 * @author tomgs
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BaseTest.class, HodorAdminApplication.class})
@DirtiesContext
public class BaseTest {

}
