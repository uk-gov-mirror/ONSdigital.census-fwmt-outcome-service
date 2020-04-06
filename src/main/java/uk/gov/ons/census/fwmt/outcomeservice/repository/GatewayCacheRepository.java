package uk.gov.ons.census.fwmt.outcomeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;

import java.util.List;

@Repository
public interface GatewayCacheRepository extends JpaRepository<GatewayCache, Long> {

  GatewayCache findByCaseId(String caseId);

  List<GatewayCache> findAll();

}
