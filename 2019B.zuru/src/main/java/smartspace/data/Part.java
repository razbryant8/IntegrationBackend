package smartspace.data;

import java.util.Objects;

public class Part {

    String partId;
    String name;
    int amount;

    public Part() {
    }

    public Part(String partId, int amount,String name) {
        this.partId = partId;
        this.amount = amount;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
