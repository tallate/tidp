package com.tallate.test.redisconfig;

import com.tallate.test.http.HttpInterceptor;
import com.tallate.test.keyprovider.SpanIdKeyProvider;
import com.tallate.tidp.idpchecker.IdpChecker;
import com.tallate.tidp.idpchecker.blocking.DefaultIdpChecker;
import com.tallate.tidp.keyprovider.KeyProvider;
import com.tallate.tidp.keystore.KeyStore;
import com.tallate.tidp.keystore.RedisClient;
import com.tallate.tidp.keystore.RedisKeyStore;
import com.tallate.tidp.spring.IdpInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在相应启动类中引入
 */
@Configuration
public class RedisTidpConfig {

    @Bean
    public KeyProvider getKeyProvider() {
        return new SpanIdKeyProvider();
    }

    @Bean
    public KeyStore getKeyStore(RedisClient redisClient) {
        return new RedisKeyStore()
                .setRedisClient(redisClient);
    }

    @Bean
    public IdpChecker getIdpChecker(KeyProvider keyProvider, KeyStore keyStore) {
        return new DefaultIdpChecker()
                .setKeyProvider(keyProvider)
                .setKeyStore(keyStore);
    }

    /**
     * 幂等切面
     */
    @Bean
    public IdpInterceptor getIdpInter(IdpChecker idpChecker) {
        return new IdpInterceptor()
                .setIdpChecker(idpChecker);
    }

    @Bean
    public HttpInterceptor getIdpInter() {
        return new HttpInterceptor();
    }

}
