/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.addnewproduct;

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
public class getProduct {

    private static String pricePath = "//div[@class='data']/input[@name='price']";
    private static String pricePromotePath = "//div[@class='data']/input[@name='promoPrice']";
    private static String sizesPath = "//div[@class='data']//ul[@class='options']//li[@class='size select required']//option";
    private static String longskuPath = "//div[@class='data']/input[@name='longSku']";
    private static String namePath = "//div[@class='data']/input[@name='name']";
    private static String colorPath = "//div[@class='data']/input[@name='color']";
    private static String productIdPath = "//div[@class='data']/input[@name='productId']";
    private static String categoryIdPath = "//div[@class='data']/input[@name='catId']";
    private static String webcodePath = "//div[@class='data']/input[@name='collection']";
    private static String seqPath = "//div[@class='data']/input[@name='cseq']";
    private static Logger LOG = Logger.getLogger("AddNewProduct");

    public static void getDataFromLinks(String inputLinks, boolean IS_WHOLE) throws FileNotFoundException, XPatherException, MalformedURLException, IOException, SQLException {
        Scanner scanner = new Scanner(inputLinks);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            getDataFromUrl(line, IS_WHOLE);
        }
    }

    public static void getDataFromUrl(String url, boolean IS_WHOLE) throws XPatherException, MalformedURLException, IOException, SQLException {
        if (IS_WHOLE) {
            url = url.replaceAll("_\\d+", "_");
            for (int i = 1; i < 16; i++) {
                System.out.println(i);

                if (i < 10) {
                    getProduct(url + "0" + i);
                } else {
                    getProduct(url + i);
                }
            }
        } else {
            getProduct(url);
        }
    }

    public static void getProduct(String url) throws XPatherException, MalformedURLException, IOException, SQLException {
        URLConnection conn = readHTML.getUrlConn(url);
        HtmlCleaner htmlCleaner = readHTML.getHtmlCleaner();

        TagNode root = htmlCleaner.clean(new InputStreamReader(conn.getInputStream()));

        String price = getValue(root, pricePath);
        String pricePromote = getValue(root, pricePromotePath);

        if (!pricePromote.isEmpty()) {
            price = pricePromote;
        }

        String name = getValue(root, namePath);
        String longSku = getValue(root, longskuPath);
        String color = getValue(root, colorPath);
        String webcode = getValue(root, webcodePath);
        String seq = getValue(root, seqPath);

        LOG.log(Level.FINEST, "Longsku :" + longSku);

        HashMap<String, String> sizeCodes = getSizes(root, sizesPath);
        String productid = getValue(root, productIdPath);
        String category_id = getValue(root, categoryIdPath);
        Db mysqlConn = new Db();

        try {
            mysqlConn.dbConnect();
            if (longSku.isEmpty()) {
                return;
            }
            BigInteger long_sku_id = new BigInteger(longSku.replaceAll("-", ""));
            String imgurl = "";
            if (url.contains("abercrombie")) {
                imgurl = "http://anf.scene7.com/is/image/anf/anf_" + webcode + "_" + seq + "_prod1?$anfCategoryJPG$";
            } else if (url.contains("hollisterco")) {
                imgurl = "http://anf.scene7.com/is/image/anf/hol_"+webcode+"_"+seq+"_prod1?$holCategoryJPG$";
            }
            
            String imgpath = long_sku_id + ".jpg";

            readHTML.saveImage(imgurl, "/Users/admin/Documents/afAppJava/pics/", imgpath);

            mysqlConn.saveProducts(long_sku_id, longSku, Integer.parseInt(webcode), Integer.parseInt(seq), Integer.parseInt(category_id), name, color, Double.parseDouble(price.replaceAll("\\$|\\s", "")), url);
            mysqlConn.saveProductsDetail(long_sku_id, sizeCodes);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(getProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
        mysqlConn.dbColse();
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

    public static HashMap<String, String> getSizes(TagNode node, String path) throws XPatherException {
        HashMap<String, String> sizesCode = new HashMap<String, String>();

        Object[] foundList = node.evaluateXPath(path);
        if (foundList == null || foundList.length < 1) {
            return null;
        }

        for (int i = 1; i < foundList.length; i++) {
            TagNode sizesNode = (TagNode) foundList[i];
            String sizeCode = sizesNode.getAttributeByName("value");

            String size = sizesNode.getText().toString().replaceAll("&nbsp|;|-|Backordered|\\n|\\s", "").replaceAll("WillShip\\d+/\\d+/\\d+", "").trim();
            sizesCode.put(size, sizeCode);

            LOG.log(Level.FINEST, sizeCode + " : " + size);
        }
        return sizesCode;
    }
}
