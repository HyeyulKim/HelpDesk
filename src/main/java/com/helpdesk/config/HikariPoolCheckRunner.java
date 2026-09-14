package com.helpdesk.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 애플리케이션 기동 시 DataSource가 실제로 HikariCP인지,
 * 풀이 정상 생성되어 커넥션을 물고 있는지 로그로 확인하기 위한 러너.
 * 확인 후에는 삭제하거나 프로필로 분리해도 됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HikariPoolCheckRunner implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        if (!(dataSource instanceof HikariDataSource)) {
            log.warn("DataSource가 HikariDataSource가 아닙니다. 실제 타입: {}", dataSource.getClass());
            return;
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        log.info("========== HikariCP 풀 상태 확인 ==========");
        log.info("Pool Name        : {}", hikariDataSource.getPoolName());
        log.info("Max Pool Size     : {}", hikariDataSource.getMaximumPoolSize());
        log.info("Minimum Idle      : {}", hikariDataSource.getMinimumIdle());
        log.info("Active Connections: {}", poolMXBean.getActiveConnections());
        log.info("Idle Connections  : {}", poolMXBean.getIdleConnections());
        log.info("Total Connections : {}", poolMXBean.getTotalConnections());
        log.info("Threads Awaiting  : {}", poolMXBean.getThreadsAwaitingConnection());
        log.info("=============================================");
    }
}
