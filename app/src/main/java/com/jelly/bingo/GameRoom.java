package com.jelly.bingo;

public class GameRoom {
    String title;
    int status;
    Member init;
    Member join;

    public GameRoom(String title, Member init) {
        this.title = title;
        this.init = init;
    }

    public GameRoom(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Member getInit() {
        return init;
    }

    public void setInit(Member init) {
        this.init = init;
    }

    public Member getJoin() {
        return join;
    }

    public void setJoin(Member join) {
        this.join = join;
    }
}
