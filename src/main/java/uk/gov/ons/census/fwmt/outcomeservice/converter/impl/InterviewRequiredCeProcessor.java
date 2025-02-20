package uk.gov.ons.census.fwmt.outcomeservice.converter.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.outcomeservice.config.GatewayOutcomeQueueConfig;
import uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceProcessor;
import uk.gov.ons.census.fwmt.outcomeservice.data.GatewayCache;
import uk.gov.ons.census.fwmt.outcomeservice.dto.OutcomeSuperSetDto;
import uk.gov.ons.census.fwmt.outcomeservice.message.GatewayOutcomeProducer;
import uk.gov.ons.census.fwmt.outcomeservice.service.impl.GatewayCacheService;
import uk.gov.ons.census.fwmt.outcomeservice.template.TemplateCreator;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gov.ons.census.fwmt.outcomeservice.converter.OutcomeServiceLogConfig.*;
import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.CCS_ADDRESS_LISTED;
import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.INTERVIEW_REQUIRED;

@Component("INTERVIEW_REQUIRED_CE")
public class InterviewRequiredCeProcessor implements OutcomeServiceProcessor {

  @Autowired
  private DateFormat dateFormat;

  @Autowired
  private GatewayOutcomeProducer gatewayOutcomeProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private GatewayCacheService gatewayCacheService;

  @Override
  public UUID process(OutcomeSuperSetDto outcome, UUID caseIdHolder, String type) throws GatewayException {
    UUID caseId = (caseIdHolder != null) ? caseIdHolder : outcome.getCaseId();
    UUID newCaseId = UUID.randomUUID();

    gatewayEventManager.triggerEvent(String.valueOf(caseId), PROCESSING_OUTCOME,
        SURVEY_TYPE, type,
        PROCESSOR, "INTERVIEW_REQUIRED",
        ORIGINAL_CASE_ID, String.valueOf(outcome.getCaseId()),
        INTERVIEW_CASE_ID, String.valueOf(newCaseId),
        ADDRESS_TYPE, "CE");

    GatewayCache plCache = gatewayCacheService.getById(String.valueOf(caseId));
    cacheData(outcome, newCaseId);
    
    String eventDateTime = dateFormat.format(outcome.getEventDate());
    Map<String, Object> root = new HashMap<>();
    root.put("outcome", outcome);
    root.put("address", outcome.getAddress());
    root.put("caseId", newCaseId);
    root.put("eventDate", eventDateTime);
    root.put("addressType", "CE");
    root.put("addressLevel", "E");
    root.put("interviewRequired", "True");
    root.put("oa", plCache.getOa());
    root.put("region",plCache.getOa().charAt(0));
    root.put("estabType", outcome.getCeDetails() != null && outcome.getCeDetails().getEstablishmentType() != null ?
        outcome.getCeDetails().getEstablishmentType() : "CE");
    root.put("organisationName", outcome.getCeDetails() != null && outcome.getCeDetails().getEstablishmentName() != null ?
        outcome.getCeDetails().getEstablishmentName() : "");

    String outcomeEvent = TemplateCreator.createOutcomeMessage(CCS_ADDRESS_LISTED, root);

    gatewayOutcomeProducer.sendOutcome(outcomeEvent, String.valueOf(outcome.getTransactionId()),
        GatewayOutcomeQueueConfig.GATEWAY_CCS_PROPERTY_LISTING_ROUTING_KEY);

    gatewayEventManager.triggerEvent(String.valueOf(caseId), OUTCOME_SENT,
        SURVEY_TYPE, type,
        TEMPLATE_TYPE, INTERVIEW_REQUIRED.toString(),
        TRANSACTION_ID, outcome.getTransactionId().toString(),
        ROUTING_KEY, GatewayOutcomeQueueConfig.GATEWAY_CCS_PROPERTY_LISTING_ROUTING_KEY);

    return newCaseId;
  }

  private void cacheData(OutcomeSuperSetDto outcome, UUID newCaseId) {
    String managerTitle = "";
    String managerForename = "";
    String managerSurname = "";
    String managerPhone = "";
    int usualResidents = 0;
    int bedspaces = 0;

    if (outcome.getCeDetails() != null) {
      if (outcome.getCeDetails().getManagerTitle() != null) {
        managerTitle = outcome.getCeDetails().getManagerTitle();
      }
      if (outcome.getCeDetails().getManagerForename() != null) {
        managerForename = outcome.getCeDetails().getManagerForename();
      }
      if (outcome.getCeDetails().getManagerSurname() != null) {
        managerSurname = outcome.getCeDetails().getManagerSurname();
      }
      if (outcome.getCeDetails().getContactPhone() != null) {
        managerPhone = outcome.getCeDetails().getContactPhone();
      }
      if (outcome.getCeDetails().getUsualResidents() != null) {
        usualResidents = outcome.getCeDetails().getUsualResidents();
      }
      if (outcome.getCeDetails().getBedspaces() != null) {
        bedspaces = outcome.getCeDetails().getBedspaces();
      }
    }

    gatewayCacheService.save(GatewayCache.builder()
        .caseId(newCaseId.toString())
        .existsInFwmt(false)
        .accessInfo(outcome.getAccessInfo())
        .careCodes(OutcomeSuperSetDto.careCodesToText(outcome.getCareCodes()))
        .type(50)
        .managerTitle(managerTitle)
        .managerFirstname(managerForename)
        .managerSurname(managerSurname)
        .managerContactNumber(managerPhone)
        .usualResidents(usualResidents)
        .bedspaces(bedspaces)
        .build());
  }
}
