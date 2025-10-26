package com.tekton.challenge.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Hilos mínimos que se mantienen activos
        executor.setMaxPoolSize(5); // Máximo número de hilos en picos de carga
        executor.setQueueCapacity(100); // Capacidad de la cola de tareas pendientes
        executor.setKeepAliveSeconds(30); // Tiempo de vida de hilos ociosos
        executor.setThreadNamePrefix("AsyncLogger-"); //Prefijo útil para depuración
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //Política de rechazo (cuando el pool y la cola están llenos)
        executor.initialize();
        return executor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logExecutorDetails() {
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor();
        logger.info("Async Executor iniciado con {} hilos (máx: {}, cola: {})",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getThreadPoolExecutor().getQueue().remainingCapacity());
    }

}

