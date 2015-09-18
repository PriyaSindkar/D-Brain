package com.webmyne.android.d_brain.ui.xmlHelpers;

/**
 * Created by priyasindkar on 18-09-2015.
 */
public class XMLValues {

    public String tagName;
    public String tagValue;

    public XMLValues(){

    }

    public XMLValues(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
