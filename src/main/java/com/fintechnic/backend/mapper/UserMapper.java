package com.fintechnic.backend.mapper;

import com.fintechnic.backend.dto.UserDTO;
import com.fintechnic.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "balance", target = "balance")
    UserDTO userToUserDTO(User user);
}
