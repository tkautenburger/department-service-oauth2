package de.legendlime.departmentService.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to bootstrap the Redisson Redis client. The Redis URL comes
 * from the PkiProperties property class. This configuration is only loaded when
 * SSL is enabled
 * 
 * @author  Thomas Kautenburger
 * @version 1.0
 *
 */


@Configuration
@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
public class RedissonConfig {
	
	@Bean 
	@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
	public static RedissonClient reddissonClient(VaultPkiProperties pkiProperties) {
		Config config = new Config();
		config.useSingleServer().setAddress(pkiProperties.getRedisUrl());
		RedissonClient redisson = Redisson.create(config);
		return redisson;
	}
}
