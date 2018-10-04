import java.util.Date;

public class BillDetailsBuilder {

    BillDetails billDetails = new BillDetails();

    public BillDetailsBuilder withEmail(String email) {
        billDetails.setEmail(email);
        return this;
    }

    public BillDetailsBuilder withName(String name) {
        billDetails.setName(name);
        return this;
    }

    public BillDetailsBuilder withAmount(float amount) {
        billDetails.setAmount(amount);
        return this;
    }

    public BillDetailsBuilder withDate(Date date) {
        billDetails.setDate(date);
        return this;
    }

    public BillDetailsBuilder withCategory(String category) {
        billDetails.setCategory(category);
        return this;
    }

    public BillDetails build() {
        return billDetails;
    }
}
