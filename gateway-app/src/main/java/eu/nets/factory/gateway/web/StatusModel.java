package eu.nets.factory.gateway.web;

import java.util.HashMap;

public  class StatusModel {
    public HashMap<String, String> data = new HashMap<>();

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  StatusModel))
            return false; //also checks for null

        StatusModel sm2 = (StatusModel) obj;
        //check that svname and pxname are equal
        return(data.get("svname").equals(sm2.data.get("svname")) && data.get("pxname").equals(sm2.data.get("pxname")));
    }

    @Override
    public String toString() {
        return "("+data.get("pxname")+" :  "+data.get("svname")+")";

    }
}
