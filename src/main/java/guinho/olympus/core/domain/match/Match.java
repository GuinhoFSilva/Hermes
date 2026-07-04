package guinho.olympus.core.domain.match;

import guinho.olympus.core.domain.match.enums.Status;
import guinho.olympus.core.domain.shared.MatchAlreadyFinishedException;
import guinho.olympus.core.domain.shared.UnchangedFieldException;
import guinho.olympus.core.domain.match.valueobject.Participants;

import java.time.LocalDateTime;
import java.util.UUID;

public class Match {
    private final UUID id;
    private final Participants participants;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime endAt;

    private Match(UUID id, Participants participants, Status status, LocalDateTime createdAt, LocalDateTime endAt) {
        this.id = id;
        this.participants = participants;
        this.status = status;
        this.createdAt = createdAt;
        this.endAt = endAt;
    }

    public static Match create(Participants participants) {
       return new Match(UUID.randomUUID(), participants, Status.STARTED, LocalDateTime.now(), null);
    }

    public static Match reconstitute(UUID id, Participants participants, Status status, LocalDateTime createdAt, LocalDateTime endAt) {
       return new Match(id, participants, status, createdAt, endAt);
    }

    public void changeStatus(Status status) {
        if(this.status.equals(Status.FINISHED)) {
            throw new MatchAlreadyFinishedException();
        }
        if(status.equals(this.status)) {
            throw new UnchangedFieldException("The new status must be different from the current status");
        }
        if (status.equals(Status.FINISHED)) {
            this.endAt = LocalDateTime.now();
        }
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Participants getParticipants() {
        return participants;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", participants=" + participants +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", endAt=" + endAt +
                '}';
    }
}
