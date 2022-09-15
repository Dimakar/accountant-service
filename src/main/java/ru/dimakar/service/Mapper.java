package ru.dimakar.service;

import ru.dimakar.dto.EventDto;
import ru.dimakar.dto.UserDto;
import ru.dimakar.model.EventModel;
import ru.dimakar.model.RoleModel;
import ru.dimakar.model.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class Mapper {

    public UserDto userToDto(UserModel userModel) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userModel,
                        UserDto.class)
                .withRoles(userModel.getRoles()
                        .stream()
                        .map(RoleModel::getName)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    public EventDto eventToDto(EventModel eventModel) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(eventModel,
                        EventDto.class);
    }
}
