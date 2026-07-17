package guinho.olympus.infrastructure.persistence;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.core.domain.shared.PlayerNotInQueueException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisQueueAdapter implements QueueService {
    private final RedisTemplate<String, PlayerId> redisTemplate;
    private static final String QUEUE_KEY = "matchmaking";

    public RedisQueueAdapter(RedisTemplate<String, PlayerId> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Participants> joinQueue(PlayerId playerId) {
        PlayerId playerOnQueue = redisTemplate.opsForList().leftPop(QUEUE_KEY);

        if (playerOnQueue == null) {
            redisTemplate.opsForList().rightPush(QUEUE_KEY, playerId);
            return Optional.empty();
        }

        Participants participants = Participants.of(playerOnQueue, playerId);

        return Optional.of(participants);
    }

    @Override
    public void leaveQueue(PlayerId playerId) {
        Long remove = redisTemplate.opsForList().remove(QUEUE_KEY, 1, playerId);

        if(remove == 0){
            throw new PlayerNotInQueueException();
        }
    }
}
