package uk.gov.ons.census.fwmt.outcomeservice.data.dto.rm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "source",
    "channel",
    "dateTime",
    "transactionId"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

  @JsonProperty("type")
  private String type;
  @JsonProperty("source")
  private String source;
  @JsonProperty("channel")
  private String channel;
  // "2011-08-12T20:17:46.384Z" example
  @JsonProperty("dateTime")
  private SimpleDateFormat dateTime;
  @JsonProperty("transactionId")
  private String transactionId;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<>();
}
