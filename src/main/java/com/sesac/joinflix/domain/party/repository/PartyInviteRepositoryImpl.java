package com.sesac.joinflix.domain.party.repository;

import com.sesac.joinflix.domain.party.entity.PartyInvite;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class PartyInviteRepositoryImpl implements PartyInviteRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(List<PartyInvite> invites) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        String sql = """
                    INSERT INTO party_invites (party_room_id, guest_id, created_at, updated_at)
            
                    VALUES (?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, invites, invites.size(),
            (PreparedStatement ps, PartyInvite invite) -> {
                ps.setLong(1, invite.getPartyRoom().getId());
                ps.setLong(2, invite.getGuest().getId());
                ps.setTimestamp(3, now);
                ps.setTimestamp(4, now);
            });
    }
}
