package org.egov.pt.web.models;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PropertyDetail
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-05-11T14:12:44.497+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PropertyDetail   {
        @JsonProperty("id")
        private String id;

              /**
   * Source of a assessment data. The properties will be created in a system based on the data avaialble in their manual records or during field survey. There can be more from client to client.
   */
  public enum SourceEnum {
    MUNICIPAL_RECORDS("MUNICIPAL_RECORDS"),
    
    FIELD_SURVEY("FIELD_SURVEY");

    private String value;

    SourceEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SourceEnum fromValue(String text) {
      for (SourceEnum b : SourceEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

        @JsonProperty("source")
        private SourceEnum source;

        @JsonProperty("usage")
        private String usage;

        @JsonProperty("noOfFloors")
        private Long noOfFloors;

        @JsonProperty("landArea")
        private Float landArea;

        @JsonProperty("buildUpArea")
        private Float buildUpArea;

        @JsonProperty("units")
        @Valid
        private Set<Unit> units;

        @JsonProperty("documents")
        @Valid
        private Set<Document> documents;

        @JsonProperty("additionalDetails")
        private String additionalDetails;

              /**
   * Property can be created from different channels Eg. System (properties created by ULB officials), CFC Counter (From citizen faciliation counters) etc. Here we are defining some known channels, there can be more client to client.
   */
  public enum ChannelEnum {
    SYSTEM("SYSTEM"),
    
    CFC_COUNTER("CFC_COUNTER"),
    
    CITIZEN("CITIZEN"),
    
    DATA_ENTRY("DATA_ENTRY"),
    
    MIGRATION("MIGRATION");

    private String value;

    ChannelEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ChannelEnum fromValue(String text) {
      for (ChannelEnum b : ChannelEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

        @JsonProperty("channel")
        private ChannelEnum channel;


        public PropertyDetail addUnitsItem(Unit unitsItem) {
            if (this.units == null) {
            this.units = new HashSet<>();
            }
        this.units.add(unitsItem);
        return this;
        }

        public PropertyDetail addDocumentsItem(Document documentsItem) {
            if (this.documents == null) {
            this.documents = new HashSet<>();
            }
        this.documents.add(documentsItem);
        return this;
        }

}

