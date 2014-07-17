package eu.nets.factory.gateway.web;

/**
* Created by ofbje on 17.07.2014.
*/
public class StatusChange {
    public String s,action,b;
    public StatusChange(){
    }


    public StatusChange(String s, String action, String b) {
        this.s = s;
        this.action = action;
        this.b = b;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "s="+s+"action="+action+"b="+b;
    }
}
