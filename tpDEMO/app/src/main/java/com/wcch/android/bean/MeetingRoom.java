package com.wcch.android.bean;

/**
 * @author created by Lzq
 * @time：2022/1/11
 * @Des：
 */
public class MeetingRoom {
    private boolean selectedRoom;
    private String roomName;
    private String hId;
    private String currentRoom;
    private boolean isLocalDevice;

    public MeetingRoom() {
    }

    public boolean isSelectedRoom() {
        return selectedRoom;
    }

    public void setSelectedRoom(boolean selectedRoom) {
        this.selectedRoom = selectedRoom;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHId() {
        return hId;
    }

    public void setHId(String hId) {
        this.hId = hId;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public boolean isLocalDevice() {
        return isLocalDevice;
    }

    public void setLocalDevice(boolean localDevice) {
        isLocalDevice = localDevice;
    }
}
