package ru.dimakar.repository;

import ru.dimakar.model.RoleModel;
import ru.dimakar.model.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    UserModel findUserModelByEmailIgnoreCase(String email);
    List<UserModel> findAll();
    List<UserModel> findAllByRolesContaining(RoleModel role);


    void deleteByEmail(String email);
}
