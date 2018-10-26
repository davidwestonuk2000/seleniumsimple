package com.pancentric.utilities;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

import java.net.*;

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.*;


import javax.swing.JOptionPane;


import jxl.read.biff.BiffException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFRow;

import org.joda.time.Days;

public class ExcelFetcher {

        private Workbook openFile(String filepath) throws BiffException, IOException {
            Workbook workbook;
            
            if (filepath.contains(".xlsx")) {
                 workbook = openXlsxFile(filepath);
            }
            
            else {
                workbook = openXlsFile(filepath);
            }
            return workbook;
        }
        

    private Workbook openXlsFile(String filepath) throws BiffException, IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        HSSFWorkbook xlsWorkbook = new HSSFWorkbook(file);
        return xlsWorkbook;
    }
    private Workbook openXlsxFile(String filepath) throws BiffException, IOException {
        FileInputStream file = new FileInputStream(new File(filepath));
        XSSFWorkbook xlsxWorkbook = new XSSFWorkbook(file);
        return xlsxWorkbook;
    }

    // to select an excel worksheet by name

    private Sheet selectSheet(Workbook workbook, String sheetname) {
        
        if (sheetname == "") {
            sheetname = "Sheet1";
        }
        
        if (sheetname == "First") {
            sheetname = getFirstSheet(workbook);
        }
        
        Sheet sheet = workbook.getSheet(sheetname);
        return sheet;
    }
    
    // get the first sheet
    
    private String getFirstSheet(Workbook workbook) {
        String sheetname = workbook.getSheetName(0);
        return sheetname;
    }
    
    // to get the number of rows in an excel file

    private int numberOfRows(Sheet sheet) {
        int numberOfRows = sheet.getLastRowNum();
        return numberOfRows + 1;
    }
    
    // to get the number of columns in an excel file

    private int numberOfColumns(Sheet sheet) {
        int numberOfColumns = sheet.getRow(0).getLastCellNum();
        return numberOfColumns + 1;
    }
    private String getDataItem(Sheet sheet, int column, int row) {  

           String dataItem = "";
           Cell cell = sheet.getRow(row).getCell(column);
           //Must do this, you need to get value based on the cell type
                switch (cell.getCellType()) {
                      
                      case Cell.CELL_TYPE_NUMERIC:


                          double i = cell.getNumericCellValue();
                           //int j = (int) i;
                           dataItem = Double.toString(i);
                           if (dataItem.endsWith(".0")) {
                               dataItem = dataItem.replace(".0", "");
                           }
                      break;

                      case Cell.CELL_TYPE_STRING:

                          dataItem = cell.getStringCellValue();
                      break;

                       case Cell.CELL_TYPE_FORMULA:
                          if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                                  switch(cell.getCachedFormulaResultType()) {
                                      case Cell.CELL_TYPE_NUMERIC:
                                      double a = cell.getNumericCellValue();
                                       int b = (int) a;
                                       dataItem = Integer.toString(b);
                                          break;
                                      case Cell.CELL_TYPE_STRING:
                                          dataItem = cell.getRichStringCellValue().toString();
                                          break;
                                  }
                           }
                       break;
                  
                      case Cell.CELL_TYPE_ERROR:
                              dataItem = "Error";
                          break;
    
                          default: 
                               dataItem = cell.getRichStringCellValue().toString();
                          break;
                  }
           
           return dataItem;
       }

    public String getCellData(String filepath,String sheetname, int column, int row) throws Exception {
        
        System.out.println("entering the method <getcelldata>");
        System.out.println("file path is" +filepath);
        System.out.println("sheet name is" +sheetname);
        System.out.println("column number is" +column);
        System.out.println("row number is" +row);
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);
        String  cellData  = getDataItem(sheet,column,row);
        System.out.println("the name is " + cellData );
        return cellData;
    }

    // to update the column of a cell

    public void incrementCellValue(String filepath, String sheetname, String searchRow, String searchColumn, String startColumn) throws Exception {
        Workbook workbook = openFile(filepath);
        
        int columnStart = 0;
        Sheet sheet = selectSheet(workbook, sheetname);
        if(startColumn!="") {
            columnStart=findColumnIndex(sheet,startColumn,0);
        }
        int row = findRowIndex(sheet,searchRow,columnStart);
        int column = (findColumnIndex(sheet,searchColumn,0));
        Long result = Long.valueOf((getDataItem(sheet,column,row))).longValue();
        result++;
        
        FileInputStream file = new FileInputStream(new File(filepath));
        //HSSFWorkbook writeWorkbook = new HSSFWorkbook(file);
        
        //HSSFSheet worksheet = writeWorkbook.getSheet(sheetname);
        
        Cell cell = null; // declare a Cell object   
        cell = sheet.getRow(row).getCell(column);   // Access the second cell in second row to update the value
        cell.setCellValue(Long.toString(result));
        
        FileOutputStream outFile =new FileOutputStream(new File(filepath));
        workbook.write(outFile);
        outFile.close();
    }
  
  // update style of the cell
 // TODO allow passing in of required style
  public void updateCellStyleToDate(String filepath, String sheetname, int row, int column, String dateFormat) throws Exception {
      System.out.println("the number of row is"  + row);
       Workbook workbook = openFile(filepath);
       
       Sheet sheet = selectSheet(workbook, sheetname);
       Cell cell = null; // declare a Cell object  
       cell = sheet.getRow(row).getCell(column);
       CellStyle cellStyle = workbook.createCellStyle();
       CreationHelper createHelper = workbook.getCreationHelper();
       cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));
       cell.setCellStyle(cellStyle);
       
       // read the string value of the cell
       String cellValue = cell.getStringCellValue();
       // parse the string as a date
       DateFormat format = new SimpleDateFormat(dateFormat);
       Date date = format.parse(cellValue);
       System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
         // set the cell value
       cell.setCellValue(date);
       
       FileOutputStream outFile =new FileOutputStream(new File(filepath));
       workbook.write(outFile);
       outFile.close();
   }

  // to find the index of the cell
  
  public HashMap<String,String> cellIndex(String sheetname, String cellContent,String filepath) throws Exception {
      System.out.println("entering method <cellIndex>");
      System.out.println("the sheet name is " + sheetname);
      System.out.println("the cell content is " + cellContent);
      System.out.println("the filepath is " + filepath);
      Workbook workbook = openFile(filepath);
      
      Sheet sheet = selectSheet(workbook, sheetname);
      int rowNum = numberOfRows(sheet);
      int colNum = numberOfColumns(sheet);
    
      HashMap<String,String> index = new HashMap<String,String>();
    // String[][] data = null;
     boolean loopBreak = false;
  
      for (int i = 0 ; i < rowNum ; i++) {
            Row row =sheet.getRow(i);
            for (int j = 0 ; j < colNum ; j++) {
             Cell  cell = row.getCell(j);
               RichTextString value =  cell.getRichStringCellValue();
                // String value = getCellData(filepath,sheetname, j, i);
              //   if(value.equalsIgnoreCase(cellContent))
             {
                     System.out.println("The index of cell is " +i);
                      System.out.println("The index of cell is " +j);
                      index.put("row","i");
                      index.put("column","j");
                      loopBreak = true;
                      break;
                  }
              }
          if(loopBreak) break;
         
          }
      System.out.println("exiting  method <cellIndex>");
      return index;   
  }
  
    //to find the text
    public void  findText(String sheetname, String cellContent,String filepath) throws Exception {
        System.out.println("entering method findText");
        Workbook workbook = openFile(filepath);
        
        Sheet sheet = selectSheet(workbook, sheetname);
        boolean loopBreak = false;
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    System.out.println("The cell content is " + cell.getRichStringCellValue().getString().trim());
                    if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                        System.out.println("The search was successful"); 
                        loopBreak = true;
                        break;
                        
                    }
                }
            }
        if(loopBreak) break;
                       
        }
    }

    // update cell with value 
    public void updateCell(String filepath, String sheetname, int row, int column, String newValue) throws Exception {
        
        Workbook workbook = openFile(filepath);
        System.out.println(row);
        System.out.println(column);
        System.out.println(newValue);
        
        Sheet sheet = selectSheet(workbook, sheetname);
        
        Cell cell = null; // declare a Cell object   
        cell = sheet.getRow(row).getCell(column);   // Access the second cell in second row to update the value
        cell.setCellValue(newValue);
        
        System.out.println(newValue);
        Sheet sheet0 = cell.getSheet();
        System.out.println("The sheet name is " + sheet0 );


        FileOutputStream outFile =new FileOutputStream(new File(filepath));
        workbook.write(outFile);
        outFile.close();
    }
    
    // to update the column of a cell

    public void updateCellValue(String filepath, String sheetname, String searchRow, String searchColumn, String startColumn, String newValue) throws Exception {
        
        Workbook workbook = openFile(filepath);

        int columnStart = 0;
        Sheet sheet = selectSheet(workbook, sheetname);
        if(startColumn!="") {
            System.out.println("here" + startColumn);
            
            columnStart=findColumnIndex(sheet,startColumn,0);
        }
        System.out.println("Start"+columnStart);
        int row = findRowIndex(sheet,searchRow,columnStart);
        System.out.println("Row"+row);
        int column = (findColumnIndex(sheet,searchColumn,0));
        System.out.println("Column"+column);

        Cell cell = null; // declare a Cell object   
        cell = sheet.getRow(row).getCell(column);   // Access the second cell in second row to update the value
        cell.setCellValue(newValue);
        System.out.println(newValue);

        
        FileOutputStream outFile =new FileOutputStream(new File(filepath));
        workbook.write(outFile);
        outFile.close();
    }
    
    // to locate a column header within a file and return index
    
    private int findColumnIndex(Sheet sheet, String searchTerm, int row) throws Exception {
        // get the max number of columns
        int maxColumns = (numberOfColumns(sheet));
        // set the start counters
        int column = 0;
        boolean found = false;
        // loop through all columns
        while ((column < maxColumns)) {
            // check the column header
            if((getDataItem(sheet,column,row)).equals(searchTerm)) {
                // if the header matches stop the search
                found = true;
                break;    
            }
            else {
                column++;
            }
        }
        if (found == false) {
            // return 0 for header not found
            column = 0;
        }
        return column;
    }
    
    // to locate a column header within a file and return index
    
    public int findColumnIndex(String filepath, String sheetname, String searchTerm, int row) throws Exception {
        
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);
        
        // get the max number of columns
        int maxColumns = (numberOfColumns(sheet));
        // set the start counters
        int column = 0;
        boolean found = false;
        // loop through all columns
        while ((column < maxColumns)) {
            // check the column header
            if((getDataItem(sheet,column,row)).equals(searchTerm)) {
                // if the header matches stop the search
                found = true;
                break;    
            }
            else {
                column++;
            }
        }
        if (found == false) {
            // return -1 for header not found
            column = -1;
        }
        return column;
    }
    
    
    // to locate a rowr within a column and return index
    
    public int findRowIndex(Sheet sheet, String searchTerm, int column) throws Exception {
        // get the max number of columns
        int maxRows = (numberOfRows(sheet));
        // set the start counters
        int row = 1;
        boolean found = false;
        // loop through all columns
        while ((row < maxRows)) {
            // check the column header
            if((getDataItem(sheet,column,row)).equals(searchTerm)) {
                // if the header matches stop the search
                found = true;
                break;
            }
            else {
                row++;
            }
        }
        if (found == false) {
            // return 0 for header not found
            row = 0;
        }
        return row;
    }

    // public method to extract data at a given index
    public String returnDataValue(String filepath, String sheetname, int column, int row) throws BiffException, IOException {
        
        Workbook workbook = openFile(filepath);
        
        Sheet sheet = selectSheet(workbook, sheetname);
        if (column == 0) {
            column = 1 + (int)(Math.random() * (numberOfColumns(sheet)));
        }
        if (column == 0) {
            column = 1 + (int)(Math.random() * (numberOfRows(sheet)));
        }
        String data = getDataItem(sheet, column - 1, row - 1);
        return data;
    }
    
    // return all fields as a list array

    public HashMap<String,String[]> getAllFields(String filepath, String sheetname, String recordtype, String[] indexColumns, boolean padded) throws Exception {
        // hashmap to store field value
        HashMap<String, String[]> fields = new HashMap();
        // open excel file
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);
        
        // get the max number of rows
        int maxRows = (numberOfRows(sheet));
        // find the name column
        int nameColumn=findColumnIndex(sheet,indexColumns[0],0);
        
        // find the final value column
        int valueColumn=findColumnIndex(sheet,indexColumns[1],0);
        
        // find the record type column
        int recordTypeColumn=findColumnIndex(sheet,indexColumns[2],0);
        
        // find the size column
        int sizeColumn=findColumnIndex(sheet,indexColumns[3],0);
        
        // find the data type column
        int dataTypeColumn=findColumnIndex(sheet,indexColumns[4],0);
        
        // set the start counters
        int row = 1;
        int counter = 0;
        // loop  through all rows
        while (row < maxRows) {
            // array in which to store field values
            String fieldValues[] = new String[4];
            // add the column values to the array if the correct record type
            if((recordtype.equals(""))||(recordtype.equals(getDataItem(sheet,recordTypeColumn,row)))) {
                // get the field index
                fieldValues[0]=Integer.toString(counter);
                // get the value column
                fieldValues[1]=getDataItem(sheet,valueColumn,row);
                // if cell requires padding add as necessary
                if (sizeColumn>0) {
                    // get the required field lengh
                    String length=(getDataItem(sheet,sizeColumn,row));
                    String dataType = (getDataItem(sheet,dataTypeColumn,row));
                    // add the necessary padding
                    if(padded==true) {
                        fieldValues[1] = getPaddedString(fieldValues[1],length,dataType);
                    }
                    // add values into array for reuse
                    fieldValues[2] = length;
                    fieldValues[3] = dataType;
                }
                // add the field to the list
                fields.put((getDataItem(sheet,nameColumn,row)), fieldValues);
                // increase the array counter
                counter++;
            }
            // go to the next row
            row++;
            
        }
        return fields;
    }
    
    // return a specific field as an array object
    
    // return all fields as a list array
        
        public HashMap<String,HashMap<String,String>> getAllFieldsNew(String filepath, String sheetname, String keyString, String[] indexColumns) throws Exception {
            
            evaluateAll(filepath);
            
            // hashmap to store field value
            HashMap<String,HashMap<String,String>> fields = new HashMap<String,HashMap<String,String>>();
            
            // open excel file
            Workbook workbook = openFile(filepath);
            Sheet sheet = selectSheet(workbook, sheetname);
            
            // get the max number of rows
            int maxRows = (numberOfRows(sheet));
            
            // set the start counters
            int row = 1;
            System.out.println(maxRows);
            // loop  through all rows
            while (row < maxRows) {
                
                int keyColumn = findColumnIndex(sheet,keyString,0);
                String fieldName = (getDataItem(sheet,keyColumn,row));
                
                // array in which to store field values
                HashMap<String,String> fieldValues = new HashMap<String,String>();
                
                int a = 0;
                while (a < indexColumns.length) {
                    int columnLookup = findColumnIndex(sheet,indexColumns[a],0);
                    fieldValues.put(indexColumns[a], getDataItem(sheet,columnLookup,row));
                    a++;    
                }
                // add the field to the list
                fields.put(fieldName, fieldValues);
            
                // go to the next row
                row++;            
            }
            return fields;
        }
    
    // return all fields as a list array

    public HashMap<String,String[]> getSingleField(String filepath, String sheetname, String fieldName, String recordType, String[] indexColumns, boolean padded) throws Exception {
        // hashmap to store field value
        HashMap<String, String[]> fields = new HashMap();
        // open excel file
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);

        // get the max number of rows
        int maxRows = (numberOfRows(sheet));

        // find the name column
        int nameColumn=findColumnIndex(sheet,indexColumns[0],0);

        // find the final value column
        int valueColumn=findColumnIndex(sheet,indexColumns[1],0);

        // find the record type column
        int recordTypeColumn=findColumnIndex(sheet,indexColumns[2],0);

        // find the size column
        int sizeColumn=findColumnIndex(sheet,indexColumns[3],0);

        // find the data type column
        int dataTypeColumn=findColumnIndex(sheet,indexColumns[4],0);

        // find the start position column
        int startPositionColumn=findColumnIndex(sheet,indexColumns[5],0);

        // find the end position column
        int endPositionColumn=findColumnIndex(sheet,indexColumns[6],0);

        // array to store propertiess
        String fieldValues[] = new String[6];

        // set the start counters
        int row = 1;
        int counter = 0;
        // loop  through all rows
        field_loop:
        while (row < maxRows) {
            // add the column values to the array if the correct record type
            if((recordType.equals(getDataItem(sheet,recordTypeColumn,row)))&&(fieldName.equals(getDataItem(sheet,nameColumn,row)))) {

                // get the field index
                fieldValues[0]=Integer.toString(counter);
                // get the value column
                fieldValues[1]=getDataItem(sheet,valueColumn,row);
                // if cell requires padding add as necessary
                if (sizeColumn>0) {
                    // get the required field lengh
                    String length=(getDataItem(sheet,sizeColumn,row));
                    String dataType = (getDataItem(sheet,dataTypeColumn,row));
                    // add the necessary padding
                    if(padded==true) {
                        fieldValues[1] = getPaddedString(fieldValues[1],length,dataType);
                    }
                    // add values into array for reuse
                    fieldValues[2] = length;
                    fieldValues[3] = dataType;
                    fieldValues[4] = getDataItem(sheet,startPositionColumn,row);
                    fieldValues[5] = getDataItem(sheet,endPositionColumn,row);
                }

                // add the field to the list
                fields.put((getDataItem(sheet,nameColumn,row)), fieldValues);

                break field_loop;
            }
            // increase the array counter
            counter++;
            // go to the next row
            row++;
        }
        return fields;
    }

    // get a data record
    public List<Map<String,String>> getARandomRecord(String filepath, String sheetname, String recordType, int selectedRow) throws Exception {
        // hashmap to store field value
        List<Map<String,String>> fields = new ArrayList<>();
        // open excel file
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);

        // get the maximum number of columns
        int maxColumns = (numberOfColumns(sheet));

        // set the start counters
        int column = 0;
        // loop  through all rows
        while (column< maxColumns-1) {
                // add the field to the list
                if (getDataItem(sheet,column,1).equals(recordType)) {
                    Map<String,String> temp = new HashMap<>();
                    temp.put("name",(getDataItem(sheet,column,0)));
                    temp.put("value",(getDataItem(sheet,column,selectedRow)));
                    fields.add(temp);
                }
                else {
                    // Do nothing
                }

            // go to the next row
            column++;
        }
        return fields;
    }

    // get a data record
    public Map<String,String> getARandomRecordMap(String filepath, String sheetname, String recordType, int selectedRow) throws Exception {
        // hashmap to store field value
        Map<String,String> fields = new HashMap<>();
        // open excel file
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);

        // get the maximum number of columns
        int maxColumns = (numberOfColumns(sheet));

        // set the start counters
        int column = 0;
        // loop  through all rows
        while (column< maxColumns-1) {
            // add the field to the list
            if (getDataItem(sheet,column,1).equals(recordType)) {
                fields.put((getDataItem(sheet,column,0)),(getDataItem(sheet,column,selectedRow)));
            }
            else {
                // Do nothing
            }

            // go to the next row
            column++;
        }
        return fields;
    }

    // get a data record
    public Map<String,String> getARandomRecordFull(String filepath, String sheetname, int selectedRow) throws Exception {
        // hashmap to store field value
        Map<String,String> fields = new HashMap<>();
        // open excel file
        Workbook workbook = openFile(filepath);
        Sheet sheet = selectSheet(workbook, sheetname);

        // get the maximum number of columns
        int maxColumns = (numberOfColumns(sheet));

        // set the start counters
        int column = 0;
        // loop  through all rows
        while (column< maxColumns-1) {
            // add the field to the list
            fields.put(getDataItem(sheet,column,0),(getDataItem(sheet,column,selectedRow).replace("Yes","true")));
            // go to the next row
            column++;
        }
        return fields;
    }

    // return a list of rows matching a criteria

    // public method to return the maximum number of rows in the file

    public List<Integer> matchingRows(String filepath, String sheetname, String criteria, int column) throws BiffException, IOException {
        Workbook workbook = openFile(filepath);
        List<Integer> matchingRows = new ArrayList<Integer>();
        Sheet sheet = selectSheet(workbook, sheetname);
        int maxRows = numberOfRows(sheet);
        int i = 0;
        while(i < maxRows) {
           if (getDataItem(sheet,column,i).equals(criteria)) {
               matchingRows.add(i);
            }
            i++;
        }
        return matchingRows;
    }

    // public method to return the maximum number of rows in the file

    public int maxRows(String filepath, String sheetname) throws BiffException,
                                                                 IOException {
        Workbook workbook = openFile(filepath);
        
        Sheet sheet = selectSheet(workbook, sheetname);
        int maxRows = numberOfRows(sheet);
        return maxRows;
    }
  
    // return the maximum number of columns

    public int  maxColumns(String filepath, String sheetname) throws BiffException, IOException {
        Workbook workbook = openFile(filepath);
        
        if (sheetname == "") {
            sheetname = "Sheet1";
        }
        Sheet sheet = selectSheet(workbook, sheetname);
        int maxColumns = numberOfColumns(sheet);
        return maxColumns;
    }

    public void deleteFiles(String path) {

        File f = new File(path);
        System.out.println("Path:" + f.getAbsolutePath());

        if (f.exists())
            System.out.println("Book Exists");
        else
            System.out.println("Not Exixts");

        if (f.isFile()) {
            System.out.println("It is File");
        } else
            System.out.println("It is Directory");

        System.out.println(f.isAbsolute());

        if (f.delete()) {
            //
        } else {
            //
        }
    }

    // enum contianing permitted data types
    
    public enum dataTypes {
        INTEGER,
        CHAR,
        VARCHAR,
        DATE,
        FLOAT
      }
    
    // pad a string by the required amount
    
    public String getPaddedString(String original, String requiredLength, String dataType) throws Exception {
        // get int value for target length to use in loop
        int targetLength = Integer.parseInt(requiredLength);
        dataTypes selectedDataType = dataTypes.valueOf(dataType.toUpperCase());
        // loop through until string reaches target length
        while ((original.length())<targetLength) {
            switch (selectedDataType) {
                case INTEGER:
                    // add leading 0
                    original = "0"+original;
                    break;
                case FLOAT:
                    original = "0"+original;
                    break;
                case CHAR:
                    // add trailing space
                    original = original + " ";
                    break;
                case VARCHAR:
                    // add trailing space
                    original = original + " ";
                    break;
                case DATE:
                    // add trailing space
                    original = original + " ";
                    break;
                default:
                    // add trailing space
                    original = original + " ";
                    break;
            }
        }
        if (selectedDataType==dataTypes.FLOAT) {
            StringBuilder floatString = new StringBuilder(original);
            floatString.setCharAt(4, '.');
            original=floatString.toString();
        }   
        return original;
    }
    
    // re evaluate all cell forumla
    
    public void evaluateAll(String filepath) throws BiffException,
                                                              IOException {       
        try {
            FileInputStream file = new FileInputStream(new File(filepath));
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();
            file.close();
             
            FileOutputStream outFile =new FileOutputStream(new File(filepath));
            workbook.write(outFile);
            outFile.close();
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    // reevaluate all cell formula of certain record type
    
    public void evaluateRecord(String filepath, String recordType, String sheetname) throws Exception {       
        
        Workbook workbook = openFile(filepath);

            Sheet sheet = selectSheet(workbook, sheetname);
            
            int maxRows = numberOfRows(sheet);
            int valueColumnIndex = findColumnIndex(sheet, "FinalValue", 0);
            int recordTypeIndex = findColumnIndex(sheet, "RecordType", 0);
            
            FileInputStream file = new FileInputStream(new File(filepath));
            
            HSSFWorkbook writeWorkbook = new HSSFWorkbook(file);
            HSSFSheet worksheet = writeWorkbook.getSheet(sheetname);
            
            FormulaEvaluator evaluator = writeWorkbook.getCreationHelper().createFormulaEvaluator();
            
            
            int row = 1;
            while (row < maxRows) {
                Cell recordTypeCell = null;
                
                recordTypeCell = worksheet.getRow(row).getCell(recordTypeIndex);   // Access the second cell in second row to update the value
                if (recordTypeCell.getStringCellValue().equals(recordType)) {
                    Cell valueCell = null; // declare a Cell object 
                    valueCell = worksheet.getRow(row).getCell(valueColumnIndex);   // Access the second cell in second row to update the value
                    evaluator.evaluate(valueCell);
                }                
                row++;
            }

            FileOutputStream outFile = new FileOutputStream(new File(filepath));
            writeWorkbook.write(outFile);
            outFile.close();
            //System.out.println("done");
    }
    //insert a new row to the excel file
   
    public void insertNewRow(String filepath, String sheetname, int row,int column ,List<String> newValue) throws Exception {
        
        Workbook workbook = openFile(filepath);

        Sheet sheet = selectSheet(workbook, sheetname);
        sheet.createRow(row);
        Cell cell = null; // declare a Cell object
        Cell cellAbove = null;
        Row tempRow = sheet.getRow(row);// row object
        Row refRow = sheet.getRow(2);
        CellStyle currentStyle = null;
        
        System.out.println("MAX COLS:" + column);
        
        for(int i=0;i<column;i++){
            
            // get the style of the cell above
            cellAbove = refRow.getCell(i);
            currentStyle = cellAbove.getCellStyle();

            cell = tempRow.createCell(i);
            String  value = newValue.get(i);
            cell.setCellValue(value);
            cell.setCellStyle(currentStyle);

            
        }
        FileOutputStream outFile =new FileOutputStream(new File(filepath));
        workbook.write(outFile);
        outFile.close();

    }
    
    //delete a row
    public String deleteRow(String filepath, String sheetname, int row, int column)throws Exception {
        Workbook workbook = openFile(filepath);
    
        Sheet sheet = selectSheet(workbook, sheetname);
        Row tempRow =sheet.getRow(row);
        String cellValue = getDataItem(sheet,column,row);
        System.out.println("The cell value is " + cellValue);
        sheet.removeRow(tempRow);
        FileOutputStream outFile =new FileOutputStream(new File(filepath));
        workbook.write(outFile);
        outFile.close();
        return cellValue;

    }    
    
}