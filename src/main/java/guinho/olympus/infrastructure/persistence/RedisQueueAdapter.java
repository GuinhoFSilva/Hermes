package guinho.olympus.infrastructure.persistence;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.core.application.usecase.match.shared.PlayerNotInQueueException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RedisQueueAdapter implements QueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_KEY = "matchmaking";
    private final RedisScript<List> script;
    private final RedisOperations<String, String> redisOperations;


    public RedisQueueAdapter(RedisTemplate<String, String> redisTemplate, RedisScript<List> script, RedisOperations<String, String> redisOperations) {
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.redisOperations = redisOperations;
    }

    @Override
    public Optional<Participants> joinQueue(PlayerId playerId) {
        List<String> players = redisOperations.execute(script, List.of(QUEUE_KEY), playerId.getValue().toString());

        if (players == null || players.size() != 2) {
            return Optional.empty();
        }

        return Optional.of(
                Participants.of(
                        PlayerId.of(UUID.fromString(players.getFirst())),
                        PlayerId.of(UUID.fromString(players.getLast()))
                )
        );
    }

    @Override
    public void leaveQueue(PlayerId playerId) {
        Long remove = redisTemplate.opsForList().remove(QUEUE_KEY, 1, playerId.getValue().toString());

        if (remove == 0) {
            throw new PlayerNotInQueueException();
        }
    }
}
