package com.google.sample.cloudvision;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.ArrayList;


public class NetworkUtils {
   final static String BASE_URL =
            "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";



    public static URL buildUrl(String githubSearchQuery) {
        String requrl = BASE_URL+githubSearchQuery+"?key=24b6344b-8ac7-41d7-9cc8-c03ed3aa726d";
        URL url = null;
        try {
            url = new URL(requrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        try {
            // try and retrieve xml definitions from merriam-webster api
            URLConnection conn = url.openConnection();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());
            System.out.print(doc);
            // hand xml over to parser
            return grab(doc);

        } catch(Exception e){
            return "Whoops, something went wrong.";
        }

    }


   public static String grab(Document doc) {
    doc.getDocumentElement().normalize();
    String def = "";

    // get every entry for the word
    NodeList entryList = doc.getElementsByTagName("entry");

    // for each entry
    for(int i=0; i<entryList.getLength(); i++) {
        Node node = entryList.item(i);
        ArrayList<String> defs = new ArrayList<String>();
        if(node.getNodeType() == Node.ELEMENT_NODE){
            Element eElement = (Element) node;
            def += eElement.getAttribute("id");
            
            NodeList defList = node.getChildNodes();


            //examine entry
            for(int j=0; j<defList.getLength(); j++) {
                Node child = defList.item(j);
                if(child.getNodeType() == Node.ELEMENT_NODE) {

                    // get funtion label (part of speech)
                    if(child.getNodeName().equals("fl")) {
                        def+=child.getTextContent()+"\n\n";
                    }
                    // get pronunciation
                    if(child.getNodeName().equals("pr")) {
                        def+=child.getTextContent()+"\n\n";
                    }

                    // get each definition                            
                    if(child.getNodeName().equals("def")) {
                        NodeList dtList = child.getChildNodes();
                        for(int k=0; k<dtList.getLength(); k++) {
                            Node dt = dtList.item(k);
                            if(dt.getNodeType() == Node.ELEMENT_NODE) {
                                if(dt.getNodeName().equals("dt")) {
                                    if(!dt.getTextContent().equals(""))
                                        def+=dt.getTextContent()+"\n\n";
                                }
                            }
                        }
                    }
                }
            }
        }

    }
       return def;
}

}
