package org.swissbib.linked.esbulk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.*;

/**
 * Created by swissbib on 7/5/16.
 */
public class ReadJSONLD {


    private StringBuilder gndObject;

    public static void main (String [] args) {


        JsonFactory jsonFactory = new JsonFactory();



        try {
            JsonParser jsonParser = jsonFactory.createParser (new File("/home/swissbib/environment/data/gnd.json-ld.2016_07/GND.jsonld"));
            //JsonParser jsonParser = jsonFactory.createParser (new File("/home/swissbib/environment/data/gnd.json-ld.2016_07/test.json"));
            //JsonParser jsonParser = jsonFactory.createParser (new File("/home/swissbib/environment/data/gnd.json-ld.2016_07/short.jsonld"));


            JsonToken currentToken = jsonParser.nextToken();
            StringBuilder myObjectString = null;
            int numberOfCreatedObjects = 0;
            int numberOfFiles = 1;


            BufferedWriter bw = createOutputFile(numberOfFiles);
            while (currentToken != null) {

                if (currentToken == JsonToken.START_OBJECT) {
                    myObjectString = new StringBuilder();

                    ParseObject pO = new ParseObject(jsonParser);
                    pO.fetchObjects();

                    //if (numberOfCreatedObjects > 0) {
                    //    myObjectString.append(",").append(pO.myObjectString);
                    //} else {
                    //    myObjectString.append(pO.myObjectString);
                    //}

                    numberOfCreatedObjects++;
                    bw.write(String.format("{\"index\":{\"_index\":\"gnd\",\"_type\":\"%s\",\"_id\":\"%s\"}}", pO.getResourceType(),pO.getObjectId()));
                    bw.newLine();
                    bw.write(pO.getObjectString());
                    bw.newLine();
                    if (numberOfCreatedObjects % 20000 == 0) {
                        bw.flush();
                        bw.close();
                        numberOfFiles++;
                        bw = createOutputFile(numberOfFiles);
                        numberOfCreatedObjects = 0;

                    }

                    numberOfCreatedObjects++;
                    //System.out.println(myObjectString.toString()) ;

                } else if (currentToken == JsonToken.END_OBJECT) {
                    if (myObjectString != null) {
                        //myObjectString.append("},");


                        System.out.println("is this possible?");
                        //write into file


                    } else {
                        System.out.println("objectString on root level null while reaching END_OBJECT --> something went wrong");

                    }
                }

                numberOfCreatedObjects++;
                currentToken = jsonParser.nextToken();
            }
            bw.flush();
            bw.close();

        } catch (IOException ioException) {

            ioException.printStackTrace();
        }


    }


    private static BufferedWriter createOutputFile (int fileNumber) {

        BufferedWriter bw = null;
        String filePrefix = String.format("%010d",fileNumber);
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/" + filePrefix + "gnd.json"), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }


        return bw;

    }


}
