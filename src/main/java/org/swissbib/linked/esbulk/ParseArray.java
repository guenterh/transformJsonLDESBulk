package org.swissbib.linked.esbulk;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

/**
 * Created by swissbib on 7/5/16.
 */
public class ParseArray extends ParseBase {






    public ParseArray(JsonParser parser) {

        super();
        this.parser = parser;
        this.numberObjects ++;
        myObjectString = new StringBuilder().append("[");
    }


    public void fetchObjects() {

        JsonToken currentToken;
        JsonToken lastToken = null;
        int numberOfObjectsInArray = 0;
        while (this.numberObjects > 0) {

            try {
                currentToken = parser.nextToken();

                if (currentToken == JsonToken.END_ARRAY) {
                    numberObjects--;
                    //myObjectString.append("]" + comma);
                    myObjectString.append("]");
                } else if ( currentToken == JsonToken.START_OBJECT) {
                    ParseObject p = new ParseObject(this.parser);
                    p.fetchObjects();
                    if (numberOfObjectsInArray > 0) {
                        myObjectString.append(comma).append(" ").append(p.myObjectString);
                    } else {
                        myObjectString.append(p.myObjectString);
                    }
                    numberOfObjectsInArray++;
                } else if (currentToken == JsonToken.START_ARRAY) {

                    ParseArray pA = new ParseArray(this.parser);
                    pA.fetchObjects();
                    myObjectString.append(pA.getObjectString());

                } else if (currentToken == JsonToken.VALUE_STRING) {
                    if (lastToken != null && lastToken == JsonToken.VALUE_STRING) {
                        myObjectString.append(qM).append(this.replaceqM(parser.getText())).append(qM).append(comma);
                        //myObjectString.append(qM).append(this.replaceqM(parser.getText())).append(qM);
                    } else {
                        myObjectString.append(qM).append(this.replaceqM(parser.getText())).append(qM);
                        lastToken = JsonToken.VALUE_STRING;
                    }
                }

            } catch (IOException ex)  {
                ex.printStackTrace();
            }

        }
    }

    public String getObjectString() {
        return myObjectString.toString();
    }


}
