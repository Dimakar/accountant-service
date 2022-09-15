package ru.dimakar.service;

import ru.dimakar.dto.CreatePayrollDto;
import ru.dimakar.dto.CreatePayrollResponse;
import ru.dimakar.dto.GetPaymentResponse;
import ru.dimakar.ex.BadRequestException;
import ru.dimakar.model.PayrollModel;
import ru.dimakar.model.UserModel;
import ru.dimakar.repository.PayrollRepository;
import ru.dimakar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Transactional
    public CreatePayrollResponse createNewPayrolls(List<CreatePayrollDto> createPayrollList) {
        createPayrollList.forEach(payroll -> {
            UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(payroll.getEmployee());
            if (userModel == null) {
                throw new BadRequestException("User with email='" + payroll.getEmployee() + "' doesn't exist");
            }
            LocalDate payrollMonth = periodToLocalDate(payroll.getPeriod());
            PayrollModel byUserIdAndPeriod = payrollRepository.findByUserIdAndPeriod(userModel, payrollMonth);
            if (byUserIdAndPeriod != null) {
                throw new BadRequestException("Period " + payroll.getPeriod() + " for user " + userModel.getEmail() + " already exists!");
            }
            userModel.getPayrolls().add(PayrollModel.builder()
                    .period(payrollMonth)
                    .salary(payroll.getSalary())
                    .userId(userModel)
                    .build());
            userRepository.save(userModel);
        });
        return new CreatePayrollResponse("Added successfully!");
    }

    @Transactional
    public CreatePayrollResponse changeNewPayrolls(CreatePayrollDto payroll) {
        PayrollModel payrollModel = userRepository.findUserModelByEmailIgnoreCase(payroll.getEmployee())
                .getPayrolls()
                .stream()
                .filter(pay -> pay.getPeriod().equals(periodToLocalDate(payroll.getPeriod())))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Payroll with email = '" + payroll.getEmployee() +
                        "' and period = '" + payroll.getPeriod() + "' doesn't exist!"));
        payrollModel.setSalary(payroll.getSalary());
        payrollRepository.save(payrollModel);
        return new CreatePayrollResponse("Updated successfully!");
    }

    public List<GetPaymentResponse> getAllPayments(String email) {
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(email);
        return userModel
                .getPayrolls()
                .stream()
                .sorted((p1, p2) -> p2.getPeriod().compareTo(p1.getPeriod()))
                .map(payrollModel -> getPaymentResponse(userModel, payrollModel))
                .collect(Collectors.toList());
    }

    public GetPaymentResponse getPaymentByPeriod(String email, String period) {
        UserModel userModel = userRepository.findUserModelByEmailIgnoreCase(email);
        PayrollModel payrollModel = userModel
                .getPayrolls()
                .stream()
                .filter(pay -> pay.getPeriod().equals(periodToLocalDate(period)))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Payroll with email = '" + email +
                        "' and period = '" + period + "' doesn't exist!"));
        return getPaymentResponse(userModel, payrollModel);
    }

    private GetPaymentResponse getPaymentResponse(UserModel userModel, PayrollModel payrollModel) {
        return GetPaymentResponse.builder()
                .lastname(userModel.getLastname())
                .name(userModel.getName())
                .period(payrollModel.getPeriod().format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH)))
                .salary(salaryToFormat(payrollModel.getSalary()))
                .build();
    }

    private String salaryToFormat(Long salary) {
        var dollars = salary / 100;
        var cent = salary % 100;
        return dollars + " dollar(s) " + cent + " cent(s)";
    }

    private LocalDate periodToLocalDate(String period) {
        return LocalDate.parse("01-" + period, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}
