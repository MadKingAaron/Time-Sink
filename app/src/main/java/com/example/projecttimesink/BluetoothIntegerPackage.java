package com.example.projecttimesink;

public class BluetoothIntegerPackage implements BluetoothMessageReceive<Integer> {

    private Integer emoteType;
    private boolean updatedSinceLastCheck;

    public BluetoothIntegerPackage() {
        this.updatedSinceLastCheck = false;
    }

    @Override
    public void updateData(Integer newData) {
        this.emoteType = newData;
        this.updatedSinceLastCheck = true;
    }

    @Override
    public Integer getData() {
        this.updatedSinceLastCheck = false;
        return this.emoteType;
    }

    @Override
    public BluetoothMessageReceive clonePackage() {
        BluetoothIntegerPackage clone = new BluetoothIntegerPackage();
        clone.emoteType = this.emoteType;
        clone.updatedSinceLastCheck = this.updatedSinceLastCheck;
        return clone;
    }

    @Override
    public boolean checkIfDataUpdatedSinceLastCall() {
        return this.updatedSinceLastCheck;
    }
}
