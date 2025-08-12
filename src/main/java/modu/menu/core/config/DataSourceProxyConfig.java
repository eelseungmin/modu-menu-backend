package modu.menu.core.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class DataSourceProxyConfig {

    @Primary
    @Bean
    public DataSource dataSource(
            @Qualifier("actualDataSource") DataSource actualDataSource // 기존 DataSource 명시적으로 분리
    ) {
        return ProxyDataSourceBuilder
                .create(actualDataSource)
                .name("ProxyDataSource")
                .logQueryBySlf4j()
                .countQuery()
                .build();
    }

    @Bean
    public DataSource actualDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
