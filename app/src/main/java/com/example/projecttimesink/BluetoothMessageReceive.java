package com.example.projecttimesink;

public interface BluetoothMessageReceive<T> {

    public void updateData(T newData);

    public T getData();

    public BluetoothMessageReceive clonePackage();

    public boolean checkIfDataUpdatedSinceLastCall();
}
