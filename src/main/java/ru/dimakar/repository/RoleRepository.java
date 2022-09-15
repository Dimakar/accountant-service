package ru.dimakar.repository;

import ru.dimakar.model.RoleModel;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleModel, Long> {
    RoleModel findRoleModelByName(String name);
}
