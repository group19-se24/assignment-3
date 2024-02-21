package com.esri.core.geometry;

import org.junit.Test;

public class TestAppendQuote {
    JsonStringWriter jsw =  new JsonStringWriter();

    /**
     * Test should pass if it creates a json-file that correctly escapes the \t 
     */
    @Test
    public void testAppendQuoteWithTabs() {
        jsw.startObject();
        jsw.addPairString("cool\tare", "tex\tabs");
        jsw.endObject();
        String testString = ("{\"cool\\tare\":\"tex\\tabs\"}");
        assert (jsw.getJson().equals(testString));
    }

    /**
     * Test should pass if it creates a json-file that correctly escapes the \n
     */
    @Test
    public void testAppendQuoteWithNewlines() {
        jsw.startObject();
        jsw.addPairString("cool\nare", "tex\nabs");
        jsw.endObject();
        String testString = ("{\"cool\\nare\":\"tex\\nabs\"}");
        assert (jsw.getJson().equals(testString));
    }

    /**
     * Test should pass if it creates a json-file that correctly escapes two backslashes 
     */
    @Test
    public void testAppendQouteWithBackslashes() {
        jsw.startObject();
        jsw.addPairString("cool\\are", "tex\\abs");
        jsw.endObject();
        String testString = ("{\"cool\\\\are\":\"tex\\\\abs\"}");
        assert (jsw.getJson().equals(testString));
    }

    /**
     * Test should pass if it creates a json-file that correctly escapes the \f and the \b
     */
    @Test
    public void testAppendQuoteWithBAndF() {
        jsw.startObject();
        jsw.addPairString("cool\bare", "tex\fabs");
        jsw.endObject();
        String testString = ("{\"cool\\bare\":\"tex\\fabs\"}");
        assert (jsw.getJson().equals(testString));
    }

    /**
     * Test should pass if it creates a json-file that correctly escapes the </ symbol
     */
    @Test
    public void testAppendQuoteWithForwardPrecission() {
        jsw.startObject();
        jsw.addPairString("cool</are", "tex//abs");
        jsw.endObject();
        String testString = ("{\"cool<\\/are\":\"tex//abs\"}");
        assert (jsw.getJson().equals(testString));
    }


}
