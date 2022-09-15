package ru.dimakar.controller;


import ru.dimakar.dto.CreatePayrollDto;
import ru.dimakar.dto.CreatePayrollResponse;
import ru.dimakar.dto.ValidList;
import ru.dimakar.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class AccountantPaymentsController {
    @Autowired
    private PayrollService payrollService;

    @PostMapping("/api/acct/payments")
    public CreatePayrollResponse uploadPayrolls(@Valid @RequestBody ValidList<CreatePayrollDto> createPayrollList) {
        return payrollService.createNewPayrolls(createPayrollList);
    }

    @PutMapping("/api/acct/payments")
    public CreatePayrollResponse changePayroll(@Valid @RequestBody CreatePayrollDto changePayroll) {
        return payrollService.changeNewPayrolls(changePayroll);
    }
}
