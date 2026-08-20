package com.example.WebServicesRest.service;

import com.example.WebServicesRest.model.InventarioDonaciones;
import com.example.WebServicesRest.repository.InventarioDonacionesRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExcelReportService {

    @Autowired
    private InventarioDonacionesRepository repo;

    public byte[] generarExcel() throws Exception {
        List<InventarioDonaciones> donaciones = repo.findAll();

        // Agrupamos por categoría
        Map<String, List<InventarioDonaciones>> agrupadas = donaciones.stream()
                .collect(Collectors.groupingBy(InventarioDonaciones::getCategoria));

        Workbook workbook = new XSSFWorkbook();

        for (Map.Entry<String, List<InventarioDonaciones>> entry : agrupadas.entrySet()) {
            Sheet sheet = workbook.createSheet(entry.getKey());
            Row header = sheet.createRow(0);

            String[] columnas = {"Fecha Alta", "Descripción", "Cantidad", "Eliminado", "Usuario Alta", "Usuario Modificación"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowNum = 1;
            for (InventarioDonaciones d : entry.getValue()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(d.getFechaHoraAlta()));
                row.createCell(1).setCellValue(d.getDescripcion());
                row.createCell(2).setCellValue(d.getCantidad());
                row.createCell(3).setCellValue(d.isEliminado());
                row.createCell(4).setCellValue(String.valueOf(d.getUsuarioAltaId()));
                row.createCell(5).setCellValue(String.valueOf(d.getUsuarioModificadoId()));
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
