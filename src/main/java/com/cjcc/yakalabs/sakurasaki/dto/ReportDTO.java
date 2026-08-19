package com.cjcc.yakalabs.sakurasaki.dto;

import java.time.LocalDate;
import java.util.Map;

public class ReportDTO {

    private LocalDate from;
    private LocalDate to;
    private Map<String, Long> data;

    public ReportDTO(LocalDate from, LocalDate to, Map<String, Long> data) {
        this.from = from;
        this.to = to;
        this.data = data;
    }

    public Map<String, Long> getData() {
        return data;
    }

    public void setData(Map<String, Long> data) {
        this.data = data;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

}
