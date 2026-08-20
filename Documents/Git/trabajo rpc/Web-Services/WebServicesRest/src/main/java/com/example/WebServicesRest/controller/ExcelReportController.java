package com.example.WebServicesRest.controller;

import com.example.WebServicesRest.service.ExcelReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExcelReportController {

    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping("/api/reporte/donaciones")
    public ResponseEntity<byte[]> generarReporte() {
        try {
            byte[] excelData = excelReportService.generarExcel();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_donaciones.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
