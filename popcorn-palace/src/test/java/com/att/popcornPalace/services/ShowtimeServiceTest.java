package com.att.popcornPalace.services;
import com.att.tdp.popcornPalace.dto.ShowtimeRequestDto;
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
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        showtime = Showtime.builder()
                .id(1L)
                .movie(movie)
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
        // Create a movie object to mock the movie repository behavior
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        // Create the ShowtimeRequestDto object instead of Showtime entity
        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .movieId(1L) // movieId instead of movie reference
                .price(100.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
                .build();

        // Mock the behavior of movieRepository to return the movie object
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // Mock that there are no overlapping showtimes
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());

        // Mock the save method to return the showtime entity
        Showtime showtimeEntity = Showtime.builder()
                .id(1L)
                .movie(movie)  // Associate the movie entity here
                .price(100.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
                .build();

        when(showtimeRepository.save(any())).thenReturn(showtimeEntity);

        // Call the service method with the DTO
        Showtime createdShowtime = showtimeService.addShowtime(showtimeRequestDto);

        // Verify the result
        assertNotNull(createdShowtime);
        assertEquals(1L, createdShowtime.getId());
        assertEquals("Theater 1", createdShowtime.getTheater());
    }


    @Test
    void testAddShowtime_MovieNotFound() {

        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .movieId(1L) // movieId instead of movie reference
                .price(100.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
                .build();


        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> showtimeService.addShowtime(showtimeRequestDto));
    }

    @Test
    void testAddShowtime_OverlappingShowtime() {
        // Create a movie object to mock the movie repository behavior
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        // Create the ShowtimeRequestDto object instead of Showtime entity
        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .movieId(1L)  // movieId instead of movie reference
                .price(100.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 14, 0))
                .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
                .build();

        // Mock the behavior of movieRepository to return the movie object
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // Mock that there is an overlapping showtime in the same theater
        Showtime overlappingShowtime = Showtime.builder()
                .id(2L)
                .movie(movie)  // Link to the same movie
                .price(50.0)
                .theater("Theater 1")
                .startTime(LocalDateTime.of(2025, 3, 20, 13, 0))  // Overlapping time
                .endTime(LocalDateTime.of(2025, 3, 20, 15, 0))
                .build();

        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of(overlappingShowtime));

        // Verify that adding the showtime with overlapping times throws BusinessRuleViolationException
        assertThrows(BusinessRuleViolationException.class, () -> showtimeService.addShowtime(showtimeRequestDto));
    }


    @Test
    void testUpdateShowtime_Success() {
        // Arrange: Setup the movie and showtime data
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);
        Showtime showtimeToUpdate = Showtime.builder()
                .id(1L)
                .movie(movie)
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.of(2025, 3, 22, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 22, 20, 0))
                .build();

        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.of(2025, 3, 22, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 22, 20, 0))
                .movieId(1L)  // Assuming you map the movieId in the DTO
                .build();

        // Mock repository calls
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtimeToUpdate)); // Existing showtime
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie)); // Movie exists
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of()); // No overlap

        // Act: Perform the update
        assertDoesNotThrow(() -> showtimeService.updateShowtime(1L, showtimeRequestDto));

        // Assert: Verify that the save method was called once (indicating the update was successful)
        verify(showtimeRepository, times(1)).save(any(Showtime.class));
    }

    @Test
    void testUpdateShowtime_NotFound() {
        // Create a mock ShowtimeRequestDto (mimicking the request details)
        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .price(15.0)
                .theater("Dolby")
                .startTime(LocalDateTime.of(2025, 3, 23, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 23, 20, 30))
                .movieId(1L)  // Assuming you pass movieId in the request
                .build();

        // Mock the behavior of showtimeRepository to simulate that no showtime exists
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> showtimeService.updateShowtime(1L, showtimeRequestDto));
    }


    @Test
    void testUpdateShowtime_OverlappingShowtime() {
        // Create a movie object
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", 120, 8.5, 2010);

        // Create a showtime object with details to update
        Showtime showtimeToUpdate = Showtime.builder()
                .id(1L)
                .movie(movie)
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.of(2025, 3, 22, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 22, 20, 0))
                .build();

        // Create a ShowtimeRequestDto for the update
        ShowtimeRequestDto showtimeRequestDto = ShowtimeRequestDto.builder()
                .price(20.0)
                .theater("IMAX")
                .startTime(LocalDateTime.of(2025, 3, 22, 18, 0))
                .endTime(LocalDateTime.of(2025, 3, 22, 20, 0))
                .movieId(1L)  // Assuming movieId is passed in the request DTO
                .build();

        // Create an overlapping showtime with a different ID, but the same theater and overlapping times
        Showtime overlappingShowtime = Showtime.builder()
                .id(2L)  // Different ID, but same theater, overlapping times
                .movie(movie)  // Correctly set the Movie object here
                .theater("IMAX")  // Same theater
                .startTime(showtimeToUpdate.getStartTime().minusMinutes(30))  // Adjust start time
                .endTime(showtimeToUpdate.getEndTime().plusMinutes(30))      // Adjust end time
                .price(50.0)
                .build();

        // Mock the behavior of showtimeRepository to return the existing showtime
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtimeToUpdate));

        // Mock the behavior of movieRepository to return the movie object
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // Mock the showtimeRepository to return the overlapping showtime
        when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of(overlappingShowtime));

        // Act & Assert: Verify that an exception is thrown due to overlapping showtimes
        assertThrows(BusinessRuleViolationException.class, () -> showtimeService.updateShowtime(1L, showtimeRequestDto));
    }



}
