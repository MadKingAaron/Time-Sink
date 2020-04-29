package com.example.projecttimesink;

public class EmoteBluetoothPackage implements BluetoothMessageReceive {
    private EmoteInterface emote;
    private Boolean updatedSinceLastCheck;

    public EmoteBluetoothPackage()
    {
        this.updatedSinceLastCheck = false;
    }

    @Override
    public void updateData(Object newData) {
        this.emote = (EmoteInterface) newData;
        this.updatedSinceLastCheck = true;
    }

    @Override
    public Object getData() {
        this.updatedSinceLastCheck = false;
        return this.emote;
    }

    @Override
    public Object clonePackage() {
        EmoteBluetoothPackage newPackage = new EmoteBluetoothPackage();
        newPackage.emote = this.emote;
        return newPackage;
    }

    @Override
    public boolean checkIfDataUpdatedSinceLastCall() {
        return this.updatedSinceLastCheck;
    }
}
