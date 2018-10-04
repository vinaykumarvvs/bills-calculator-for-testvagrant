import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BillCalculatorForTestVagrant {

    private List<BillDetails> singleEmployeeList;
    private List<Double> nonPayrollAmountList;
    private List<Double> payrollAmountList;
    private List<Date> broadbandDatesList;
    private List<Date> healthAndWellnessDatesList;

    public static void main(String[] args) throws IOException {

        BillCalculatorForTestVagrant billCalculatorForTestVagrant = new BillCalculatorForTestVagrant();

        Scanner in = new Scanner(System.in);
        String fileName = billCalculatorForTestVagrant.readFileName(in);
        String email = billCalculatorForTestVagrant.readEmail(in);
        int expenseType = billCalculatorForTestVagrant.readExpenseType(in);
        int year = billCalculatorForTestVagrant.readYear(in);
        int month = billCalculatorForTestVagrant.readMonth(in);

        billCalculatorForTestVagrant.calculateEmployeeBills(fileName, email, year, month, expenseType);
//        System.out.println("Press any key to exit ...");
//        System.console().readLine();
    }

    private String readFileName(Scanner in) {
        System.out.println("Enter FileName : ");
        return in.nextLine();
    }

    private String readEmail(Scanner in) {
        System.out.println("Enter 'all' or 'particular-employee-email' to calculate the bills");
        return in.nextLine();
    }

    private int readExpenseType(Scanner in) {
        System.out.println("Select the Expense Type, Enter 1 or 2");
        System.out.println("1. Payroll");
        System.out.println("2. Non-Payroll");
        return in.nextInt();
    }

    private int readYear(Scanner in) {
        System.out.println("Enter Year : ");
        return in.nextInt();
    }

    private int readMonth(Scanner in) {
        System.out.println("Enter Month : ");
        return in.nextInt();
    }

    private void calculateEmployeeBills(String fileName, String email, int year, int month, int expenseType) throws IOException {
        List<BillDetails> empList = new ExcelReader(fileName).getAllEmployeesBillDetails(year, month);
        if (email.contains("@")) {
            calculateBillForEachEmployee(email, empList, expenseType);
            totalAmountClaimedForEachEmployee(expenseType);
        } else
            calculateBillForAllEmployees(empList, expenseType);
    }

    private void calculateBillForAllEmployees(List<BillDetails> empList, int expenseType) {
        while (empList.size() != 0) {
            String email = empList.get(0).getEmail();
            calculateBillForEachEmployee(email, empList, expenseType);
            totalAmountClaimedForEachEmployee(expenseType);
            empList.removeIf(emp -> emp.getEmail().equals(email));
        }
    }

    private void calculateBillForEachEmployee(String email, List<BillDetails> empList, int expenseType) {

        System.out.println("============================================================");
        System.out.format("Employee : %s ", email);
        System.out.println();
        System.out.println("------------------------------------------------------------");

        singleEmployeeList = empList.stream()
                .filter(emp -> emp.getEmail().equals(email))
                .collect(Collectors.toList());

        nonPayrollAmountList = new ArrayList<>();
        payrollAmountList = new ArrayList<>();
        broadbandDatesList = new ArrayList<>();
        healthAndWellnessDatesList = new ArrayList<>();

        while (singleEmployeeList.size() != 0) {
            String category = singleEmployeeList.get(0).getCategory();
            if (ExpenseCategory.Non_Payroll_Broadband.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Broadband.toString()) &&
                                broadbandDatesList.add(emp.getDate()))
                        .mapToDouble(BillDetails::getAmount).sum();

                if (expenseType == 2) {

                    total = verifyAmountFromExcelForHealthAndBroadband(singleEmployeeList, total, ExpenseCategory.Non_Payroll_Broadband, broadbandDatesList);
                    singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));


                    if (total == 0)
                        System.out.println(ExpenseCategory.Non_Payroll_Broadband.toString() + " : " + total +
                                " ( In Sufficient Balance )");
                    else
                        System.out.println(ExpenseCategory.Non_Payroll_Broadband.toString() + " : " + total);

                    nonPayrollAmountList.add(total);
                }else
                    singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));

            } else if (ExpenseCategory.Non_Payroll_Health_And_Wellness.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString()) &&
                                healthAndWellnessDatesList.add(emp.getDate()))
                        .mapToDouble(BillDetails::getAmount).sum();

                if (expenseType == 2) {
                    total = verifyAmountFromExcelForHealthAndBroadband(singleEmployeeList, total, ExpenseCategory.Non_Payroll_Health_And_Wellness, healthAndWellnessDatesList);
                    singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));

                    if (total == 0)
                        System.out.println(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString() + " : " + total +
                                " ( In Sufficient Balance )");
                    else
                        System.out.println(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString() + " : " + total);

                    nonPayrollAmountList.add(total);
                }else
                    singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));

            } else if (ExpenseCategory.Non_Payroll_CABS.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_CABS.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Lunch_And_Snacks.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Lunch_And_Snacks.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Tea_And_Coffee.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Tea_And_Coffee.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Broadband.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Broadband.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Electricity.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Electricity.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Domain.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Domain.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Maintainance.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Maintainance.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Team_Outing.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Team_Outing.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Office_Hardware_And_Software.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Office_Hardware_And_Software.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Onsite_Travel.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Onsite_Travel.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Training_And_Seminars.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Training_And_Seminars.toString(), expenseType);

            } else if (ExpenseCategory.Non_Payroll_Domestic_Travel.toString().equals(category)) {
                calculateNonPayRollBills(ExpenseCategory.Non_Payroll_Domestic_Travel.toString(), expenseType);

            } else if (ExpenseCategory.Payroll_Books_And_Periodicals.toString().equals(category)) {
                calculatePayRollBills(ExpenseCategory.Payroll_Books_And_Periodicals.toString(), expenseType);

            } else if (ExpenseCategory.Payroll_Medical.toString().equals(category)) {
                calculatePayRollBills(ExpenseCategory.Payroll_Medical.toString(), expenseType);

            } else if (ExpenseCategory.Payroll_Mobile.toString().equals(category)) {
                calculatePayRollBills(ExpenseCategory.Payroll_Mobile.toString(), expenseType);
            }
        }
    }

    private void calculateNonPayRollBills(String expenseCategory, int expenseType) {
        double total = singleEmployeeList.stream()
                .filter(emp -> emp.getCategory().equals(expenseCategory))
                .mapToDouble(BillDetails::getAmount).sum();

        singleEmployeeList.removeIf(emp -> emp.getCategory().equals(expenseCategory));
        if (expenseType == 2) {
            System.out.println(expenseCategory + " : " + total);
            nonPayrollAmountList.add(total);
        }
    }

    private void calculatePayRollBills(String expenseCategory, int expenseType) {
        double total = singleEmployeeList.stream()
                .filter(emp -> emp.getCategory().equals(expenseCategory))
                .mapToDouble(BillDetails::getAmount).sum();

        singleEmployeeList.removeIf(emp -> emp.getCategory().equals(expenseCategory));
        if (expenseType == 1) {
            System.out.println(expenseCategory + " : " + total);
            payrollAmountList.add(total);
        }
    }

    private void totalAmountClaimedForEachEmployee(int expenseType) {

        if (expenseType == 1) {
            double payrollAmount = payrollAmountList.stream().mapToDouble(i -> i).sum();
            System.out.println("------------------------------------------------------------");
            System.out.println("Total Payroll Amount is : " + payrollAmount);
        } else if (expenseType == 2) {
            double nonPayrollAmount = nonPayrollAmountList.stream().mapToDouble(i -> i).sum();
            System.out.println("------------------------------------------------------------");
            System.out.println("Total NonPayroll Amount is : " + nonPayrollAmount);
        }
        System.out.println("============================================================");
        System.out.println();

    }

    private float verifyAmountFromExcelForHealthAndBroadband(List<BillDetails> singleEmployeeList, double total, ExpenseCategory expenseCategory, List<Date> datesList) {
        String email = singleEmployeeList.get(0).getEmail();
        try {
            if (expenseCategory == ExpenseCategory.Non_Payroll_Health_And_Wellness)
                return new ExcelReader("HealthAndBroadband").checkEligibilityForHealthAndBroadband(email, total, expenseCategory, datesList);
            else if (expenseCategory == ExpenseCategory.Non_Payroll_Broadband)
                return new ExcelReader("HealthAndBroadband").checkEligibilityForHealthAndBroadband(email, total, expenseCategory, datesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
