//   Copyright 2007-2018 Maen Artimy
//
//   Permission is hereby granted, free of charge, to any person obtaining a 
//   copy of this software and associated documentation files (the "Software"),
//   to deal in the Software without restriction, including without limitation 
//   the rights to use, copy, modify, merge, publish, distribute, sublicense, 
//   and/or sell copies of the Software, and to permit persons to whom the 
//   Software is furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice shall be included 
//    in all copies or substantial portions of the Software.

//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
//    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY
//    , FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
//    THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
//    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
//    DEALINGS IN THE SOFTWARE.

package sti;

import java.util.*;
import java.io.*;

/**
 *
 * @author martimy
 */
public class Options {

    static public String REFRESH_RATE;
    static public String SNMP_TIMEOUT;
    static public String GRAPH_REFRESH_TIME;
    static public String TOPOLOGY_FILE;
    static public String PUBLIC_COMMUNITY;
    static public boolean language;
    static public int wsize, hsize;
    static public String defdir;
    private final static String propFileName = "properties.ini";

    static public void load() {
        //Set default values
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty("REFRESH_RATE", "5000");
        defaultSettings.setProperty("SNMP_TIMEOUT", "5000");
        defaultSettings.setProperty("GRAPH_REFRESH_TIME", "500");
        defaultSettings.setProperty("PUBLIC_COMMUNITY", "S$CN$T_ro");
        //defaultSettings.setProperty("SIZE", "400 400");
        //defaultSettings.setProperty("DIRECTORY", ".");

        //Load properties' values
        Properties settings = new Properties(defaultSettings);
        try {
            FileInputStream sf = new FileInputStream(propFileName);
            settings.load(sf);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        REFRESH_RATE = settings.getProperty("REFRESH_RATE");
        SNMP_TIMEOUT = settings.getProperty("SNMP_TIMEOUT");
        GRAPH_REFRESH_TIME = settings.getProperty("GRAPH_REFRESH_TIME");
        PUBLIC_COMMUNITY = settings.getProperty("PUBLIC_COMMUNITY");

        /*TOPOLOGY_FILE = settings.getProperty("SERVER");
         String tempLang = settings.getProperty("LANGUAGE");
         language = tempLang.equals("Arabic");
         StringTokenizer st = new StringTokenizer(settings.getProperty("SIZE"));
         wsize = Integer.parseInt(st.nextToken());
         hsize = Integer.parseInt(st.nextToken());
         defdir = settings.getProperty("DIRECTORY");*/
    }

    static public void store() {
        Properties settings = new Properties();
        settings.setProperty("REFRESH_RATE", REFRESH_RATE);
        settings.setProperty("SNMP_TIMEOUT", SNMP_TIMEOUT);
        settings.setProperty("GRAPH_REFRESH_TIME", GRAPH_REFRESH_TIME);
        /*settings.setProperty("SERVER", TOPOLOGY_FILE);
         settings.setProperty("LANGUAGE", language ? "Arabic" : "English");
         settings.setProperty("SIZE", wsize+" "+hsize);
         settings.setProperty("DIRECTORY", defdir);*/
        try {
            FileOutputStream sf = new FileOutputStream(propFileName);
            settings.store(sf, "Environment settings");
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
