public enum ExpenseCategory {

    Non_Payroll_Broadband("Non Payroll - Broadband"),
    Non_Payroll_Health_And_Wellness("Non Payroll - Health & Wellness"),
    Non_Payroll_CABS("Non Payroll - Cabs"),
    Non_Payroll_Office_Lunch_And_Snacks("Non Payroll - Office Expenses (Lunch/Snacks)"),
    Non_Payroll_Office_Tea_And_Coffee("Non Payroll - Office Expenses (Tea/Coffee)"),
    Non_Payroll_Office_Broadband("Non Payroll - Office Expenses (Broadband)"),
    Non_Payroll_Office_Electricity("Non Payroll - Office Expenses (Electricity Bill)"),
    Non_Payroll_Office_Domain("Non Payroll - Office Expenses (Domain)"),
    Non_Payroll_Office_Maintainance("Non Payroll - Office Expenses (Maintainance)"),
    Non_Payroll_Office_Team_Outing("Non Payroll - Office Expenses (Team outing)"),
    Non_Payroll_Office_Hardware_And_Software("Non Payroll - Office Expenses (Hardware/Software)"),
    Non_Payroll_Onsite_Travel("Non Payroll - Onsite Travel Expenses"),
    Non_Payroll_Training_And_Seminars("Non Payroll - Training/Seminars"),
    Non_Payroll_Domestic_Travel("Non Payroll - Domestic Travel Expenses"),

    Payroll_Mobile("Payroll - Mobile Bill"),
    Payroll_Books_And_Periodicals("Payroll - Books & Periodicals"),
    Payroll_Medical("Payroll - Medical");

    private String expenseType;

    ExpenseCategory(String s) {
        expenseType = s;
    }

    @Override
    public String toString() {
        return expenseType;
    }
}
