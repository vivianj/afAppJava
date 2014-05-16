/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Scanner;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yuanyuan
 */
public class saveData {

    private BigInteger orderId;
    private java.sql.Date date;
     private java.sql.Date shipdate;
    private double subtotal;
    private double tax;
    private double total;
    private double ship_fee;
    private ArrayList<String> Items = new ArrayList<String>();
    private String date_format = "yyyy-MM-dd";
    private String email;
    private int count;
    private static Logger LOG = Logger.getLogger("readEmail");
   

    public void getOrderData(String txt) throws ParseException {
        Scanner scanner = new Scanner(txt);
        orderId = new BigInteger("0");
        subtotal = 0.0;
        total = 0.0;
        tax = 0.0;
        ship_fee = 0.0;
        email = "";
        count = 0;
        
        while (scanner.hasNextLine()) {
            
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.contains("Email")) {
                email = line.split(":")[1];
                continue;
            }
            if (line.matches("Order:\\s*\\d+")) {
                orderId = new BigInteger(line.split(":")[1].trim());
                continue;
            }
            if (line.matches("Date.*")) {
                SimpleDateFormat sdf = new SimpleDateFormat(date_format);
                date = new java.sql.Date(sdf.parse(line.split(":")[1]).getTime());
                continue;
            }
            if (line.matches("ShipDate.*")) {
                SimpleDateFormat sdf = new SimpleDateFormat(date_format);
                shipdate = new java.sql.Date(sdf.parse(line.split(":")[1]).getTime());
                continue;
            }
            
            if (line.matches("Subtotal.*")) {
                subtotal = Double.parseDouble(line.split(":")[1].trim());
                continue;
            }
            if (line.matches("Tax.*")) {
                tax = Double.parseDouble(line.split(":")[1]);
                continue;
            }

            if (line.matches("Total.*")) {
                total = Double.parseDouble(line.split(":")[1]);
                continue;
            }

            if (line.matches("Shipping.*")) {
                ship_fee = Double.parseDouble(line.split(":")[1]);
                continue;
            }

            Items.add(line.trim());
            count++;
        }
        
        if(shipdate == null){
            shipdate = date;
        }
    }

    public boolean saveOrderData(Db db, int orderStatusId, int userId) throws ClassNotFoundException, SQLException, ParseException {

        if (total == 0.0) {
            LOG.log(Level.WARNING, "Order :" + orderId + ", the total is zero!");
            return false;
        }

        if (!db.getOrderInfoByOrderId(orderId).isEmpty()) {
            if (db.getOrderStatusByOrderId(orderId) == orderStatusId && orderStatusId == 2) {

                String orderDate = db.getOrderDateByOrderId(orderId);
                java.util.Date orderdate = new SimpleDateFormat(date_format).parse(orderDate);
                java.sql.Date currentOrderdate = new java.sql.Date(orderdate.getTime());

                if ( shipdate.after(currentOrderdate) && count+ db.getItemAccount(orderId) <= 12) {
                    String orderInfo = db.getOrderInfoByOrderId(orderId);
                    String[] orderInfoData = orderInfo.split("#");

                    subtotal += Double.parseDouble(orderInfoData[2]);
                    tax += Double.parseDouble(orderInfoData[3]);
                    ship_fee += Double.parseDouble(orderInfoData[4]);
                    total += Double.parseDouble(orderInfoData[5]);
                } else {
                    return true;
                }

                //date = currentOrderdate; 
            } else if (db.getOrderStatusByOrderId(orderId) == 1) {
                db.deleteOrderDetailByOrderId(orderId);
            }else{
                  return true;
            }
        }  

        db.saveOrderInfo(orderId, date, subtotal, tax, ship_fee, total, orderStatusId, userId);
 
        LOG.log(Level.FINEST, "insert order" + orderId +" status is " + orderStatusId);
        for (int i = 0; i < Items.size(); i++) {
            String[] items;
            
            if(Items.get(i).matches("\\d{9}\\s\\d+\\.\\d+")){
               items = Items.get(i).split("\\s");

                db.saveOrderDetial(orderId, Integer.parseInt(items[0]), Double.parseDouble(items[1]));

                LOG.log(Level.FINEST, "Insert : " + Integer.parseInt(items[0]));
            }
                
        }
        
         return true;
    }
}
