package org.letgabr.RSADigitalSignatureShowcase.dao;

import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, String> {
}
