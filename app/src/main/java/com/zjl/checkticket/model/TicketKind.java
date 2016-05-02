package com.zjl.checkticket.model;

/**
 * Created by zzg on 2016/5/1
 * 票数据类型.
 */
public class TicketKind {
    String kind;
   int firstNumber;
   int secondNumber;
    String user;

    public int getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(int firstNumber) {
        this.firstNumber = firstNumber;
    }

    public int getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(int secondNumber) {
        this.secondNumber = secondNumber;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
