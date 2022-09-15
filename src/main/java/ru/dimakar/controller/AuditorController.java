package ru.dimakar.controller;

import ru.dimakar.dto.EventDto;
import ru.dimakar.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class AuditorController {

    @Autowired
    private EventService eventService;

    @GetMapping("api/security/events")
    public List<EventDto> getUser() {
        return eventService.getAllEvents();
    }

}
