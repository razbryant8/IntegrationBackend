package smartspace.plugins;

import smartspace.data.VehicleStatus;

public class ReportVehicleStatusInput {

    private String vehicleStatus;

    public ReportVehicleStatusInput() {
    }

    public ReportVehicleStatusInput(String vehicleStatus){
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }
}
