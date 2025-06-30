package com.fftmback.authentication.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Profile("test")
public class DatabaseCleanup implements InitializingBean {

    public final static String VIEW_PREFIX = "v_";
    public final static String USER_VIEW = "v_user";
    public final static String USER_TABLE = "user_entity";
    private final static List<String> TABLES_NOT_TO_CLEAN = List.of("role");

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Transactional
    public void execute() {
        entityManager.flush();

        for (final String tableName : tableNames) {
            if (!tableName.startsWith(VIEW_PREFIX)) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE").executeUpdate();
            } else {
                if (tableName.equals(USER_VIEW)) {
                    entityManager.createNativeQuery("TRUNCATE TABLE " + USER_TABLE + " RESTART IDENTITY CASCADE").executeUpdate();
                } else {
                    entityManager.createNativeQuery("TRUNCATE TABLE " + tableName.substring(VIEW_PREFIX.length())
                            + " RESTART IDENTITY CASCADE").executeUpdate();
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Table.class) != null)
                .map(e -> e.getJavaType().getAnnotation(Table.class).name())
                .filter(tableName -> !TABLES_NOT_TO_CLEAN.contains(tableName))
                .toList();
    }
}
