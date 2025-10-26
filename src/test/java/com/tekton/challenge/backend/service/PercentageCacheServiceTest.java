package com.tekton.challenge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PercentageCacheServiceTest {
    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PercentageCacheService percentageCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Debe guardar correctamente un valor en el caché
    @Test
    void shouldSavePercentageInCache() {
        when(cacheManager.getCache("percentageCache")).thenReturn(cache);

        percentageCacheService.savePercentage(12.5);

        verify(cacheManager).getCache("percentageCache");
        verify(cache).put("latest", 12.5);
    }

    // No debe fallar si el cacheManager retorna null
    @Test
    void shouldNotFailIfCacheIsNullWhenSaving() {
        when(cacheManager.getCache("percentageCache")).thenReturn(null);

        assertDoesNotThrow(() -> percentageCacheService.savePercentage(15.0));

        verify(cacheManager).getCache("percentageCache");
        verifyNoMoreInteractions(cache);
    }

    // Debe devolver el valor guardado si está presente
    @Test
    void shouldReturnCachedPercentageIfPresent() {
        when(cacheManager.getCache("percentageCache")).thenReturn(cache);
        when(cache.get("latest", Double.class)).thenReturn(8.75);

        Optional<Double> result = percentageCacheService.getCachedPercentage();

        assertTrue(result.isPresent());
        assertEquals(8.75, result.get());
        verify(cache).get("latest", Double.class);
    }

    // Debe devolver Optional.empty() si el cache devuelve null
    @Test
    void shouldReturnEmptyIfCacheValueIsNull() {
        when(cacheManager.getCache("percentageCache")).thenReturn(cache);
        when(cache.get("latest", Double.class)).thenReturn(null);

        Optional<Double> result = percentageCacheService.getCachedPercentage();

        assertTrue(result.isEmpty());
    }

    // Debe devolver Optional.empty() si el cacheManager devuelve null
    @Test
    void shouldReturnEmptyIfCacheIsNullWhenGetting() {
        when(cacheManager.getCache("percentageCache")).thenReturn(null);

        Optional<Double> result = percentageCacheService.getCachedPercentage();

        assertTrue(result.isEmpty());
        verify(cacheManager).getCache("percentageCache");
        verifyNoInteractions(cache);
    }

    // Debe limpiar el cache si existe
    @Test
    void shouldClearCacheIfExists() {
        when(cacheManager.getCache("percentageCache")).thenReturn(cache);

        percentageCacheService.clearCache();

        verify(cacheManager).getCache("percentageCache");
        verify(cache).clear();
    }

    // No debe fallar si el cache es null al limpiar
    @Test
    void shouldNotFailIfCacheIsNullWhenClearing() {
        when(cacheManager.getCache("percentageCache")).thenReturn(null);

        assertDoesNotThrow(() -> percentageCacheService.clearCache());

        verify(cacheManager).getCache("percentageCache");
        verifyNoInteractions(cache);
    }
}