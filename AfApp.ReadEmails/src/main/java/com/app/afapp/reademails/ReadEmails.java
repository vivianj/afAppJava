/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author yuanyuan
 */
public class ReadEmails {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

        int limit = 100000;
        Logger logger = Logger.getLogger("readEmail");

        String email = "";
        String password = "831218xx";
        String dbname = "";
        String dbusername = "";
        String dbpassword = "";
        
        InputStream in = ReadEmails.class.getResourceAsStream("/email.properties");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
    
        String line;
        while ((line = br.readLine()) != null) {
            if(line.contains("email=")){
                email = line.split("=")[1];
            }else if(line.contains("password=")){
                password = line.split("=")[1];
            }else if(line.contains("dbname=")){
                dbname = line.split("=")[1];
            } else if(line.contains("dbusername=")){
                dbusername = line.split("=")[1];
            }else if(line.contains("dbpassowrd=")){
                dbpassword = line.split("=")[1];
            }
        }
        br.close();

        Handler fh = new FileHandler("/Users/admin/Documents/afAppJava/readEmails.log", limit, 1, true);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        logger.setLevel(Level.ALL);
        logger.info("Begin Reading Emails:");
        InboxReader.readEmail(email, password, dbname, dbusername, dbpassword);
    }
   
}
