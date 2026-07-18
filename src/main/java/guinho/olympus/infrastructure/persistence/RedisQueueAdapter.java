package guinho.olympus.infrastructure.persistence;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.core.application.usecase.match.shared.PlayerNotInQueueException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RedisQueueAdapter implements QueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_KEY = "matchmaking";

    public RedisQueueAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Participants> joinQueue(PlayerId playerId) {
        String playerIdOnQueue = redisTemplate.opsForList().leftPop(QUEUE_KEY);

        if (playerIdOnQueue == null) {
            redisTemplate.opsForList().rightPush(QUEUE_KEY, playerId.getValue().toString());
            return Optional.empty();
        }

        PlayerId playerOnQueue = PlayerId.of(UUID.fromString(playerIdOnQueue));

        Participants participants = Participants.of(playerOnQueue, playerId);

        return Optional.of(participants);
    }

    @Override
    public void leaveQueue(PlayerId playerId) {
        Long remove = redisTemplate.opsForList().remove(QUEUE_KEY, 1, playerId.getValue().toString());

        if(remove == 0){
            throw new PlayerNotInQueueException();
        }
    }
}
