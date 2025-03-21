package com.att.popcornPalace.services;
import com.att.tdp.popcornPalace.exception.BusinessRuleViolationException;
import com.att.tdp.popcornPalace.exception.ResourceNotFoundException;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import com.att.tdp.popcornPalace.services.ShowtimeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Transactional
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Showtime showtime;
    @AfterEach
    public void cleanup() {
        movieRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        showtime = Showtime.builder()
                .id(1L)
                .movieId(1L)
                .price(100.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
                .build();
    }

    @Test
    void testGetShowtimeById_Success() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        Showtime foundShowtime = showtimeService.getShowtimeById(1L);

        assertNotNull(foundShowtime);
        assertEquals(1L, foundShowtime.getId());
    }

    @Test
    void testGetShowtimeById_NotFound() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> showtimeService.getShowtimeById(1L));
    }

    @Test
    void testDeleteShowtime_Success() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        doNothing().when(showtimeRepository).deleteById(1L);

        assertDoesNotThrow(() -> showtimeService.deleteShowtime(1L));
        verify(showtimeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteShowtime_NotFound() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> showtimeService.deleteShowtime(1L));
    }

    @Test
    void testAddShowtime_Success() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());
        when(showtimeRepository.save(any())).thenReturn(showtime);

        Showtime createdShowtime = showtimeService.addShowtime(showtime);
        assertNotNull(createdShowtime);
        assertEquals(1L, createdShowtime.getId());
    }

    @Test
    void testAddShowtime_MovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> showtimeService.addShowtime(showtime));
    }

    @Test
    void testAddShowtime_OverlappingShowtime() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of(showtime));

        assertThrows(BusinessRuleViolationException.class, () -> showtimeService.addShowtime(showtime));
    }

    @Test
    void testUpdateShowtime_Success() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());

        assertDoesNotThrow(() -> showtimeService.updateShowtime(1L, showtime));
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    @Test
    void testUpdateShowtime_NotFound() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> showtimeService.updateShowtime(1L, showtime));
    }

    @Test
    void testUpdateShowtime_OverlappingShowtime() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        Showtime overlappingShowtime = Showtime.builder()
                .id(2L)  // Different ID, same theater, overlapping times
                .movieId(1L)
                .theater("Theater 1")
                .startTime(showtime.getStartTime().minusMinutes(30))
                .endTime(showtime.getEndTime().plusMinutes(30))
                .price(50.0)
                .build();

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of(overlappingShowtime));

        assertThrows(BusinessRuleViolationException.class, () -> showtimeService.updateShowtime(1L, showtime));
    }

}
