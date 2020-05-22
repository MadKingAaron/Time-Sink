package com.example.projecttimesink;

public class EmoteBluetoothPackage implements BluetoothMessageReceive <EmoteInterface> {
    private EmoteInterface emote;
    private Boolean updatedSinceLastCheck;

    public EmoteBluetoothPackage()
    {
        this.updatedSinceLastCheck = false;
    }

    @Override
    public void updateData(EmoteInterface newData) {
        this.emote = newData;
        this.updatedSinceLastCheck = true;
    }

    @Override
    public EmoteInterface getData() {
        this.updatedSinceLastCheck = false;
        return this.emote;
    }

    @Override
    public BluetoothMessageReceive<EmoteInterface> clonePackage() {
        EmoteBluetoothPackage newPackage = new EmoteBluetoothPackage();
        newPackage.emote = this.emote;
        return newPackage;
    }

    @Override
    public boolean checkIfDataUpdatedSinceLastCall() {
        return this.updatedSinceLastCheck;
    }
}
