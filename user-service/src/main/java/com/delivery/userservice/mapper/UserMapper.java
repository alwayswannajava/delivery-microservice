package com.delivery.userservice.mapper;

import com.delivery.userservice.config.MapperConfig;
import com.delivery.userservice.dto.request.CreateUserRequest;
import com.delivery.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface UserMapper {

    User toUser(CreateUserRequest createUserRequest);
}
