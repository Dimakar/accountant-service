package ru.dimakar.service;

import ru.dimakar.dto.EventDto;
import ru.dimakar.dto.Events;
import ru.dimakar.model.EventModel;
import ru.dimakar.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Mapper mapper;

    private void addEvent(EventModel eventModel) {
        eventModel.setPath(getPath());
        eventModel.setDate(LocalDateTime.now());
        eventRepository.save(eventModel);
    }

    private String getPath() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
    }

    private String getCurrentUserEmail(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "null";
        }
        return null;
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll()
                .stream().map(e -> mapper.eventToDto(e))
                .collect(Collectors.toList());
    }

    public void bruteForce(String email) {
        addEvent(EventModel.builder()
                .action(Events.BRUTE_FORCE.toString())
                .subject(email)
                .object(getPath())
                .build());
    }

    public void lockUser(String email, boolean autoLocked) {
        addEvent(EventModel.builder()
                .action(Events.LOCK_USER.toString())
                .subject(autoLocked ? email : getCurrentUserEmail())
                .object("Lock user " + email)
                .build());
    }

    public void unlockUser(String email) {
        addEvent(EventModel.builder()
                .action(Events.UNLOCK_USER.toString())
                .subject(getCurrentUserEmail())
                .object("Unlock user " + email)
                .build());
    }

    public void accessDenied(String email) {
        addEvent(EventModel.builder()
                .action(Events.ACCESS_DENIED.toString())
                .object(getPath())
                .subject(email)
                .build());
    }

    public void changePassword(String email) {
        addEvent(EventModel.builder()
                .action(Events.CHANGE_PASSWORD.toString())
                .subject(email)
                .object(email)
                .build());
    }

    public void deleteUser(String deletedEmail) {
        addEvent(EventModel.builder()
                .action(Events.DELETE_USER.toString())
                .subject(Objects.requireNonNull(getCurrentUserEmail()))
                .object(deletedEmail)
                .build());
    }

    public void removeRole(String authority, String userEmail) {
        addEvent(EventModel.builder()
                .action(Events.REMOVE_ROLE.toString())
                .subject(Objects.requireNonNull(getCurrentUserEmail()))
                .object("Remove role " + authority + " from " + userEmail)
                .build());
    }

    public void grantRole(String authority, String userEmail) {
        addEvent(EventModel.builder()
                .action(Events.GRANT_ROLE.toString())
                .subject(Objects.requireNonNull(getCurrentUserEmail()))
                .object("Grant role " + authority + " to " + userEmail)
                .build());
    }

    public void loginFailed(String email) {
        addEvent(EventModel.builder()
                .action(Events.LOGIN_FAILED.toString())
                .subject(email)
                .object(getPath())
                .build());
    }

    public void createUser(String email) {
        addEvent(EventModel.builder()
                .action(Events.CREATE_USER.toString())
                .subject("Anonymous")
                .object(email)
                .build());
    }
}
