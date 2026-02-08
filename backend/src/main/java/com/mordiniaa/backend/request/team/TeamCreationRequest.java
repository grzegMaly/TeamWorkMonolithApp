package com.mordiniaa.backend.request.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreationRequest {

    @NotBlank
    @Size(min = 5, max = 40)
    @Pattern(regexp = "^\\p{L}+([ -]\\p{L}+)*$")
    private String teamName;
}
