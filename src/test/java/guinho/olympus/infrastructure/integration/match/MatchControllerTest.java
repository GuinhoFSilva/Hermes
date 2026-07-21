package guinho.olympus.infrastructure.integration.match;

import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.infrastructure.integration.IntegrationTest;
import guinho.olympus.infrastructure.integration.IntegrationTestContainers;
import guinho.olympus.infrastructure.persistence.JdbcMatchRepository;
import guinho.olympus.infrastructure.persistence.RedisQueueAdapter;
import guinho.olympus.infrastructure.security.JwtTokenExtractor;
import guinho.olympus.support.JwtTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@IntegrationTest
public class MatchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcMatchRepository matchRepository;

    @Autowired
    private RedisQueueAdapter queueAdapter;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", IntegrationTestContainers::getHost);

        registry.add("spring.data.redis.port",
                IntegrationTestContainers::getPort);
    }


    @Nested
    class Matches {

        @Test
        public void shouldReturnMatchById() throws Exception {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Match saved = matchRepository.save(Match.create(Participants.of(playerOne, playerTwo)));

            String token = JwtTestFactory.generateToken(playerOne.getValue());

            mockMvc.perform(
                            get("/v1/matches/" + saved.getId()).header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.matchId").value(saved.getId().toString()))
                    .andExpect(jsonPath("$.participants.firstPlayerId").value(playerOne.getValue().toString()))
                    .andExpect(jsonPath("$.participants.secondPlayerId").value(playerTwo.getValue().toString()))
                    .andExpect(jsonPath("$.status").value(saved.getStatus().toString()));
        }

        @Test
        public void shouldReturnOnlyAuthenticatedPlayerMatches() throws Exception {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            PlayerId playerThree = PlayerId.of(UUID.randomUUID());
            matchRepository.save(Match.create(Participants.of(playerOne, playerTwo)));
            matchRepository.save(Match.create(Participants.of(playerTwo, playerOne)));
            matchRepository.save(Match.create(Participants.of(playerTwo, playerThree)));

            String token = JwtTestFactory.generateToken(playerOne.getValue());

            mockMvc.perform(
                            get("/v1/matches/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        public void shouldReturnNoContentWhenAuthenticatedPlayerHasNoMatches() throws Exception {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());
            String token = JwtTestFactory.generateToken(playerId.getValue());

            mockMvc.perform(
                            get("/v1/matches/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }

        @Test
        public void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Match saved = matchRepository.save(Match.create(Participants.of(playerOne, playerTwo)));

            mockMvc.perform(
                            get("/v1/matches/" + saved.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldReturnForbiddenWhenPlayerIsNotParticipantOfMatch() throws Exception {
            PlayerId authenticatedPlayer = PlayerId.of(UUID.randomUUID());
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());
            Match saved = matchRepository.save(Match.create(Participants.of(playerOne, playerTwo)));

            String token = JwtTestFactory.generateToken(authenticatedPlayer.getValue());

            mockMvc.perform(
                            get("/v1/matches/" + saved.getId()).header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class QueueOperations {
        @BeforeEach
        void cleanRedis() {
            redisTemplate.delete("matchmaking");
        }

        @Test
        public void shouldReturnNoMatchWhenPlayerJoinsEmptyQueue() throws Exception {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());

            String token = JwtTestFactory.generateToken(playerId.getValue());

            mockMvc.perform(
                            post("/v1/matches/queue").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasMatch").value(false));
        }

        @Test
        public void shouldCreateMatchWhenPlayerJoinsQueueWithWaitingPlayer() throws Exception {
            PlayerId playerOne = PlayerId.of(UUID.randomUUID());
            PlayerId playerTwo = PlayerId.of(UUID.randomUUID());

            String token = JwtTestFactory.generateToken(playerTwo.getValue());

            queueAdapter.joinQueue(playerOne);

            mockMvc.perform(
                            post("/v1/matches/queue").header("Authorization", "Bearer " + token))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.hasMatch").value(true));
        }

        @Test
        public void shouldRemovePlayerFromQueueWhenPlayerIsWaiting() throws Exception {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());

            String token = JwtTestFactory.generateToken(playerId.getValue());

            queueAdapter.joinQueue(playerId);

            mockMvc.perform(
                            delete("/v1/matches/queue").header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }

        @Test
        public void shouldReturnConflictWhenPlayerIsNotInQueue() throws Exception {
            PlayerId playerId = PlayerId.of(UUID.randomUUID());

            String token = JwtTestFactory.generateToken(playerId.getValue());

            mockMvc.perform(
                            delete("/v1/matches/queue").header("Authorization", "Bearer " + token))
                    .andExpect(status().isConflict());
        }
    }
}
