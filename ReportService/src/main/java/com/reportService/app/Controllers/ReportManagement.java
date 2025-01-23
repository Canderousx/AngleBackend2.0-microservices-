package com.reportService.app.Controllers;


import com.reportService.app.Config.Exceptions.ReportNotFoundException;
import com.reportService.app.Config.Exceptions.SolutionDoesNotExistException;
import com.reportService.app.Models.Records.ReportSolution;
import com.reportService.app.Models.Records.ServerMessage;
import com.reportService.app.Models.ReportSolutions;
import com.reportService.app.Services.Report.ReportManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReportManagement {

    private final ReportManagementService reportManagementService;


    @RequestMapping(value = "/getSolutions",method = RequestMethod.GET)
    public String[] getSolutions(){
        return ReportSolutions.toArray();
    }

    @RequestMapping(value = "/solveReport",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage>solveReport(@RequestBody ReportSolution reportSolution) throws SolutionDoesNotExistException, ReportNotFoundException {
        reportManagementService.solveReport(reportSolution);
        return ResponseEntity.ok(new ServerMessage("Report has been solved. Thank you!"));
    }
}
