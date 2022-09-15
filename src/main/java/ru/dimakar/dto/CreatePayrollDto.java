package ru.dimakar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePayrollDto {
    @NotEmpty(message = "Employee email must be not empty")
    @JsonProperty("employee")
    private String employee;
    @NotEmpty(message = "Period must be not empty")
    @Pattern(regexp = "(0[1-9]|1[0-2])-(19|20)\\d\\d", message = "Period must have mm-YYYY format")
    @JsonProperty("period")
    private String period;
    @NotNull
    @Min(value = 0L, message = "Salary must be positive value")
    @JsonProperty("salary")
    private Long salary;
}
