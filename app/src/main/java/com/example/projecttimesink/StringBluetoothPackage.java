package com.example.projecttimesink;

public class StringBluetoothPackage implements BluetoothMessageReceive {
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
    public Object getData() {
        this.updatedSinceLastCheck = false;
        return this.data;
    }

    @Override
    public Object clonePackage() {
        StringBluetoothPackage newPackage = new StringBluetoothPackage();
        newPackage.data = this.data;
        return new StringBluetoothPackage();
    }

    @Override
    public boolean checkIfDataUpdatedSinceLastCall() {
        return this.updatedSinceLastCheck;
    }
}
