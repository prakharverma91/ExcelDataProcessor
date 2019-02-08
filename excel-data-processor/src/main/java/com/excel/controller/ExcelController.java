package com.excel.controller;


import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excel.service.ExcelService;

@RestController
@RequestMapping("api/v1/")
public class ExcelController {

	private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

	@Autowired
	private ExcelService excelService;
	
	@GetMapping(value="excel/data/process")
	public ResponseEntity<Object> processExcelData(){
		log.info("Inside the processExcelData API");
		
		try {
			excelService.processExcelData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return new ResponseEntity<Object>("Excel data processed successfully", HttpStatus.OK);
		
	}
	
}
