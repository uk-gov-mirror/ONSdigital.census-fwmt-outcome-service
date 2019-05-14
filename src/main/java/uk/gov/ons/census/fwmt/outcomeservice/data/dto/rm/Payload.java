package uk.gov.ons.census.fwmt.outcomeservice.data.dto.rm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "invalidAddress",
    "refusal",
    "fulfilment",
    "contact"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {

  @JsonProperty("invalidAddress")
  private InvalidAddress invalidAddress;

  @JsonProperty("refusal")
  private Refusal refusal;

  @JsonProperty("uac")
  private Uac uac;

  @JsonProperty("fulfilment")
  private Fulfilment fulfilment;

  @JsonProperty("contact")
  private Contact contact;

}
