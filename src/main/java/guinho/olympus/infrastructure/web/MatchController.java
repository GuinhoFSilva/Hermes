package guinho.olympus.infrastructure.web;

import guinho.olympus.core.application.usecase.match.GetMatchUseCase;
import guinho.olympus.core.application.usecase.match.GetPlayerMatchesUseCase;
import guinho.olympus.core.application.usecase.match.JoinQueueUseCase;
import guinho.olympus.core.application.usecase.match.LeaveQueueUseCase;
import guinho.olympus.core.application.usecase.match.dto.JoinQueueResult;
import guinho.olympus.core.application.usecase.match.dto.MatchResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/matches")
public class MatchController {
    private final GetMatchUseCase getMatchUseCase;
    private final GetPlayerMatchesUseCase getPlayerMatchesUseCase;
    private final JoinQueueUseCase joinQueueUseCase;
    private final LeaveQueueUseCase leaveQueueUseCase;

    public MatchController(GetMatchUseCase getMatchUseCase, GetPlayerMatchesUseCase getPlayerMatchesUseCase, JoinQueueUseCase joinQueueUseCase, LeaveQueueUseCase leaveQueueUseCase) {
        this.getMatchUseCase = getMatchUseCase;
        this.getPlayerMatchesUseCase = getPlayerMatchesUseCase;
        this.joinQueueUseCase = joinQueueUseCase;
        this.leaveQueueUseCase = leaveQueueUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<List<MatchResponseDto>> getPlayerMatches(JwtAuthenticationToken token) {
        List<MatchResponseDto> matches = getPlayerMatchesUseCase.findMatches(token.getToken().getTokenValue());
        if(matches.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDto> getMatch(JwtAuthenticationToken token, @PathVariable UUID id) {
        return ResponseEntity.ok().body(getMatchUseCase.find(token.getToken().getTokenValue(), id));
    }

    @PostMapping("/queue")
    public ResponseEntity<JoinQueueResult> joinQueue(JwtAuthenticationToken token) {
        JoinQueueResult response = joinQueueUseCase.execute(token.getToken().getTokenValue());

        HttpStatus status = HttpStatus.OK;

        if(response.hasMatch()) status = HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/queue")
    public ResponseEntity<Void> leaveQueue(JwtAuthenticationToken token) {
        leaveQueueUseCase.execute(token.getToken().getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
