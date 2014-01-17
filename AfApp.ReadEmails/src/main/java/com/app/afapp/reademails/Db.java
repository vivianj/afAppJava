/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yuanyuan
 */
public class Db {
    
    public Connection con = null;
    public Statement statement= null;
    public String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //public String username = "b04a669d1b1d5a",password = "e60882cf";
    //static String dbname = "jdbc:mysql://us-cdbr-azure-east-c.cloudapp.net/afappa2hwggg1ar0";
    String dbtime;
    
     public  Db(){
    }
    
    public void dbConnect(String dbname, String username, String password) throws ClassNotFoundException{   
        try {
             Class.forName(JDBC_DRIVER);
            con = (Connection) DriverManager.getConnection(dbname, username, password);
           } catch (SQLException ex) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dbColse() throws SQLException{
        con.close();
    }
    
    public void dbSaveData(String sql){
        try{
            Statement st = (Statement) con.createStatement(); 
             st.executeUpdate(sql);
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
    }
    
    public double getOrderTotalByOrderId(BigInteger orderId){
     double result = 0.0;
         
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT total FROM order_info WHERE order_id = " + orderId;
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                    result +=  rs.getDouble("total");
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
        return result;
    }
    public String getOrderInfoByOrderId(BigInteger orderId){
       String result = "";
         
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM order_info WHERE order_id = " + orderId;
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                    result += rs.getString("order_id") + "#"; 
                    result +=  rs.getString("date") + "#";
                    result += rs.getString("subtotal") + "#"; 
                    result +=  rs.getString("tax") + "#";
                    result += rs.getString("ship_fee") + "#"; 
                    result +=  rs.getString("total");
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
        return result;
    }
    
     public int getOrderStatusByOrderId(BigInteger orderId){
       String result = "";
         
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT order_status_id FROM order_info WHERE order_id = " + orderId;
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                    result = rs.getString("order_status_id");                 
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
        return Integer.parseInt(result);
    }
     
     public String getOrderDateByOrderId(BigInteger orderId){
          String date = "";
       
       try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT date FROM order_info WHERE order_id = " + orderId;
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                   date = rs.getString("date");    
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
       return date;
    }
    
    public void deleteOrderInfoByOrderId(BigInteger orderId){
       try{
            Statement st = (Statement) con.createStatement();
            String sql = "DELETE FROM order_info WHERE order_id ='" + orderId+"'" ;
            
            PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
            //stmt.setObject(1, orderId);
            st.executeUpdate(sql);
                        
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
    }
    
    public void deleteOrderDetailByOrderId(BigInteger orderId){
        try{
            Statement st = (Statement) con.createStatement();
            String sql = "DELETE FROM order_detail WHERE order_id ='" +orderId+"'";
            
            PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
            //stmt.setObject(1, orderId);
            st.executeUpdate(sql);
            
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
    }
    
    public HashMap<Integer, Double> getOrderDetailByOrderId(BigInteger orderId){
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();
         
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM order_detail WHERE order_id = " + orderId;
            
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                result.put( rs.getInt("product_detail_code"),rs.getDouble("price") );
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
        return result;
    }
    
    public String getProductDetailByItemCode(int product_detail_code){
        String result = "";
        
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM products_detail WHERE product_detail_code = " + product_detail_code;
            
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                    result += rs.getInt("long_sku_id") + "#"; 
                    result +=  rs.getString("size");
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
         return result;
    }
    
    public String getProductByLongsku(int long_sku_id){
         String result = "";
        
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM products WHERE long_sku_id = " + long_sku_id;
            
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                    result += rs.getString("long_sku") + "#"; 
                    result +=  rs.getString("category_id") + "#";
                    result +=  rs.getString("name") + "#";
                    result +=  rs.getString("color");        
               }   
                
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     
         return result;
    }
    
     public String getCategoryByCategoryId(int category_id){
         String result = "";
        
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM category WHERE category_id = " + category_id;
            
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                    result += rs.getString("category");            
               }           
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
         return result;
    }
     
     public ArrayList<String> getAllProductsByOrderId(BigInteger order_id){
         ArrayList<String> result = new ArrayList<String> ();
         String tmp = "";
         
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "select od.product_detail_code, p.long_sku, p.name, p.color, c.category_name, pd.size, od.price "
                          + "FROM order_detail od "
                          + "LEFT JOIN products_detail pd "
                          + "ON pd.product_detail_code = od.product_detail_code "
                          + "LEFT JOIN products p " 
                          + "ON p.long_sku_id = pd.long_sku_id " 
                          + "LEFT JOIN category c " 
                          + "ON c.category_id = p.category_id "
                          + "WHERE od.order_id = " + order_id;

            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                    tmp += rs.getString("product_detail_code") + "#"; 
                    tmp += rs.getString("long_sku") + "#"; 
                    tmp += rs.getString("category_name") + "#"; 
                    tmp += rs.getString("name") + "#";    
                    tmp += rs.getString("color") + "#"; 
                    tmp += rs.getString("size") + "#"; 
                    tmp += rs.getString("price") + "#"; 
                    
                    result.add(tmp);
                    tmp = "";
               }           
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
         return result;
    } 
     
     
    public void saveOrderInfo(BigInteger order, Date date, double subtotal, double tax, double ship_fee, double total, int orderStatusId, int user_id){
        try{  
            
            Statement st = (Statement) con.createStatement();
            String sql0 = "SELECT * FROM order_info WHERE order_id = " + order;
            ResultSet rs = st.executeQuery(sql0);
            
            if (rs.next()) {
                      return;
               }   
            
            String sql = "INSERT INTO order_info(order_id, date, subtotal, tax, ship_fee, total, order_status_id, user_id) VALUES( ?, ?, ?, ?, ?, ?, ?,?)";
         
            System.out.println(sql);
            PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
            stmt.setObject(1, order);
            stmt.setDate(2, date);
            stmt.setDouble(3, subtotal);
            stmt.setDouble(4, tax);
            stmt.setDouble(5, ship_fee);
            stmt.setDouble(6, total);
            stmt.setInt(7, orderStatusId);
            stmt.setInt(8, user_id);
            stmt.execute();
                    
        }catch(SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
    }
    
    public void saveOrderDetial(BigInteger order, int itemCode, double unitPrice){
         try{
            String sql = "INSERT INTO order_detail(order_id, product_detail_code, price) VALUES( ?, ?, ?)";
         
            System.out.println(sql);
            PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
            stmt.setObject(1, order);
            stmt.setInt(2, itemCode);
            stmt.setDouble(3, unitPrice);
            stmt.execute();
             
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
    }
    
     public void saveProducts(BigInteger long_sku_id, String longSku, int category_id, String name, String color, double price, String url){
          try{          
                Statement st = (Statement) con.createStatement();
                String sql0 =  "SELECT * FROM products WHERE long_sku_id = " + long_sku_id;
            
                ResultSet rs = st.executeQuery(sql0);
            
                if (rs.next()){
                    return; 
                }  
                
                String img = long_sku_id + ".jpg";
                String sql = "INSERT INTO products(long_sku_id,long_sku,category_id, name, color, price, img, url, onsweep) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
                PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
                stmt.setObject(1, long_sku_id);
                stmt.setString(2, longSku);
                stmt.setInt(3, category_id);
                stmt.setString(4, name);
                stmt.setString(5, color);
                stmt.setDouble(6, price);
                stmt.setString(7, img);
                stmt.setString(8, url);
                stmt.setInt(9, 1);
                
                stmt.execute();
           }catch(SQLException ex){
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
           }    
        }
    
       public void saveProductsDetail(BigInteger long_sku_id, HashMap<String, String> sizeCodes){
         
           try{ 
               
              Iterator it = sizeCodes.entrySet().iterator();
              while (it.hasNext()) {
                Map.Entry sizeCode = (Map.Entry)it.next();
                int sizecode = Integer.parseInt(sizeCode.getValue().toString());
                String size =  sizeCode.getKey().toString();
                 
                Statement st = (Statement) con.createStatement();
                String sql0 =  "SELECT *  FROM products_detail WHERE product_detail_code = " + sizecode;
                ResultSet rs = st.executeQuery(sql0);
              
                if (rs.next()){
                    System.out.println(size + " continuesss");
                    continue;
                }       
                System.out.println("insert " + size);
                
                String sql = "INSERT INTO products_detail(product_detail_code,long_sku_id,size) VALUES(?, ?, ?)";
            
                PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
                stmt.setInt(1, sizecode);
                stmt.setObject(2, long_sku_id);
                stmt.setString(3, size);
                stmt.execute();
              }
           }catch(SQLException ex){
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
           }    
        }
       
     public int saveUser(String email){
         try{    
                if(email.trim().isEmpty()){
                    return 2;
                }
                
                Statement st = (Statement) con.createStatement();
                String sql0 =  "SELECT user_id FROM user_email WHERE email = '" + email +"'" ;
                ResultSet rs = st.executeQuery(sql0);
            
                if (rs.next()){
                    return rs.getInt("user_id");
                }       
                String sql = "INSERT INTO user_email(email) VALUES(?)";
            
                PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.execute();
               
                
           }catch(SQLException ex){
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
           } 
         
         return 2;
     }
       
     public void updateOrderUserId(BigInteger order_id, int user_id){
          try{
                 String sql = "UPDATE order_info SET user_id = " + user_id + " WHERE order_id =" + order_id;
              
                 PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
                stmt.execute();  
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
     }
       public HashMap<String, String> getProductId_Longsku(){
          HashMap<String, String> result = new HashMap<String, String>();
        
         try{
            Statement st = (Statement) con.createStatement();
            String sql = "SELECT * FROM longsku_productid";
            
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                    result.put( rs.getString("productid"), rs.getString("longsku"));            
               }           
        }catch (SQLException ex) {
               Logger lgr = Logger.getLogger(Db.class.getName());
               lgr.log(Level.SEVERE, ex.getMessage(), ex);
       } 
         return result;  
       }
       
       public void updateOrderStatus(BigInteger orderId, int order_status_id){
           try{                    
                Statement st = (Statement) con.createStatement();
                String sql0 =  "SELECT order_status_id From order_info " + " WHERE order_id = " + orderId;
                ResultSet rs = st.executeQuery(sql0);
                
                String result = "";
                
               while (rs.next()) {
                    result = rs.getString("order_status_id");   
                    
                     if(Integer.parseInt(result) > 1){
                        return;
                   }
               } 
               
                String sql =  "UPDATE order_info SET order_status_id = " + order_status_id + " WHERE order_id = " + orderId;
              
                PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
                stmt.execute();
                 
           }catch(SQLException ex){
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
           }   
       }
}
