package com.tallate.test.mysqlconfig;

import com.tallate.test.http.HttpInterceptor;
import com.tallate.test.keyprovider.SpanIdKeyProvider;
import com.tallate.tidp.idpchecker.IdpChecker;
import com.tallate.tidp.idpchecker.blocking.DefaultIdpChecker;
import com.tallate.tidp.keyprovider.KeyProvider;
import com.tallate.tidp.keystore.JdbcKeyStore;
import com.tallate.tidp.keystore.KeyStore;
import com.tallate.tidp.spring.IdpInterceptor;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在相应启动类中引入
 */
@Configuration
public class MySQLTidpConfig {

  @Bean
  public KeyProvider keyProvider() {
    return new SpanIdKeyProvider();
  }

  @Bean
  public KeyStore keyStore(DataSource dataSource) {
    return new JdbcKeyStore()
        .setDataSource(dataSource);
  }

  @Bean
  public IdpChecker idpChecker(KeyProvider keyProvider, KeyStore keyStore) {
    return new DefaultIdpChecker()
        .setKeyProvider(keyProvider)
        .setKeyStore(keyStore);
  }

  /**
   * 幂等切面
   */
  @Bean
  public IdpInterceptor idpInterceptor(IdpChecker idpChecker) {
    return new IdpInterceptor()
        .setIdpChecker(idpChecker);
  }

  @Bean
  public HttpInterceptor httpInterceptor() {
    return new HttpInterceptor();
  }

}
