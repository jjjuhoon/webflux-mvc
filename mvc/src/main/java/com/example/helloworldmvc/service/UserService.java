package com.example.helloworldmvc.service;

import com.example.helloworldmvc.web.dto.TokenListDTO;
import com.example.helloworldmvc.web.dto.UserRequestDTO;

public interface UserService {
    TokenListDTO loginGmail(UserRequestDTO.GoogleEmailRequest request);
    String findLanguage(String gmail);

}
