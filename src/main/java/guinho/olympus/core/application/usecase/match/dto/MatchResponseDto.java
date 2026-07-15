package guinho.olympus.core.application.usecase.match.dto;

import guinho.olympus.core.domain.match.enums.Status;
import guinho.olympus.core.domain.match.valueobject.Participants;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchResponseDto(UUID matchId, ParticipantsResponseDto participants, Status status, LocalDateTime createdAt, LocalDateTime endAt) {
}
