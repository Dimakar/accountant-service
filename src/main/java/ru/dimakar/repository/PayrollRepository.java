package ru.dimakar.repository;

import ru.dimakar.model.PayrollModel;
import ru.dimakar.model.UserModel;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PayrollRepository extends CrudRepository<PayrollModel, Long> {
    PayrollModel findByUserIdAndPeriod(UserModel userId, LocalDate period);
    List<PayrollModel> findAllByUserIdOrderByPeriodDesc(UserModel userId);
}
