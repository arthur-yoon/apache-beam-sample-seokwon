package com.seokwon.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

@Slf4j
public class ConnectionPoolProvider {

    private static final Object lock = new Object();
    private static ConnectionPoolProvider instance = null;
    DataSource dataSource;

    private ConnectionPoolProvider(DataSourceAuthDto dataSourceAuthDto) {
        this.dataSource = getHikariDataSource(dataSourceAuthDto);
    }

    public static ConnectionPoolProvider getInstance(DataSourceAuthDto dataSourceAuthDto) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConnectionPoolProvider(dataSourceAuthDto);
                }
            }
        }
        return instance;
    }

    public static DataSourceAuthDto getDataSourceAuthDto(String profile) {
        try {
            String root = System.getProperty("user.dir");
            Reader reader = new FileReader(root + "/dataSource.yml");

            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(reader);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(map.get(profile), DataSourceAuthDto.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private DataSource getHikariDataSource(DataSourceAuthDto dataSourceAuthDto) {

        HikariConfig config = new HikariConfig();

        // Configure which instance and what database user to connect with.
        config.setJdbcUrl(dataSourceAuthDto.getUrl());
        config.setUsername(dataSourceAuthDto.getUsername());
        config.setPassword(dataSourceAuthDto.getPassword());

        // maximumPoolSize limits the total number of concurrent connections this pool will keep.
        config.setMaximumPoolSize(15);
        // minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
        // Additional connections will be established to meet this value unless the pool is full.
        config.setMinimumIdle(5);
        // setConnectionTimeout is the maximum number of milliseconds to wait for a connection checkout.
        // Any attempt to retrieve a connection from this pool that exceeds the set limit will throw an
        // SQLException.
        config.setConnectionTimeout(10000); // 10 seconds
        // idleTimeout is the maximum amount of time a connection can sit in the pool. Connections that
        // sit idle for this many milliseconds are retried if minimumIdle is exceeded.
        config.setIdleTimeout(600000); //  10 minutes
        // maxLifetime is the maximum possible lifetime of a connection in the pool. Connections that
        // live longer than this many milliseconds will be closed and reestablished between uses. This
        // value should be several minutes shorter than the database's timeout value to avoid unexpected
        // terminations.
        config.setMaxLifetime(1800000); // 30 minutes


        return new HikariDataSource(config);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
