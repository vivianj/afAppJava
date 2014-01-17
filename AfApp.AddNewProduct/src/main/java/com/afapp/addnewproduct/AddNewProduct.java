/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afapp.addnewproduct;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.htmlcleaner.XPatherException;

/**
 *
 * @author yjiang
 */
public class AddNewProduct {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException, FileNotFoundException, ClassNotFoundException, ParseException, MalformedURLException, XPatherException {
        // TODO code application logic here

        String inputfile = "/Users/admin/Documents/afAppJava/NewLinks.txt";
        
        BufferedReader bufferedReader = new BufferedReader( new FileReader(inputfile));
        StringBuffer links = new StringBuffer();
        String line = null;
        
        while((line = bufferedReader.readLine()) != null){
             links.append(line).append("\n");
         }
        
        int limit = 100000;
        Logger logger = Logger.getLogger("AddNewProduct");

        Handler fh = new FileHandler("/Users/admin/Documents/afAppJava/addNewProduct.log", limit, 1, true);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        logger.setLevel(Level.ALL);
        logger.info("Begin Add New Product:");
        
        getProduct.getDataFromLinks(links.toString(), false);
    }
}
