package com.consultorio.oftalmologico.infraestructure.configuration;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MonitoreoIndices {
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void monitorearIndices() {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        log.info("Estadísticas de índices:");
        log.info("Búsquedas por índice: {}", stats.getQueryExecutionCount());
        log.info("Tiempo promedio de búsqueda: {}ms", stats.getQueryExecutionMaxTime());
    }
}
