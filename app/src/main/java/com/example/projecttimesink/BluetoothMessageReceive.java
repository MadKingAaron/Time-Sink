package com.example.projecttimesink;

public interface BluetoothMessageReceive {

    public void updateData(Object newData);

    public Object getData();

    public Object clonePackage();

    public boolean checkIfDataUpdatedSinceLastCall();
}
