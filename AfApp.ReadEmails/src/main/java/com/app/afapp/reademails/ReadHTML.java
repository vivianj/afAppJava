/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.String;

import java.math.BigInteger;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 *
 * @author yuanyuan
 */
public class ReadHTML {

    public static URLConnection getUrlConn(String url) throws MalformedURLException, IOException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        URL url1 = new URL(url);
        URLConnection conn = url1.openConnection();

        return conn;
    }

    public static HtmlCleaner getHtmlCleaner() {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        CleanerProperties props = htmlCleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);

        return htmlCleaner;
    }

    public static void saveImage(String imageUrl, String destDir, String destFile) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("GET");

        huc.connect();
        int code = huc.getResponseCode();

        if (code == 403 || code == 404) {
            return;
        }
        File desFir = new File(destDir + destFile);

        if (desFir.exists()) {
            return;
        }

        InputStream is = url.openStream();

        OutputStream os = new FileOutputStream(destDir + destFile);

        System.out.println("Save new image : " + destFile);
        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    public static String getValue(TagNode node, String path) throws XPatherException {
        String value = "";
        Object[] foundList = node.evaluateXPath(path);

        if (foundList == null || foundList.length < 1) {
            return null;
        }

        TagNode aNode = (TagNode) foundList[0];
        value = aNode.getAttributeByName("value");

        return value;
    }
}
