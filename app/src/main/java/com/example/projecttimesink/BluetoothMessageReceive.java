package com.example.projecttimesink;

public interface BluetoothMessageReceive<T> {

    public void updateData(Object newData);

    public T getData();

    public BluetoothMessageReceive clonePackage();

    public boolean checkIfDataUpdatedSinceLastCall();
}
