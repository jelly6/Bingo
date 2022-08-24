package com.jelly.bingo;

public class Member {
    String uid;
    String displayName;
    String nickName;
    int avatarId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarIndex) {
        this.avatarId = avatarIndex;
    }
}
