package com.sesac.joinflix.domain.party.repository;

import com.sesac.joinflix.domain.party.entity.PartyInvite;
import java.util.List;

public interface PartyInviteRepositoryCustom {

    void saveAll(List<PartyInvite> invites);
}
