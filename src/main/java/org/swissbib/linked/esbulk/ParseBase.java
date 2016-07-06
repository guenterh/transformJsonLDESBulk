package org.swissbib.linked.esbulk;

import com.fasterxml.jackson.core.JsonParser;

/**
 * Created by swissbib on 7/5/16.
 */
public abstract class ParseBase {

    protected final String qM = "\"";
    protected final String escapedqM = "\\\\\"";
    protected final String colon = ":";
    protected final String comma = ", ";


    protected JsonParser parser;
    protected int numberObjects = 0;

    protected StringBuilder myObjectString;


    public ParseBase(){}


    public abstract void fetchObjects();



    protected String replaceqM(String value) {
        return value.replaceAll(qM,escapedqM);
    }


}
