package smartspace.plugins;

import smartspace.data.VehicleStatus;

public class ReportVehicleStatusInput {

    private VehicleStatus vehicleStatus;

    public ReportVehicleStatusInput() {
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }
}
