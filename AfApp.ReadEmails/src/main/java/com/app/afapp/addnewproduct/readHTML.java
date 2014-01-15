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
import java.io.FileInputStream;
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
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;

/**
 *
 * @author yuanyuan
 */
public class readHTML {
    static String domain = "waws-prod-blu-003.ftp.azurewebsites.windows.net";
    static String user = "afapp\\$afapp";
    static String pwd = "Bg8ik95E1uN8hiLq9PBfi7kheHklKpziB8JSE5xi43k7Gn7w0uednrg5l5yA";
    private static Logger LOG = Logger.getLogger("AddNewProduct");
     
    public static URLConnection getUrlConn(String url) throws MalformedURLException, IOException{ 
       CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
       
       //System.out.println(url);
       URL url1 = new URL(url);
       URLConnection conn = url1.openConnection();   
           
       return conn;
    }
    
    public static HtmlCleaner getHtmlCleaner(){
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
                
                if(code == 403 || code == 404){
                    return;
                }
                File desFir = new File(destDir + destFile);
                
                if(desFir.exists()){
                    return;
                }
                
		InputStream is = url.openStream();
                
		OutputStream os = new FileOutputStream(destDir + destFile);

                LOG.log(Level.FINEST, "Save new image : " + destFile);
               // System.out.println("Save new image : " + destFile);
		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

               // uploadImg(destDir+destFile, domain, user,pwd);
		is.close();
		os.close();
	}
    
    public static void getLongsku_ProductId(String webLink) throws IOException, XPatherException, SQLException{
        String longskuPath = "//div[@class='data']/input[@name='longSku']";
        String productId = "//div[@class='data']/input[@name='productId']";
        String seqPath = "//div[@class='data']/input[@name='cseq']";
        
        URLConnection conn = getUrlConn(webLink);
        HtmlCleaner htmlCleaner = getHtmlCleaner();
       
       TagNode root = htmlCleaner.clean(new InputStreamReader(conn.getInputStream()));
       
       String longsku = getProduct.getValue(root, longskuPath);
       String productid = getProduct.getValue(root, productId);
       String seq = getProduct.getValue(root, seqPath);
               
       if(longsku == null || longsku.isEmpty()){
           return;
       }
       
       BigInteger long_sku_id = new BigInteger(longsku.replaceAll("-", ""));
       Map<String, String> queryMap = new HashMap<String, String>();
       
        Db mysqlConn = new Db();
        try{
             mysqlConn.dbConnect();  
             mysqlConn.saveLongSkProductID(long_sku_id, productid+"_"+seq);
             }catch (ClassNotFoundException ex) {
            Logger.getLogger(readHTML.class.getName()).log(Level.SEVERE, null, ex);
        }    
         mysqlConn.dbColse();
    }
    
       public static String getValue(TagNode node, String path) throws XPatherException{   
       String value = "";
       Object[] foundList = node.evaluateXPath(path);
       
       if( foundList == null || foundList.length < 1) {
          return null;
       }

        TagNode aNode = (TagNode)foundList[0];      
        value = aNode.getAttributeByName("value");
        
        return value;
    }
       public static void uploadImg(String inputfile, String ftpdomain, String user, String pwd){
           FTPClient client= new FTPClient();
           FileInputStream fis = null;
           
           try{
               client.connect(ftpdomain);
               client.login(user, pwd);
               
               client.setFileType(FTP.BINARY_FILE_TYPE);
               fis = new FileInputStream(inputfile);
               String filename = new File(inputfile).getName();
               
               client.storeFile("/site/wwwroot/images/"+filename, fis);
               client.logout();
               
               LOG.log(Level.FINEST, "UPLOAD image to ftp : " + filename);
           }catch(IOException e){
               e.printStackTrace();
           }finally {
                try {
                  if (fis != null) {
                       fis.close();
                     }
                    client.disconnect();
                   } catch (IOException e) {
                  e.printStackTrace();
                   }
           }
}
}
