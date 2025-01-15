package com.reportService.app.Controllers;


import com.reportService.app.Config.Exceptions.TypeDoesNotExistException;
import com.reportService.app.Models.Records.ServerMessage;
import com.reportService.app.Models.Report;
import com.reportService.app.Models.ReportCategories;
import com.reportService.app.Services.Report.ReportManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/newReport")
@RequiredArgsConstructor
public class NewReport {

    private final ReportManagementService reportManagementService;

    @RequestMapping(value = "/getCategories",method = RequestMethod.GET)
    public List<String>getCategories(){
        return ReportCategories.getAll();
    }
    @RequestMapping(value = "",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>newReport(@RequestBody Report report) throws TypeDoesNotExistException {
        reportManagementService.addReport(report);
        return ResponseEntity.ok(new ServerMessage("Report has been sent. Thank you"));
    }



}
