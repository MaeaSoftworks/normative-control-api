package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;

import java.util.List;

public interface InvitesRepository extends JpaRepository<Invite, Integer> {
    List<Invite> findAllByOwner(Normocontroller owner);
}
