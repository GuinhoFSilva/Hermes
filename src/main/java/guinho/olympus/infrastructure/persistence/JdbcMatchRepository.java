package guinho.olympus.infrastructure.persistence;

import guinho.olympus.core.application.repository.MatchMutation;
import guinho.olympus.core.application.repository.MatchQuery;
import guinho.olympus.core.application.usecase.match.shared.ResourceNotFoundException;
import guinho.olympus.core.domain.match.Match;
import guinho.olympus.core.domain.match.enums.Status;
import guinho.olympus.core.domain.match.valueobject.Participants;
import guinho.olympus.core.domain.match.valueobject.PlayerId;
import guinho.olympus.infrastructure.persistence.exceptions.CorruptedDataException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcMatchRepository implements MatchQuery, MatchMutation {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_MATCH = "SELECT id, status, created_at, end_at FROM matches";

    private RowMapper<Match> returnRowMapper(Participants participants) {
        return ((rs, rowNum) -> Match.reconstitute(
                UUID.fromString(rs.getString("id")),
                participants,
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getObject("end_at", LocalDateTime.class)
        ));
    }

    public JdbcMatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    @Transactional
    public Match save(Match match) {
        List<PlayerId> players = List.of(match.getParticipants().getFirst(), match.getParticipants().getSecond());

        jdbcTemplate.update("INSERT INTO matches (id, status, created_at, end_at) VALUES (?, ?, ?, ?)", match.getId().toString(), match.getStatus().toString(), match.getCreatedAt(), match.getEndAt());

        jdbcTemplate.batchUpdate("INSERT INTO participants (id_match, id_player) VALUES (?, ?)", players, players.size(), (PreparedStatement ps, PlayerId player) -> {
            ps.setString(1, match.getId().toString());
            ps.setString(2, player.getValue().toString());
        });

        return match;
    }


    @Override
    public Match update(Match match) {
        int rows = jdbcTemplate.update("UPDATE matches SET status = ?, endAt = ? WHERE id = ?", match.getStatus().toString(), match.getEndAt(), match.getId().toString());

        if(rows == 0) throw new ResourceNotFoundException("Match not found");

        return match;
    }

    @Override
    public Optional<Match> findById(UUID id) {
        Participants participants = getParticipants(id);

        return jdbcTemplate.query(SELECT_MATCH + " WHERE id = ?", returnRowMapper(participants), id.toString()).stream().findFirst();
    }

    @Override
    public List<Match> findByPlayerId(PlayerId playerId) {
        List<UUID> matchesIds = jdbcTemplate.queryForList("SELECT m.id FROM matches m JOIN participants p ON p.id_match = m.id WHERE p.id_player = ?", UUID.class, playerId.getValue().toString());

        // TODO: Replace with a single query that reconstructs aggregates to avoid the N+1 query

        List<Match> matches = new ArrayList<>();
        for (UUID matchId : matchesIds) {
            Optional<Match> match = findById(matchId);
            match.ifPresent(matches::add);
        }

        return matches;
    }

    private Participants getParticipants(UUID matchId) {
        List<UUID> playerIds = jdbcTemplate.queryForList("SELECT id_player FROM participants WHERE id_match = ?", UUID.class, matchId.toString());

        if (playerIds.isEmpty() || playerIds.size() < 2) {
            throw new CorruptedDataException("Match exists but no valid participants were found");
        }

        return Participants.of(PlayerId.of(playerIds.getFirst()), PlayerId.of(playerIds.getLast()));
    }

}
