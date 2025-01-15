package com.reportService.app.Models.Records;

import java.util.Date;

public record BanData(
        String reportId,
        String reportedId,
        String reporterId,
        String bannedMediaId,
        String reason,
        Date dateBanned
) {
}
