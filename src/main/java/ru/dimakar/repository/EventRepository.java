package ru.dimakar.repository;

import ru.dimakar.model.EventModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<EventModel, Long> {
    List<EventModel> findAll();
}
