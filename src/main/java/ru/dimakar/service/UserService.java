package ru.dimakar.service;

import ru.dimakar.dto.*;
import ru.dimakar.ex.BadRequestException;
import ru.dimakar.ex.NotFoundException;
import ru.dimakar.model.RoleModel;
import ru.dimakar.model.UserModel;
import ru.dimakar.repository.RoleRepository;
import ru.dimakar.repository.UserRepository;
import ru.dimakar.security.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    // TODO: 02.09.2022 create table with breached passwords in DB
    Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Mapper mapper;

    @Autowired
    private EventService eventService;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            if (roleRepository.findRoleModelByName(UserRole.ROLE_USER.toString()) == null)
                roleRepository.save(RoleModel.builder().name(UserRole.ROLE_USER.toString()).type(UserRole.ROLE_USER.getGroup()).build());
            if (roleRepository.findRoleModelByName(UserRole.ROLE_ADMINISTRATOR.toString()) == null)
                roleRepository.save(RoleModel.builder().name(UserRole.ROLE_ADMINISTRATOR.toString()).type(UserRole.ROLE_ADMINISTRATOR.getGroup()).build());
            if (roleRepository.findRoleModelByName(UserRole.ROLE_ACCOUNTANT.toString()) == null)
                roleRepository.save(RoleModel.builder().name(UserRole.ROLE_ACCOUNTANT.toString()).type(UserRole.ROLE_ACCOUNTANT.getGroup()).build());
            if (roleRepository.findRoleModelByName(UserRole.ROLE_AUDITOR.toString()) == null)
                roleRepository.save(RoleModel.builder().name(UserRole.ROLE_AUDITOR.toString()).type(UserRole.ROLE_AUDITOR.getGroup()).build());
        } catch (Exception e) {

        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(email.toLowerCase());
        if (userModel == null) {
            throw new UsernameNotFoundException("User is not found: " + email);
        }
        if (userModel.isLocked()) {
            throw new BadRequestException("User account is locked");
        }
        return new User(userModel);
    }

    @Transactional
    public UserDto save(SignUpRequest signUpRequest) {
        checkBreachedPasswords(signUpRequest.getPassword());
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(signUpRequest.getEmail().toLowerCase());
        if (userModel != null) {
            throw new BadRequestException("User exist!");
        }

        userModel = modelMapper.map(signUpRequest, UserModel.class);
        userModel.setEmail(signUpRequest.getEmail().toLowerCase());
        RoleModel roleModelAdmin = roleRepository.findRoleModelByName(UserRole.ROLE_ADMINISTRATOR.toString());
        RoleModel roleModelUser = roleRepository.findRoleModelByName(UserRole.ROLE_USER.toString());
        if (roleModelAdmin == null || roleModelUser == null)
            throw new RuntimeException("System doesn't have needed roles");
        userModel.setRoles(userRepository.findAllByRolesContaining(roleModelAdmin).isEmpty() ?
                List.of(roleModelAdmin)
                : List.of(roleModelUser));
        userModel.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userModel.setLocked(false);
        userModel.setCountFailures(0);
        userModel = userRepository.save(userModel);
        eventService.createUser(userModel.getEmail());
        return modelMapper.map(userModel,
                        UserDto.class)
                .withRoles(userModel.getRoles()
                        .stream()
                        .map(RoleModel::getName)
                        .collect(Collectors.toList()));
    }

    public ChangePassResponse changePassword(ChangePassRequest changePassRequest, UserDetails userDetails) {
        checkBreachedPasswords(changePassRequest.getPassword());
        if (passwordEncoder.matches(changePassRequest.getPassword(), userDetails.getPassword())) {
            throw new BadRequestException("The passwords must be different!");
        }
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(userDetails.getUsername());
        userModel.setPassword(passwordEncoder.encode(changePassRequest.getPassword()));
        userRepository.save(userModel);
        eventService.changePassword(userModel.getEmail());
        return new ChangePassResponse(userDetails.getUsername(), "The password has been updated successfully");
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(um -> mapper.userToDto(um))
                .collect(Collectors.toList());
    }

    private void checkBreachedPasswords(String password) {
        if (breachedPasswords.contains(password)) {
            throw new BadRequestException("The password is in the hacker's database!");
        }
    }

    @Transactional
    public DeleteUserResponse deleteUser(String email) {
        UserModel userModelByEmail = getUserByEmail(email);
        if (userModelByEmail.getRoles().stream()
                .anyMatch(rm -> rm.getName().equals(UserRole.ROLE_ADMINISTRATOR.toString()))) {
            throw new BadRequestException("Can't remove ADMINISTRATOR role!");
        }

        userRepository.deleteByEmail(email);
        eventService.deleteUser(email);
        return DeleteUserResponse.builder()
                .user(email)
                .status("Deleted successfully!")
                .build();
    }

    @Transactional
    public UserDto changeRole(ChangeRolesDto changeRolesDto) {
        changeRolesDto.setRole("ROLE_" + changeRolesDto.getRole());
        UserModel userModel = getUserByEmail(changeRolesDto.getUser());
        RoleModel roleModelByName = roleRepository.findRoleModelByName(changeRolesDto.getRole());
        if (roleModelByName == null) {
            throw new NotFoundException("Role not found!");
        }
        if (changeRolesDto.getOperation() == ChangeRolesDto.Operation.REMOVE) {
            if (!userModel.getRoles().contains(roleModelByName)) {
                throw new BadRequestException("The user does not have a role!");
            }
            if (changeRolesDto.getRole().equals(UserRole.ROLE_ADMINISTRATOR.toString())) {
                throw new BadRequestException("Can't remove ADMINISTRATOR role!");
            }
            if (userModel.getRoles().size() == 1) {
                throw new BadRequestException("The user must have at least one role!");
            }
            userModel.getRoles().remove(roleModelByName);
            userModel = userRepository.save(userModel);
            eventService.removeRole(changeRolesDto.getRole().replaceAll("ROLE_", ""), userModel.getEmail());
        } else if (changeRolesDto.getOperation() == ChangeRolesDto.Operation.GRANT) {
            if (userModel.getRoles()
                    .stream()
                    .noneMatch(rm -> rm.getType().equals(roleModelByName.getType()))) {
                throw new BadRequestException("The user cannot combine administrative and business roles!");
            }
            userModel.getRoles().add(roleModelByName);
            userModel = userRepository.save(userModel);
            eventService.grantRole(changeRolesDto.getRole().replaceAll("ROLE_", ""), userModel.getEmail());
        }
        return mapper.userToDto(userModel);
    }

    private UserModel getUserByEmail(String email) {
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(email);
        if (userModel == null) {
            throw new NotFoundException("User not found!");
        }
        return userModel;
    }

    @Transactional
    public ChangePassResponse changeAccess(ChangeAccessRequest changeAccessRequest) {
        UserModel userModel = getUserByEmail(changeAccessRequest.getUser());
        if (userModel.getRoles().contains(roleRepository.findRoleModelByName(UserRole.ROLE_ADMINISTRATOR.toString()))) {
            throw new BadRequestException("Can't lock the ADMINISTRATOR!");
        }
        if (changeAccessRequest.getOperation() == ChangeAccessRequest.Operation.LOCK) {
            userModel.setLocked(true);
            eventService.lockUser(userModel.getEmail(), false);
        } else if (changeAccessRequest.getOperation() == ChangeAccessRequest.Operation.UNLOCK) {
            userModel.setLocked(false);
            eventService.unlockUser(userModel.getEmail());
        }
        userRepository.save(userModel);
        return ChangePassResponse.builder().status("User " + userModel.getEmail()
                + " " + changeAccessRequest.getOperation().toString().toLowerCase() + "ed!").build();
    }

    @Transactional
    public void incrementFailedAttempts(String email, int maxAttempts, int maxAttemptsForAdmin) {

        eventService.loginFailed(email);
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(email);
        if (userModel != null) {
            userModel.setCountFailures(userModel.getCountFailures() + 1);
            int maxAtt = userModel.getRoles()
                    .contains(roleRepository.findRoleModelByName(UserRole.ROLE_ADMINISTRATOR.toString())) ?
                    maxAttemptsForAdmin : maxAttempts;
            if (userModel.getCountFailures() == maxAtt) {
                userModel.setLocked(true);
                eventService.bruteForce(email);
                eventService.lockUser(email, true);
            }
            userRepository.save(userModel);
        }
    }

    public void clearFailureCount(String email) {
        UserModel userModel = getUserByEmail(email);
        userModel.setCountFailures(0);
        userRepository.save(userModel);
    }
}
