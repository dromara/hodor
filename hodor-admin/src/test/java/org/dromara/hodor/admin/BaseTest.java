package org.dromara.hodor.admin;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * springboot测试基类
 *
 * @author tomgs
 * @since 1.0
 */
@SpringBootTest
@DirtiesContext
@ExtendWith(SpringExtension.class)
public class BaseTest {

}
