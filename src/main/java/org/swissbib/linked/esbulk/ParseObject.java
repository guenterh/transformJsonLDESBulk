package org.swissbib.linked.esbulk;






/**
 * Created by swissbib on 7/5/16.
 */


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Splitter;

import java.io.IOException;


public class ParseObject extends ParseBase{

    JsonParser parser;
    int numberObjects = 0;
    String objectId = "";
    String resourceTpe = "DEFAULT";
    boolean fieldNameWasId = false;
    boolean fieldNameWasType = false;


    StringBuilder myObjectString;

    public ParseObject(JsonParser parser) {
        super();
        this.parser = parser;
        this.numberObjects ++;
        myObjectString = new StringBuilder().append("{");
    }


    public void fetchObjects() {

        JsonToken currentToken;
        //JsonToken lastToken = null;
        int numberCreatedField_Names = 0;
        while (this.numberObjects > 0) {

            try {
                currentToken = parser.nextToken();

                if (currentToken == JsonToken.END_OBJECT) {
                    numberObjects--;
                    myObjectString.append("}");
                } else if ( currentToken == JsonToken.START_OBJECT) {
                    ParseObject p = new ParseObject(this.parser);
                    numberObjects++;
                    p.fetchObjects();
                    myObjectString.append(p.myObjectString);
                } else if (currentToken == JsonToken.START_ARRAY) {
                    ParseArray pA = new ParseArray(this.parser);
                    pA.fetchObjects();
                    myObjectString.append(pA.getObjectString());
                } else if (currentToken == JsonToken.FIELD_NAME) {

                    String validKeyName = parser.getText().replace(".","_");
                    if (numberCreatedField_Names > 0) {
                        myObjectString.append(comma).append(" ").append(qM).append(validKeyName).append(qM).append(colon);
                    } else {
                        myObjectString.append(qM).append(validKeyName).append(qM).append(colon);
                    }
                    if (parser.getCurrentName().equals("@id")) {
                        fieldNameWasId = true;
                    }
                    //else if (parser.getCurrentName().equals("@type")) {
                    //    fieldNameWasType = true;
                    //}
                    //System.out.println(parser.getCurrentName());
                    //lastToken = JsonToken.FIELD_NAME;
                } else if (currentToken == JsonToken.VALUE_STRING) {
                    //String test = "Männergesangverein Liedertafel gen. \"Worzel\" Ffm.-Unterliederbach";
                    //myObjectString.append(qM).append(this.replaceqM(test)).append(qM);
                    myObjectString.append(qM).append(this.replaceqM(parser.getText())).append(qM);
                    numberCreatedField_Names++;
                    if (fieldNameWasId) {
                        this.objectId = parser.getText();
                        fieldNameWasId = false;
                    }
                    //else if (fieldNameWasType) {
                    //    String type =  parser.getText();
                    //    String[] partsOfType = type.split("#");
                    //    if (partsOfType.length == 2) {
                    //        this.resourceTpe = partsOfType[1];
                    //    }
                    //    fieldNameWasType = false;
                    //}
                }

            } catch (IOException ex)  {
               ex.printStackTrace();
            }

        }
    }

    public String getObjectString() {
        return myObjectString.toString();
    }

    public String getObjectId() {

        return this.objectId;
    }

    public String getResourceType () {
        return this.resourceTpe;
    }

}
