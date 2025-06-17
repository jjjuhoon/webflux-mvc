package com.example.helloworldmvc.web.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenListDTO {
    private List<TokenDTO> tokenList;
}
