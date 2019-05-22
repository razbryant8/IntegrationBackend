package smartspace.data;

import java.util.Objects;

public class Part {

    String partId;
    int amount;

    public Part() {
    }

    public Part(String partId, int amount) {
        this.partId = partId;
        this.amount = amount;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part)) return false;
        Part part = (Part) o;
        return getPartId().equals(part.getPartId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPartId());
    }
}
