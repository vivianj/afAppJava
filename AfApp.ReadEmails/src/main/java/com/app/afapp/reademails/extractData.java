/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author yuanyuan
 */
public class extractData {

    static String QQ_REGEX = "qq\\s*:\\s*(\\d+)$";
    static String STORE_ID_REGEX = ".*?store\\s*(\\d+).*?";
    static String DATE_REGEX = ".*?trans\\s*(\\d+)date/time\\s*(\\d{4}-\\d{2}-\\d{2})\\s*\\d{2}:?\\d{2}.*";
    static String ITEM_REGEX = "(\\d{9}).*?\\$\\d+\\s*\\.?\\d+\\s*$";
    static String PRICE_REGEX = "\\$(\\d+\\.\\d+)\\s*";
    static String Subtotal_REGEX = ".*?subtotal\\s*\\$(\\d+\\s*[,\\.]\\d+).*";   
    static String Tax_REGEX = ".*?subtotal\\s*\\$(\\d+\\s*[,\\.]?\\d+)\\s*tax.*?\\$(\\d+\\s*[,\\.]?\\d+)\\s*total\\s*\\$(\\d+\\s*[,\\.]?\\d+).*";
    static String TOTAL_REGEX = ".*?\\s+total\\s*\\$(\\d+\\s*[,\\.]?\\d+)";
    static String orderId_regex = ".*?order\\s*#:.*?(\\d+).*";
    static String date_regex = ".*order\\s*date:.*?(\\d+/\\d+/\\d+).*";
    static String ship_date = ".*ship\\s*date:.*?(\\d+/\\d+/\\d+).*";
    static String start_regex = ".*item\\s*description.*";
    static String end_regex = "total\\s*discount\\s+\\$.*";
    static String code_regex = ".*?(\\d{9})";
    static String price_regex = "(?:price)?\\$?(\\d+\\.\\d+).*";
    static String subtotal_regex = ".*?subtotal.*?\\$?(\\d+\\.\\d{2}).*?shipping.*?\\$?(\\d+\\.\\d{2}).*?tax.*?\\$?(\\d+\\.\\d{2}).*?total.*?\\$?(\\d+\\.\\d{2}).*";
    static String total_regex = "\\$\\d+\\.\\d+\\s*order\\s+total\\s+\\$?(\\d+\\.\\d+)\\s.*";
    static String tax_regex = ".*estimated\\s+sales\\s+tax\\s+\\$?(\\d+\\.\\d+)\\s";
    static String shipping_regex = ".*\\sshipping\\s+&\\s*handling\\s+\\$?(\\d+\\.\\d+)\\s.*";

    public static String getFileContent(String textFile) throws FileNotFoundException, IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(textFile));
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
            result.append("\n");
        }

        br.close();

        return result.toString();
    }

    public static String extractReceiptData(String input) {
        if(input.contains("Tax")){
           input = mergeTotalLines(input.replaceAll("<.*?>", "").replaceAll("=20", ""));
        }
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(input);
        String storeid = "";
        String date = "";
        String email = "";
        boolean start = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().toLowerCase();
            
            if (line.trim().isEmpty()) {
                continue;
            }
            
            if (line.toLowerCase().matches(STORE_ID_REGEX)) {
                storeid = getContentFromLine(line, STORE_ID_REGEX);
                continue;
            }

            if (line.matches(DATE_REGEX)) {
                String[] dates = getContentFromLine(line, DATE_REGEX).split("\\s+");
                result.append("Order" + ":" + storeid + dates[0] + dates[1].replaceAll("-", "") +"\n");
                result.append("Date:" + dates[1] + "\n");
                start = true;
                continue;
            }
            
            if(start){
              if ( line.trim().matches(ITEM_REGEX) ){
                   String code = getContentFromLine(line.toLowerCase().trim(), ITEM_REGEX);
                   result.append(code + " ");
               }else if( line.toLowerCase().trim().matches(PRICE_REGEX) ){
                   result.append(getContentFromLine(line.toLowerCase().trim(), PRICE_REGEX)); 
                   result.append("\n");
               }
           }
             
          
            if (line.toLowerCase().matches(Tax_REGEX)) {
                start = false;
                String[] totals = getContentFromLine(line, Tax_REGEX).split(" ");
                result.append("Subtotal" + ":" + totals[0] + "\n");
                result.append("Tax" + ":" + totals[1] + "\n");
                result.append("Total" + ":" + totals[2] + "\n");
                continue;
            }

            if (line.matches(Subtotal_REGEX)) {
                String tax = getContentFromLine(line, Subtotal_REGEX);
                result.append("Subtotal" + ":" + tax);
                result.append("\n");
            }

            if (line.toLowerCase().matches(TOTAL_REGEX)) {
                String total = getContentFromLine(line, TOTAL_REGEX);
                result.append("Total" + ":" + total);
                result.append("\n");
                continue;
            }
                  
            
        }
        return result.toString();
    }

    public static String getContentFromLine(String line, String str_regex) {
        String result = "";

        Pattern pattern = Pattern.compile(str_regex);
        Matcher match = pattern.matcher(line.toLowerCase());

        if (match.matches()) {
            for (int j = 0; j < match.groupCount(); j++) {
                result += match.group(j + 1).replaceAll("\\s+", "") + " ";
            }
        }
        return result.trim();
    }

      public static String extractEmailData(String str){
       
       str = mergeTotalLines(str.replaceAll("<.*?>", ""));
       
       System.out.println("Email content : " + str);
       System.out.println("*************************************");
       
       StringBuilder result = new StringBuilder();
       Scanner scanner = new Scanner(str);
       boolean start = false;     
       String prices = "";
       String email = "";
       String qq = "";
       
       while (scanner.hasNextLine()) {
           String line = scanner.nextLine().replaceAll("\\*", "").replaceAll("\\|", "").replaceAll(">", "").replaceAll("=09", "").trim();   
           if( line.isEmpty() ){
               continue;
           }
           
            if( line.toLowerCase().matches(orderId_regex) ){   
                String orderId = getContentFromLine(line.toLowerCase(), orderId_regex);
                result.append( "Order:"+ orderId + "\n");
            }
            
            if( line.toLowerCase().matches(date_regex) ){
                 String dates = getContentFromLine(line.toLowerCase(), date_regex);
                 String[] dates2 = dates.split("/");
                 result.append( "Date:"+ dates2[2].trim() + "-" + dates2[0].trim() + "-" + dates2[1].trim() +  "\n");
                 continue;
            }
           
            
           if(start){
              if ( line.trim().matches(code_regex) ){
                   String code = getContentFromLine(line.toLowerCase().trim(), code_regex);
                   result.append(code + " ");
               }else if( line.toLowerCase().trim().matches(price_regex) ){
                   result.append(getContentFromLine(line.toLowerCase().trim(), price_regex)); 
                   result.append("\n");
               }
           }
             
            
           if( line.toLowerCase().matches(subtotal_regex) ){ 
               start = false;
               String[] totals = getContentFromLine(line.toLowerCase(), subtotal_regex).split(" ");
               result.append( "Subtotal:"+totals[0]+ "\n");
               result.append( "Shipping:"+totals[1]+ "\n");
               result.append( "Tax:"+totals[2]+ "\n");
               result.append( "Total:"+totals[3]+ "\n");
           }
           
           if( line.toLowerCase().matches(start_regex) ){
               start = true;
           }    
      }
       
       return result.toString();
    }
      
      public static String mergeTotalLines(String str){
          boolean start = false;
          Scanner scanner = new Scanner(str);
          StringBuilder result = new StringBuilder();
           
           while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();   
                if( line.isEmpty() ){
                    continue;
                }
                
                if(line.matches(".*Subtotal.*")){
                    start = true;
                }
                
                 if(line.contains("Total Discount")){
                    int end = line.indexOf("Total Discount");
                    result.append(line.substring(0, end) + "\n");
                    start = false;
                }else if(line.contains("Change Due")){
                    int end = line.indexOf("Change Due");
                    result.append(line.substring(0, end) + "\n");
                    start = false;
                    
                }  
                if(!start){
                    result.append(line + "\n");
                }else{
                    result.append(line + " ");
                }         
           }
           
           System.out.print( "Merged string: "+ str);
           return result.toString();
      }
}
