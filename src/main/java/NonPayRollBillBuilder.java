import java.util.Date;

public class NonPayRollBillBuilder {

    NonPayRollBill nonPayRollBill = new NonPayRollBill();

    public NonPayRollBillBuilder withEmail(String email) {
        nonPayRollBill.setEmail(email);
        return this;
    }

    public NonPayRollBillBuilder withName(String name) {
        nonPayRollBill.setName(name);
        return this;
    }

    public NonPayRollBillBuilder withAmount(float amount) {
        nonPayRollBill.setAmount(amount);
        return this;
    }

    public NonPayRollBillBuilder withDate(Date date) {
        nonPayRollBill.setDate(date);
        return this;
    }

    public NonPayRollBillBuilder withCategory(String category) {
        nonPayRollBill.setCategory(category);
        return this;
    }

    public NonPayRollBill build() {
        return nonPayRollBill;
    }
}
