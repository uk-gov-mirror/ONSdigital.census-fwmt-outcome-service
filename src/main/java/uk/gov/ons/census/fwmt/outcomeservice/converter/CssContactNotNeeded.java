package uk.gov.ons.census.fwmt.outcomeservice.converter;

import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.data.ccs.CCSPropertyListingOutcome;
import uk.gov.ons.census.fwmt.outcomeservice.template.HouseholdTemplateCreator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.ons.census.fwmt.outcomeservice.enums.EventType.CONTACT_NOT_NEEDED;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getAddressLevel;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getAddressType;
import static uk.gov.ons.census.fwmt.outcomeservice.util.CcsUtilityMethods.getOrganisationName;

@Component
public class CssContactNotNeeded implements CcsOutcomeServiceProcessor {

  @Override
  public boolean isValid(CCSPropertyListingOutcome ccsPropertyListingOutcome) {
    List<String> validSecondaryOutcomes = Collections.singletonList("Contact not needed");
    return validSecondaryOutcomes.contains(ccsPropertyListingOutcome.getSecondaryOutcome());
  }

  @Override
  public void processMessage(CCSPropertyListingOutcome ccsPropertyListingOutcome) {
    Map<String, Object> root = new HashMap<>();
    root.put("householdOutcome", ccsPropertyListingOutcome);
    root.put("addressType", getAddressType(ccsPropertyListingOutcome));
    root.put("addressLevel", getAddressLevel(ccsPropertyListingOutcome));
    root.put("organisationName", getOrganisationName(ccsPropertyListingOutcome));

    String outcomeEvent = HouseholdTemplateCreator.createOutcomeMessage(CONTACT_NOT_NEEDED, root);
  }
}
