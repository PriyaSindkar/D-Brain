package com.webmyne.android.d_brain.ui.xmlHelpers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by priyasindkar on 18-09-2015.
 */
public class MainXmlPullParser {
    ArrayList<XMLValues> xmlList;
    XMLValues xmlTagItem;

    public  ArrayList<XMLValues> processXML(InputStream inputStream) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            xmlList = new ArrayList<XMLValues>();
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {


                if (eventType == XmlPullParser.START_TAG && !(parser.getName().equalsIgnoreCase("response"))) {
                    // not use in coding
                    xmlTagItem = new XMLValues();
                    xmlTagItem.tagName = parser.getName();
                    // System.out.println("Start tag " + parser.getComponentName());

                } else if (eventType == XmlPullParser.TEXT && !parser.getText().toString().trim().equalsIgnoreCase("")) {
                    xmlTagItem.tagValue = parser.getText();
                    xmlList.add(xmlTagItem);

                    //  System.out.println("Text " + parser.getText());
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlList;

    }
}
