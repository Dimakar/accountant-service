package ru.dimakar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeRolesDto {
    private String user;
    private String role;
    private Operation operation;


    public enum Operation {
        REMOVE,
        GRANT
    }
}
