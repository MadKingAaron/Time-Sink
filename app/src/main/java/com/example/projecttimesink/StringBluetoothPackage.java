package com.example.projecttimesink;

public class StringBluetoothPackage implements BluetoothMessageReceive<String> {
    private String data;
    private boolean updatedSinceLastCheck;

    public StringBluetoothPackage()
    {
        this.updatedSinceLastCheck = false;
    }
    @Override
    public void updateData(Object newData) {
        this.data = (String) newData;
        this.updatedSinceLastCheck = true;
    }

    @Override
    public String getData() {
        this.updatedSinceLastCheck = false;
        return this.data;
    }

    @Override
    public BluetoothMessageReceive<String> clonePackage() {
        StringBluetoothPackage newPackage = new StringBluetoothPackage();
        newPackage.data = this.data;
        return new StringBluetoothPackage();
    }

    @Override
    public boolean checkIfDataUpdatedSinceLastCall() {
        return this.updatedSinceLastCheck;
    }
}
