package com.room_reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.room_reservation.config.TestContainersConfig;
import com.room_reservation.domain.Room;
import com.room_reservation.repository.RoomRepository;
import com.room_reservation.repository.ReservationRepository;
import com.room_reservation.security.RequestUser;
import com.room_reservation.security.RequestUserHolder;
import com.room_reservation.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@Transactional
public class ReservationConcurrencyTest {

    static PostgreSQLContainer<?> postgres = TestContainersConfig.postgres;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Room testRoom;
    public record CreateReservationRequest(
            Long roomId,
            OffsetDateTime startAt,
            OffsetDateTime endAt
    ) {}
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testRoom = roomRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("테스트용 회의실을 찾을 수 없습니다"));
    }

    @Test
    void 동시예약방지_테스트() throws Exception {
        // Given: 동일한 시간대에 예약하려는 요청들
        OffsetDateTime startTime = OffsetDateTime.now().plusHours(1);
        OffsetDateTime endTime = startTime.plusHours(2);
        
        String requestBody = objectMapper.writeValueAsString(new CreateReservationRequest(
                testRoom.getId(),
                startTime,
                endTime
        ));

        // When: 10개의 병렬 요청 실행
        int numberOfRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfRequests; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    RequestUserHolder.set(new RequestUser(Role.USER, (long) userId));
                    
                    var result = mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "user-token-" + userId)
                            .content(requestBody));
                    
                    // 상태 코드가 201이면 성공
                    int statusCode = result.andReturn().getResponse().getStatus();
                    if (statusCode == 201) {
                        successCount.incrementAndGet();
                    } else if (statusCode == 400) {
                        //상태코드 400이고 해당 시간대에 예약이 있다면 
                        String responseBody = result.andReturn().getResponse().getContentAsString();
                        if (responseBody.contains("해당 시간대에 이미 예약이 있습니다")) {
                            failureCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    RequestUserHolder.clear();
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // Then: 정확히 1건만 성공한다.
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(9);
        assertThat( reservationRepository.count()).isEqualTo(1);
    }
}