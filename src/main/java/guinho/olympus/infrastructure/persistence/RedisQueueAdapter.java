package guinho.olympus.infrastructure.persistence;

import guinho.olympus.core.application.abstractions.QueueService;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.core.domain.shared.PlayerNotInQueueException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

public class RedisQueueAdapter implements QueueService {
    private final RedisTemplate<String, PlayerId> redisTemplate;

    public RedisQueueAdapter(RedisTemplate<String, PlayerId> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Participants> joinQueue(PlayerId playerId) {
        PlayerId playerOnQueue = redisTemplate.opsForList().leftPop("matchmaking");

        if (playerOnQueue == null) {
            redisTemplate.opsForList().rightPush("matchmaking", playerId);
            return Optional.empty();
        }

        Participants participants = Participants.of(playerOnQueue, playerId);

        return Optional.of(participants);
    }

    @Override
    public void leaveQueue(PlayerId playerId) {
        Long remove = redisTemplate.opsForList().remove("matchmaking", 1, playerId);

        if(remove == 0){
            throw new PlayerNotInQueueException();
        }
    }
}
