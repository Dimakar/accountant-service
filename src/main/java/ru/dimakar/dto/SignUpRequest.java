package ru.dimakar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpRequest{

	@NotEmpty
	@Size(min = 12, message = "Password length must be 12 chars minimum!")
	@JsonProperty("password")
	private String password;
	@NotEmpty
	@JsonProperty("name")
	private String name;
	@NotEmpty
	@Pattern(regexp = "(.*)@acme\\.com", message = "Email must be from acme.com domain")
	@Email
	@JsonProperty("email")
	private String email;
	@NotEmpty
	@JsonProperty("lastname")
	private String lastname;
}