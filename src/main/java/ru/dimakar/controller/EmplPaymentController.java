package ru.dimakar.controller;

import ru.dimakar.service.PayrollService;
import io.micrometer.core.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Validated
public class EmplPaymentController {
    @Autowired
    private PayrollService payrollService;

    @GetMapping("/api/empl/payment")
    public Object getPayment(@AuthenticationPrincipal UserDetails userDetails,
                                         @Nullable
                                         @Valid
                                         @RequestParam
                                         @Pattern(regexp = "(0[1-9]|1[0-2])-(19|20)\\d\\d",
                                                 message = "Period must have mm-YYYY format") String period) {
        if (period == null) {
            return payrollService.getAllPayments(userDetails.getUsername());
        }
        return payrollService.getPaymentByPeriod(userDetails.getUsername(), period);
    }
}
