package com.att.popcornPalace.repositories;

import com.att.tdp.popcornPalace.models.Showtime;
import com.att.tdp.popcornPalace.repositories.ShowtimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeRepositoryTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Test
    void testFindOverlappingShowtimes_FoundOverlaps() {
        String theater = "Theater A";
        LocalDateTime startTime = LocalDateTime.of(2025, 3, 21, 18, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 3, 21, 20, 0);

        Showtime overlappingShowtime = new Showtime();
        when(showtimeRepository.findOverlappingShowtimes(theater, startTime, endTime))
                .thenReturn(List.of(overlappingShowtime));

        List<Showtime> result = showtimeRepository.findOverlappingShowtimes(theater, startTime, endTime);

        assertThat(result).isNotEmpty().hasSize(1);
        verify(showtimeRepository, times(1)).findOverlappingShowtimes(theater, startTime, endTime);
    }

    @Test
    void testFindOverlappingShowtimes_NoOverlaps() {
        String theater = "Theater B";
        LocalDateTime startTime = LocalDateTime.of(2025, 3, 21, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 3, 21, 14, 0);

        when(showtimeRepository.findOverlappingShowtimes(theater, startTime, endTime))
                .thenReturn(List.of());

        List<Showtime> result = showtimeRepository.findOverlappingShowtimes(theater, startTime, endTime);

        assertThat(result).isEmpty();
        verify(showtimeRepository, times(1)).findOverlappingShowtimes(theater, startTime, endTime);
    }
}
