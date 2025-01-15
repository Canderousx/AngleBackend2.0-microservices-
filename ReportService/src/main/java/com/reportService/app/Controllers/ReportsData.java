package com.reportService.app.Controllers;


import com.reportService.app.Models.Report;
import com.reportService.app.Services.Report.ReportRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsData {

    private final ReportRetrievalService retrievalService;
    @RequestMapping(value = "/getUnresolved",method = RequestMethod.GET)
    public Page<Report>getUnresolved(@RequestParam int page,
                                     @RequestParam int pageSize,
                                     @RequestParam(required = false) String category){
        if(category != null){
            return retrievalService.getUnresolved(category,page,pageSize);
        }
        return retrievalService.getUnresolved(page,pageSize);
    }

    @RequestMapping(value = "/getByCategory",method = RequestMethod.GET)
    public Page<Report>getByCategory(@RequestParam String category,
                                     @RequestParam(required = false) Boolean solved,
                                     @RequestParam int page,
                                     @RequestParam int pageSize){
        if(solved == null){
            return retrievalService.getByCategory(category,page,pageSize);
        }
        return retrievalService.getByCategory(category,solved,page,pageSize);
    }

    @RequestMapping(value = "/getResolved",method = RequestMethod.GET)
    public Page<Report>getResolved(@RequestParam int page,
                                   @RequestParam int pageSize,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) String adminId){
        if (category == null && adminId == null){
            return retrievalService.getResolved(page,pageSize);
        }
        if (category != null && adminId == null){
            return retrievalService.getByCategory(category,true,page,pageSize);
        }
        if (category == null){
            return retrievalService.getResolved(adminId,page,pageSize);
        }
        return retrievalService.getByCategory(category,adminId,page,pageSize);
    }

    @RequestMapping(value = "/getByReportedId",method = RequestMethod.GET)
    public Page<Report>getByReportedId(@RequestParam String id,
                                       @RequestParam(required = false)String solution,
                                       @RequestParam int page,
                                       @RequestParam int pageSize){
        if(solution == null){
            return retrievalService.getByReportedId(id,solution,page,pageSize);
        }
        return retrievalService.getByReportedId(id,page,pageSize);
    }





}
