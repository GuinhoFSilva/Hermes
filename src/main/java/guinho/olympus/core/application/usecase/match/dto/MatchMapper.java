package guinho.olympus.core.application.usecase.match.dto;

import guinho.olympus.core.domain.match.Match;

import java.util.List;

public class MatchMapper {
    public static MatchResponseDto toResponse(Match entity) {
        if(entity == null){
            return null;
        }

        return new MatchResponseDto(entity.getId(), entity.getParticipants(), entity.getStatus(), entity.getCreatedAt(), entity.getEndAt());
    }

    public static List<MatchResponseDto> toResponse(List<Match> entities) {
        return entities.stream().map(MatchMapper::toResponse).toList();
    }
}
