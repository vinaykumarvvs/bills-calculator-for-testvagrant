import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class NonPayRollBillCalculatorForAnEmp {

    public static void main(String[] args) throws IOException {

        NonPayRollBillCalculatorForAnEmp nonPayRollBillCalculatorForAnEmp = new NonPayRollBillCalculatorForAnEmp();

        Scanner in = new Scanner(System.in);
        String fileName = nonPayRollBillCalculatorForAnEmp.readFileName(in);
        String email = nonPayRollBillCalculatorForAnEmp.readEmail(in);
        int year = nonPayRollBillCalculatorForAnEmp.readYear(in);
        int month = nonPayRollBillCalculatorForAnEmp.readMonth(in);

        nonPayRollBillCalculatorForAnEmp.readReimbursementsForThisEmp(fileName, email, year, month);
    }

    private String readFileName(Scanner in) {
        System.out.println("Enter FileName : ");
        return in.nextLine();
    }

    private String readEmail(Scanner in) {
        System.out.println("Enter Employee Mail-Id : ");
        return in.nextLine();
    }

    private int readYear(Scanner in) {
        System.out.println("Enter Year : ");
        return in.nextInt();
    }

    private int readMonth(Scanner in) {
        System.out.println("Enter Month : ");
        return in.nextInt();
    }

    private void readReimbursementsForThisEmp(String fileName, String email, int year, int month) throws IOException {
        List<NonPayRollBill> empList = new ExcelReader(fileName).getAllEmployeesBillDetails(year, month);
        if (email.contains("@"))
            calculateBillsBasedOnEmail(email, empList);
        else
            calculateAllEmpBills(empList);
    }

    private void calculateBillsBasedOnEmail(String email, List<NonPayRollBill> empList) {

        System.out.println("============================================================");
        System.out.format("Employee : %s ", email);
        System.out.println();
        System.out.println("------------------------------------------------------------");

        List<NonPayRollBill> singleEmployeeList = empList.stream()
                .filter(emp -> emp.getEmail().equals(email))
                .collect(Collectors.toList());

        List<Double> totalAmountList = new ArrayList<>();
        List<Double> nonPayrollAmountList = new ArrayList<>();
        List<Double> payrollAmountList = new ArrayList<>();
        List<Date> broadbandDatesList = new ArrayList<>();
        List<Date> healthAndWellnessDatesList = new ArrayList<>();

        while (singleEmployeeList.size() != 0) {
            String category = singleEmployeeList.get(0).getCategory();
            if (ExpenseCategory.Non_Payroll_Broadband.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(
                                emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Broadband.toString()) &&
                                        broadbandDatesList.add(emp.getDate())
                        )
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                String msg = checkEligibleOrNot(singleEmployeeList, total, ExpenseCategory.Non_Payroll_Broadband, broadbandDatesList);
                total = msg.contains(":") ? Float.parseFloat(msg.split(":")[0]) : Float.parseFloat(msg);
                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));

                if (total == 0)
                    System.out.println(ExpenseCategory.Non_Payroll_Broadband.toString() + " : " + total +
                            " ( The amount " + Float.parseFloat(msg.split(":")[1]) +" is already claimed or no sufficient balance is left )");
                else
                    System.out.println(ExpenseCategory.Non_Payroll_Broadband.toString() + " : " + total);

                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Health_And_Wellness.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(
                                emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString()) &&
                                        healthAndWellnessDatesList.add(emp.getDate())
                        )
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                String msg = checkEligibleOrNot(singleEmployeeList, total, ExpenseCategory.Non_Payroll_Health_And_Wellness, healthAndWellnessDatesList);
                total = msg.contains(":") ? Float.parseFloat(msg.split(":")[0]) : Float.parseFloat(msg);

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                if (total == 0)
                    System.out.println(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString() + " : " + total +
                            " ( The amount " + Float.parseFloat(msg.split(":")[1]) +" is already claimed or no sufficient balance is left )");
                else
                    System.out.println(ExpenseCategory.Non_Payroll_Health_And_Wellness.toString() + " : " + total);

                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_CABS.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_CABS.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_CABS.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Lunch_And_Snacks.toString().equals(category)) {
                double total = (float) singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Lunch_And_Snacks.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Lunch_And_Snacks.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Tea_And_Coffee.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Tea_And_Coffee.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Tea_And_Coffee.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Broadband.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Broadband.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Broadband.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Electricity.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Electricity.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Electricity.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Domain.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Domain.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Domain.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Maintainance.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Maintainance.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Maintainance.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Team_Outing.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Team_Outing.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Team_Outing.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Office_Hardware_And_Software.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Office_Hardware_And_Software.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Office_Hardware_And_Software.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Onsite_Travel.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Onsite_Travel.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Onsite_Travel.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Training_And_Seminars.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Training_And_Seminars.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Training_And_Seminars.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Non_Payroll_Domestic_Travel.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Non_Payroll_Domestic_Travel.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
                System.out.println(ExpenseCategory.Non_Payroll_Domestic_Travel.toString() + " : " + total);
                totalAmountList.add(total);
                nonPayrollAmountList.add(total);

            } else if (ExpenseCategory.Payroll_Books_And_Periodicals.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Payroll_Books_And_Periodicals.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
//                System.out.println(ExpenseCategory.Payroll_Books_And_Periodicals.toString() + " : " + total);
                totalAmountList.add(total);
//                payrollAmountList.add(total);

            } else if (ExpenseCategory.Payroll_Medical.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Payroll_Medical.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
//                System.out.println(ExpenseCategory.Payroll_Medical.toString() + " : " + total);
                totalAmountList.add(total);
//                payrollAmountList.add(total);

            } else if (ExpenseCategory.Payroll_Mobile.toString().equals(category)) {
                double total = singleEmployeeList.stream()
                        .filter(emp -> emp.getCategory().equals(ExpenseCategory.Payroll_Mobile.toString()))
                        .mapToDouble(NonPayRollBill::getAmount).sum();

                singleEmployeeList.removeIf(emp -> emp.getCategory().equals(category));
//                System.out.println(ExpenseCategory.Payroll_Mobile.toString() + " : " + total);
                totalAmountList.add(total);
//                payrollAmountList.add(total);

            }
        }

//        double totalAmount = totalAmountList.stream().mapToDouble(i -> i).sum();
        double nonPayrollAmount = nonPayrollAmountList.stream().mapToDouble(i -> i).sum();
//        double payrollAmount = payrollAmountList.stream().mapToDouble(i -> i).sum();

//        System.out.println("------------------------------------------------------------");
//        System.out.println("Total NonPayroll Amount is  : " + nonPayrollAmount);
//        System.out.println("------------------------------------------------------------");
//        System.out.println("Total Payroll Amount is : " + payrollAmount);
        System.out.println("------------------------------------------------------------");
        System.out.println("Total NonPayroll Amount is : " + nonPayrollAmount);
        System.out.println("============================================================");
        System.out.println();
    }

    private void calculateAllEmpBills(List<NonPayRollBill> empList) {
        while (empList.size() != 0) {
            String email = empList.get(0).getEmail();
            calculateBillsBasedOnEmail(email, empList);
            empList.removeIf(emp -> emp.getEmail().equals(email));
        }
    }

    private String checkEligibleOrNot(List<NonPayRollBill> singleEmployeeList, double total, ExpenseCategory expenseCategory, List<Date> datesList) {
        String email = singleEmployeeList.get(0).getEmail();
        try {
            if (expenseCategory == ExpenseCategory.Non_Payroll_Health_And_Wellness)
                return new ExcelReader("HealthAndBroadband").checkEligibilityForHealthAndBroadband(email, total, expenseCategory, datesList);
            else if (expenseCategory == ExpenseCategory.Non_Payroll_Broadband)
                return new ExcelReader("HealthAndBroadband").checkEligibilityForHealthAndBroadband(email, total, expenseCategory, datesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
