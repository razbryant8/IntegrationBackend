package smartspace.plugins;

public class CatchNReleaseInput {
    private String vehicleStatus;

    public CatchNReleaseInput() {
    }

    public CatchNReleaseInput(String vehicleStatus){
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }
}
