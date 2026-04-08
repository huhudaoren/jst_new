package com.ruoyi.jst.common.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 客户端配置
 * <p>
 * 决策 Q1=A：使用 Redisson 实现分布式锁。
 * 兼容性提示：Spring Boot 4.x + Redisson 3.41 若 starter 自动配置失败，
 * 本类作为手写 @Bean 兜底，可保证 Redisson 必然可用。
 * <p>
 * 配置项（沿用若依 spring.data.redis.* 节点）：
 * <pre>
 *   spring.data.redis.host
 *   spring.data.redis.port
 *   spring.data.redis.password
 *   spring.data.redis.database
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword((password == null || password.isEmpty()) ? null : password)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(10)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(10000)
                .setTimeout(3000)
                .setRetryAttempts(3);
        return Redisson.create(config);
    }
}
