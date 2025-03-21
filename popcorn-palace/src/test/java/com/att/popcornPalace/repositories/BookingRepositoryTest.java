package com.att.popcornPalace.repositories;


import com.att.tdp.popcornPalace.repositories.BookingRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.att.tdp.popcornPalace.PopcornPalaceApplication;
import com.att.tdp.popcornPalace.models.Movie;
import com.att.tdp.popcornPalace.repositories.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingRepositoryTest {

    @Mock
    private BookingRepository bookingRepository;

    @Test
    void testExistsByShowtimeIdAndSeatNumber_SeatExists() {
        Long showtimeId = 1L;
        int seatNumber = 10;

        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(true);

        boolean exists = bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);

        assertThat(exists).isTrue();
        verify(bookingRepository, times(1)).existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
    }

    @Test
    void testExistsByShowtimeIdAndSeatNumber_SeatDoesNotExist() {
        Long showtimeId = 2L;
        int seatNumber = 20;

        when(bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber)).thenReturn(false);

        boolean exists = bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);

        assertThat(exists).isFalse();
        verify(bookingRepository, times(1)).existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
    }
}