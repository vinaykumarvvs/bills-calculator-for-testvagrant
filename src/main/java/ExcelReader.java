import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelReader {

    private Workbook workbook;
    private Sheet sheet;
    private String excelFilePath;

    public ExcelReader(String fileName) throws IOException {
//        String partialPath = File.separator; // Comment this line for local execution and un-comment next line
        String partialPath = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "payrollFiles" + File.separator;
        excelFilePath = System.getProperty("user.dir") + partialPath + fileName + ".xlsx";

        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        workbook = new XSSFWorkbook(inputStream);
        sheet = workbook.getSheetAt(0);
    }

    public List<NonPayRollBill> getAllEmployeesBillDetails(int year, int month) {

        int startingRow = sheet.getFirstRowNum() + 1;
        int endingRow = sheet.getLastRowNum() + 1;

        List<NonPayRollBill> empList = new ArrayList<>();
        String email, expenseCategory;
        Date timestamp;
        float amount;

        while (startingRow < endingRow) {
            sheet.getRow(startingRow).getCell(0).setCellType(Cell.CELL_TYPE_NUMERIC);
            timestamp = sheet.getRow(startingRow).getCell(0).getDateCellValue();

            if (filterRecordsBasedOnMonthAndYear(timestamp, year, month)) {
                email = sheet.getRow(startingRow).getCell(1).toString();
                expenseCategory = sheet.getRow(startingRow).getCell(3).toString();
                sheet.getRow(startingRow).getCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
                amount = Float.parseFloat(sheet.getRow(startingRow).getCell(4).toString());

                empList.add(new NonPayRollBillBuilder()
                        .withDate(timestamp)
                        .withEmail(email)
                        .withCategory(expenseCategory)
                        .withAmount(amount)
                        .build());
            }

            startingRow++;
        }
        return empList;
    }

    private boolean filterRecordsBasedOnMonthAndYear(Date date, int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return year == c.get(Calendar.YEAR) && month - 1 == c.get(Calendar.MONTH);
    }

    private int getCellNumberBasedOnExpenseCategory(ExpenseCategory expenseCategory) {
        if (expenseCategory == ExpenseCategory.Non_Payroll_Health_And_Wellness)
            return 1;
        else if (expenseCategory == ExpenseCategory.Non_Payroll_Broadband)
            return 2;
        return 0;
    }

    private int getCellNumberBasedOnClaimedDate(ExpenseCategory expenseCategory) {
        if (expenseCategory == ExpenseCategory.Non_Payroll_Health_And_Wellness)
            return 4;
        else if (expenseCategory == ExpenseCategory.Non_Payroll_Broadband)
            return 5;
        return 0;
    }

    public String checkEligibilityForHealthAndBroadband(String email, double claimedAmount, ExpenseCategory expenseCategory, List<Date> datesList) {
        int cellNumber = getCellNumberBasedOnExpenseCategory(expenseCategory);
        int startingRow = sheet.getFirstRowNum() + 1;
        int endingRow = sheet.getLastRowNum() + 1;
        float amountFromExcel = 0;
        boolean amountShouldBeClaimedOrNot = false;

        while (startingRow < endingRow) {
            if (sheet.getRow(startingRow).getCell(0).toString().equals(email)) {
                checkOneYearIsCompletedOrNot(sheet, email);
                amountShouldBeClaimedOrNot = checkAmountIsClaimedOrNot(sheet.getRow(startingRow), datesList, expenseCategory);
                if (amountShouldBeClaimedOrNot)
                    amountFromExcel = Float.parseFloat(sheet.getRow(startingRow).getCell(cellNumber).toString());
            }
            startingRow++;
        }

        float claimedAmountAfterVerification = 0;
        if (amountShouldBeClaimedOrNot && amountFromExcel > 0 && amountFromExcel >= claimedAmount) {
            double updateAmount = amountFromExcel - claimedAmount;
            claimedAmountAfterVerification = (float) claimedAmount;
            updateHealthOrBroadbandValueInExcel(sheet, email, (float) updateAmount, cellNumber);
        } else if (amountShouldBeClaimedOrNot && amountFromExcel > 0 && amountFromExcel < claimedAmount) {
            claimedAmountAfterVerification = amountFromExcel;
            updateHealthOrBroadbandValueInExcel(sheet, email, 0, cellNumber);
        }

        if (claimedAmountAfterVerification == 0)
            return String.valueOf(claimedAmountAfterVerification) + ":" + String.valueOf(claimedAmount);
        else
            return String.valueOf(claimedAmountAfterVerification);
    }

    private boolean checkAmountIsClaimedOrNot(Row row, List<Date> datesList, ExpenseCategory expenseCategory) {
        int cellNumber = getCellNumberBasedOnClaimedDate(expenseCategory);
        Date dateFromExcel = null;
        try {
            dateFromExcel = row.getCell(cellNumber).getDateCellValue();
        } catch (Exception ignored) {
        }
        boolean claimed = false;
        if (dateFromExcel == null) {
            claimed = true;
        } else {
            for (Date date : datesList) {
                if (dateFromExcel.before(date)) {
                    claimed = true;
                    row.getCell(cellNumber).setCellValue(date);
                }
            }
        }
        return claimed;
    }

    private synchronized void updateHealthOrBroadbandValueInExcel(Sheet sheet, String email, float updateAmount, int cellNumber) {
        int startingRow = sheet.getFirstRowNum() + 1;
        int endingRow = sheet.getLastRowNum() + 1;

        while (startingRow < endingRow) {
            if (sheet.getRow(startingRow).getCell(0).toString().equals(email)) {
                sheet.getRow(startingRow).getCell(cellNumber).setCellType(Cell.CELL_TYPE_NUMERIC);
                sheet.getRow(startingRow).getCell(cellNumber).setCellValue((updateAmount));
                closeExcelFile();
            }
            startingRow++;
        }
    }

    private void checkOneYearIsCompletedOrNot(Sheet sheet, String email) {
        int startingRow = sheet.getFirstRowNum() + 1;
        int endingRow = sheet.getLastRowNum() + 1;

        while (startingRow < endingRow) {
            if (sheet.getRow(startingRow).getCell(0).toString().equals(email)) {
                String activationDate = sheet.getRow(startingRow).getCell(3).toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(activationDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long oneYear = (new Date().getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
                if (oneYear / (float) 365 > 1) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, 365);
                    sheet.getRow(startingRow).getCell(1).setCellValue(6000);
                    sheet.getRow(startingRow).getCell(2).setCellValue(12000);
                    sheet.getRow(startingRow).getCell(3).setCellValue(simpleDateFormat.format(c.getTime()));
                    closeExcelFile();
                }
            }

            startingRow++;
        }
    }

    private void closeExcelFile() {
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}