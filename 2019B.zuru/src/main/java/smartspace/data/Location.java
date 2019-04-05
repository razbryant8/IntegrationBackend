package smartspace.data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {
    private double x;

    private double y;

    public Location(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Location() {

    }

    @Column(name="X")
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


}
