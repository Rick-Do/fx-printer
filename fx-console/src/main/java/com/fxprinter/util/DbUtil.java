package com.fxprinter.util;


import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.fx.app.entity.ProgramServerInfo;
import com.fx.app.entity.RocketMqConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-11 13:57:49
 * @since jdk1.8
 */
public class DbUtil {

    private static SqlSessionFactory sqlSessionFactory;
    private static HikariDataSource dataSource;

    // 初始化数据库连接池
    public static void initDatabase() {
        // 创建用户数据目录
        File dataDir = new File("data");
        if (!dataDir.exists()) dataDir.mkdirs();

        // 使用sqlite数据库
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:file:data/fxPrinter.db");
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
        createDatabaseSchema();

        // 配置 MyBatis-Plus
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        // 🔥 核心配置1：实体类扫描
        factory.setTypeAliasesPackage("com.fx.app.entity");
        // 🔥 核心配置3：MyBatis-Plus配置
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisConfiguration.getMapperRegistry().addMappers("com.fx.app.mapper");
        factory.setConfiguration(mybatisConfiguration);
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        factory.setGlobalConfig(globalConfig);
        try {
            sqlSessionFactory = factory.getObject();
        } catch (Exception e) {
            System.out.println("初始化数据库失败");
        }
    }

    private static void createDatabaseSchema() {
        try  {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            AutomationSchemaValidator validator = new AutomationSchemaValidator(jdbcTemplate);
            validator.registerEntity(ListUtil.toList(
                    ProgramServerInfo.class,
                    RocketMqConfig.class
            ));

        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static SqlSession openSession() {
        if (sqlSessionFactory == null) initDatabase();
        return sqlSessionFactory.openSession();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


}
