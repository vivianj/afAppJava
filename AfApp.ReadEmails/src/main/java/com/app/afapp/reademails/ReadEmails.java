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

/**
 *
 * @author yuanyuan
 */
public class ReadEmails {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
         
         int limit = 100000;
         Logger logger = Logger.getLogger("readEmail");
         
         Handler fh = new FileHandler("/Users/admin/Documents/afAppJava/readEmails.log", limit, 1, true);
         logger.addHandler(fh);
         SimpleFormatter formatter = new SimpleFormatter();
         fh.setFormatter(formatter);
         
         logger.setLevel(Level.ALL);
         logger.info("Begin Reading Emails:");
         InboxReader.readEmail();
    } 
}
