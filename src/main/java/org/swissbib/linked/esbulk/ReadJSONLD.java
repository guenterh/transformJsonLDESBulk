package org.swissbib.linked.esbulk;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by swissbib on 7/5/16.
 */
public class ReadJSONLD {


    private static String inputFile;
    private static String outDir;
    private static String indexType;
    private static String indexName;
    private static boolean printID;

    private static Pattern pBlankNode = Pattern.compile("_:node");


    public static void main (String [] args) throws Exception{



        readProperties();

        JsonFactory jsonFactory = new JsonFactory();



        try {
            JsonParser jsonParser = jsonFactory.createParser (new File(inputFile));


            JsonToken currentToken = jsonParser.nextToken();
            StringBuilder myObjectString = null;
            int numberOfCreatedObjects = 0;
            int numberTotal = 0;
            int numberTotalBlankNode = 0;
            int numberOfFiles = 1;


            BufferedWriter bw = createOutputFile(numberOfFiles);
            while (currentToken != null) {

                try {

                    if (currentToken == JsonToken.START_OBJECT) {



                        myObjectString = new StringBuilder();

                        ParseObject pO = new ParseObject(jsonParser);
                        pO.setResourceTpe(indexType);
                        pO.fetchObjects();

                        //if (numberOfCreatedObjects > 0) {
                        //    myObjectString.append(",").append(pO.myObjectString);
                        //} else {
                        //    myObjectString.append(pO.myObjectString);
                        //}


                        numberOfCreatedObjects++;
                        if (pBlankNode.matcher(pO.getObjectId()).find())
                        {
                            numberTotalBlankNode++;
                            if (printID) {
                                System.out.println(pO.getObjectId() + " created objects blank Node: " + numberTotalBlankNode);
                            }


                        } else {

                            numberTotal++;
                            if (printID) {
                                System.out.println(pO.getObjectId() + " created objects: " + numberTotal);
                            }


                        }


                        bw.write(String.format("{\"index\":{\"_index\":\"" + indexName + "\",\"_type\":\"%s\",\"_id\":\"%s\"}}", pO.getResourceType(),pO.getObjectId()));
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


                            throw new Exception("myObjectString not equal null in case of END_OBJECT - possible ?");
                            //write into file


                        } else {
                            System.out.println("objectString on root level null while reaching END_OBJECT --> something went wrong");

                        }
                    }
                    numberOfCreatedObjects++;
                    currentToken = jsonParser.nextToken();


                } catch (Throwable exceptionWhileParsing) {
                    exceptionWhileParsing.printStackTrace();
                }

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
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDir + "/" + filePrefix + "gnd.json"), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }


        return bw;

    }


    private static void readProperties() throws Exception{

        Optional<String> iF = Optional.ofNullable( System.getProperty("input.file", null));

        if (! iF.isPresent() )
            throw new Exception("no input.file property");
        else
            inputFile = iF.get();

        indexName = System.getProperty("index.name", "gnd");

        outDir = System.getProperty("out.dir", "outdir");

        indexType = System.getProperty("index.type", "DEFAULT");

        printID = Boolean.parseBoolean(System.getProperty("print.id", "false"));


    }


}
