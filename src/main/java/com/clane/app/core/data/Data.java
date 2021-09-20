package com.clane.app.core.data;

import javax.xml.bind.JAXB;
import java.io.Serializable;
import java.io.StringWriter;

public class Data implements Serializable {
    public static String APP_NAME = "Clane";
    public static String DEFAULT_WALLET = "11000000000";

    @Override
    public String toString() {
        return toXmlString();
    }

    public String toXmlString() {
        StringBuilder buf = new StringBuilder();
        StringWriter os = new StringWriter();
        JAXB.marshal(this, os);
        buf.append(os);
        return buf.toString();
    }
}
