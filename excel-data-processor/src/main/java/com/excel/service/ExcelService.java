package com.excel.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.excel.ExcelDataProcessorApplication;
import com.excel.constant.ExcelConstant;
import com.excel.controller.ExcelController;
import com.excel.util.ExcelUtil;

@Service
public class ExcelService {

	@Autowired
	ResourceLoader resourceLoader;
	private static final Logger log = LoggerFactory.getLogger(ExcelService.class);


	Map<String,Double> getExchangeRate() throws InvalidFormatException, IOException{

		Resource banner = resourceLoader.getResource("classpath:"+ExcelConstant.EXCHANGE_RATES_PATH);
		Workbook workbook = new XSSFWorkbook(banner.getFile());

		if(workbook.getNumberOfSheets()<1){
			log.error("Sheet not exixt in the file : {}",ExcelConstant.EXCHANGE_RATES_PATH);
		}

		DataFormatter dataFormatter = new DataFormatter();

		System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");
		Map<String,Double> exchangeRateMap = new HashMap<String,Double>();

		boolean isHeader = true;
		for (Row row: workbook.getSheetAt(0)) {
			if(isHeader){
				isHeader=false;
				continue;
			}

			exchangeRateMap.put(row.getCell(0).toString().trim(), ExcelUtil.parseIntoDouble(row.getCell(1).toString()));
			for(Cell cell: row) {
				String cellValue = dataFormatter.formatCellValue(cell);
				System.out.print(cellValue + "\t");
			}
			System.out.println();
		}

		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		return exchangeRateMap;
	}

	public void processExcelData() throws InvalidFormatException, IOException{

		Map<String,Double> exchangeRateMap = getExchangeRate();
		log.info("ExchangeRateMap  : {}",exchangeRateMap);

		log.info("Try to read file : {}",ExcelConstant.PRODUCT_LIST_PRICE_PATH);
		Resource productListPriceResource = resourceLoader.getResource("classpath:"+ExcelConstant.PRODUCT_LIST_PRICE_PATH);
		Workbook productListPriceWorkbook = new XSSFWorkbook(productListPriceResource.getFile());

		if(productListPriceWorkbook.getNumberOfSheets()<1){
			log.error("Sheet not exixt in the file : {}",ExcelConstant.PRODUCT_LIST_PRICE_PATH);
		}

		log.info("Try to read file : {}",ExcelConstant.PRODUCTS_INR_PRICE_PATH);
	//	Resource productInrPriceResource = resourceLoader.getResource("classpath:"+ExcelConstant.PRODUCTS_INR_PRICE_PATH);
		Workbook productInrPriceWorkbook = new XSSFWorkbook();

		Sheet sheet = productInrPriceWorkbook.createSheet();
		createHeaderRow(sheet,productInrPriceWorkbook);

		
		DataFormatter dataFormatter = new DataFormatter();

		System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");

		int i=0;
		for (Row row: productListPriceWorkbook.getSheetAt(0)) {
			if(i==0){
				i++;
				continue;
			}
            
			String pid = row.getCell(1).toString().trim();
			String currency = row.getCell(4).toString().trim();
			Double price = null;
			log.info("pid : {}  , currency : {} , price : {}",pid,currency,price);

			try{
			price = ExcelUtil.parseIntoDouble(row.getCell(3).toString().trim());
			}catch (Exception e) {
				log.error("Exception occur while convert value : {} into double ",row.getCell(3));
				continue;
			}
			log.info("pid : {}  , currency : {} , price : {}",pid,currency,price);

			Row r = sheet.createRow(i);
			Cell c = r.createCell(0);
			c.setCellValue(pid);
			c = r.createCell(1);
			c.setCellValue("INR");
			c = r.createCell(2);
			Double inrValue = getPriceByExchangerate(exchangeRateMap,price,currency);
			c.setCellValue(inrValue);

			System.out.println();
			i++;
		}

		updateValueOfInrPriceList(productInrPriceWorkbook,productListPriceResource);
		
	}

	private void createHeaderRow(Sheet s,Workbook workbook) {
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		cs.setAlignment(HorizontalAlignment.LEFT);

		Row r = s.createRow(0);
		r.setRowStyle(cs);

		Cell c = r.createCell(0);
		c.setCellValue("pid");
		//	     s.setColumnWidth(0, poiWidth(18.0));
		c = r.createCell(1);
		c.setCellValue("price");
		//    s.setColumnWidth(1, poiWidth(24.0));
		c = r.createCell(2);
		c.setCellValue("currency");

	}

	Double getPriceByExchangerate(Map<String,Double> exchangeRateMap,Double price,String curreny){
	
		log.info("Try to convert current value into a inr value ");
		
		if(curreny.equals("INR")){
			return price;
		}
		
		return exchangeRateMap.get(curreny.trim()) * price;
	}
	
	public void updateValueOfInrPriceList(Workbook workbook,Resource productInrPriceResource ) throws IOException {
	    if (workbook == null) {
	        return;
	    }
	    try {
	    	log.info("uri is : {}",productInrPriceResource.getURI());
	    	String path = productInrPriceResource.getFile().getParentFile().getAbsolutePath().toString();
	    	log.info("path : {}",path);
	    	  FileOutputStream fileOut = new FileOutputStream(path+ExcelConstant.PRODUCTS_INR_PRICE_PATH);
	          workbook.write(fileOut);
	          fileOut.close();
	          // Closing the workbook
	          workbook.close();
	     
	    } catch (IOException ex) {
	        throw new IOException("Error writing to output file", ex);
	    }
	}
	
}
