package org.egov.wcms.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.egov.wcms.web.models.Role;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * User
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-05-03T01:09:48.367+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User   {
        @JsonProperty("id")
        private Long id = null;

        @JsonProperty("userName")
        private String userName = null;

        @JsonProperty("password")
        private String password = null;

        @JsonProperty("salutation")
        private String salutation = null;

        @JsonProperty("name")
        private String name = null;

        @JsonProperty("gender")
        private String gender = null;

        @JsonProperty("mobileNumber")
        private String mobileNumber = null;

        @JsonProperty("emailId")
        private String emailId = null;

        @JsonProperty("altContactNumber")
        private String altContactNumber = null;

        @JsonProperty("pan")
        private String pan = null;

        @JsonProperty("aadhaarNumber")
        private String aadhaarNumber = null;

        @JsonProperty("permanentAddress")
        private String permanentAddress = null;

        @JsonProperty("permanentCity")
        private String permanentCity = null;

        @JsonProperty("permanentPincode")
        private String permanentPincode = null;

        @JsonProperty("correspondenceCity")
        private String correspondenceCity = null;

        @JsonProperty("correspondencePincode")
        private String correspondencePincode = null;

        @JsonProperty("correspondenceAddress")
        private String correspondenceAddress = null;

        @JsonProperty("active")
        private Boolean active = null;

        @JsonProperty("dob")
        private Long dob = null;

        @JsonProperty("pwdExpiryDate")
        private Long pwdExpiryDate = null;

        @JsonProperty("locale")
        private String locale = null;

        @JsonProperty("type")
        private String type = null;

        @JsonProperty("signature")
        private String signature = null;

        @JsonProperty("accountLocked")
        private Boolean accountLocked = null;

        @JsonProperty("roles")
        @Valid
        private List<Role> roles = null;

        @JsonProperty("fatherOrHusbandName")
        private String fatherOrHusbandName = null;

        @JsonProperty("bloodGroup")
        private String bloodGroup = null;

        @JsonProperty("identificationMark")
        private String identificationMark = null;

        @JsonProperty("photo")
        private String photo = null;

        @JsonProperty("createdBy")
        private String createdBy = null;

        @JsonProperty("createdDate")
        private Long createdDate = null;

        @JsonProperty("lastModifiedBy")
        private String lastModifiedBy = null;

        @JsonProperty("lastModifiedDate")
        private Long lastModifiedDate = null;

        @JsonProperty("otpReference")
        private String otpReference = null;

        @JsonProperty("tenantId")
        private String tenantId = null;


        public User addRolesItem(Role rolesItem) {
            if (this.roles == null) {
            this.roles = new ArrayList<>();
            }
        this.roles.add(rolesItem);
        return this;
        }

}

