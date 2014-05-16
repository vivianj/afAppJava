/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.afapp.reademails;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.HtmlCleaner;

public class InboxReader {

    static int orderStatusId = 1;
    private static Logger LOG = Logger.getLogger("readEmail");
    static public Db db = new Db();
    static int userId = 0;
    static String af_confirmation_regex = ".*?order #.*\\d+ confirmation.*";
    static String af_ship_regex = ".*?order #.*\\d+ has shipped.*";
    static String af_orderId_regex = ".*?order #.*?(\\d+)\\s.*";
    static String af_store_ereceipt_regex = ".*?Your E-Receipt from Abercrombie & Fitch.*";
    private static boolean move = false;

    public static void readEmail() throws ClassNotFoundException, SQLException {

        ArrayList<Message> orderMsgs = new ArrayList<Message>();
        ArrayList<Message> shipMsgs = new ArrayList<Message>();

        String host = "imap.gmail.com";
        String protocol = "imaps";
        String username = "kangyihong001@gmail.com";
        String password = "831218xx";
        String folder = "Inbox";

        Flags flag = new Flags(Flags.Flag.ANSWERED);

        try {
            Store store = connectEmail(username, password, host, protocol);

            Folder inbox = store.getFolder(folder);
            Folder orderFolder = store.getFolder("OrderConfirmation");
            Folder shipFolder = store.getFolder("ShippingConfirmation");

            inbox.open(Folder.READ_WRITE);
            FlagTerm ft = new FlagTerm(flag, false);

            Message messages[] = inbox.search(ft);

            int i = 0;

            db.dbConnect();

            for (Message message : messages) {

                String subject = message.getSubject();
                orderStatusId = 1;

                if (subject.toLowerCase().matches(af_confirmation_regex) || subject.toLowerCase().matches(af_ship_regex)) {

                    i++;

                    LOG.log(Level.FINEST, "Email Subject:" + subject);
                    Address[] from = message.getFrom();
                    String email = getFromAdd(from[0].toString().trim());

                    LOG.log(Level.FINEST, "Email From:" + email);
                    userId = db.saveUser(email);

                    String orderId = extractData.getContentFromLine(subject, af_orderId_regex);
                    BigInteger order_id = new BigInteger(orderId.trim());


                    if (subject.toLowerCase().matches(af_ship_regex)) {
                        orderStatusId = 2;
                    }

                    try {
                        printParts(message, subject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (move) {
                        if (subject.toLowerCase().matches("af_confirmation_regex")) {
                            orderMsgs.add(message);
                        } else {
                            shipMsgs.add(message);
                        }
                    }


                }

                if (subject.matches(af_store_ereceipt_regex)) {
                    LOG.log(Level.FINEST, "Email Subject:" + subject);
                    Address[] from = message.getFrom();
                    String email = getFromAdd(from[0].toString().trim());

                    LOG.log(Level.FINEST, "Email From:" + email);
                    userId = db.saveUser(email);

                    orderStatusId = 2;

                    try {
                        printParts(message, subject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (move) {
                        shipMsgs.add(message);
                        move = false;
                    }

                }
            }

            Message[] ordermessages = new Message[orderMsgs.size()];
            orderMsgs.toArray(ordermessages);
            Message[] shipmessages = new Message[shipMsgs.size()];
            shipMsgs.toArray(shipmessages);

            moveMessages(ordermessages, inbox, orderFolder);
            moveMessages(shipmessages, inbox, shipFolder);

            db.dbColse();

            LOG.log(Level.FINEST, "Total number of emails :" + i);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void moveMessages(Message[] messages, Folder fromFolder, Folder destFolder) throws MessagingException {
        if (!fromFolder.exists()) {
            System.out.println("Invalid input email folder!");
            LOG.log(Level.FINEST, "Invalid input email folder!");
            return;
        }

        if (!destFolder.exists()) {
            destFolder.create(Folder.HOLDS_MESSAGES);
            LOG.log(Level.FINEST, "Create new folder!");
        }

        fromFolder.copyMessages(messages, destFolder);
        LOG.log(Level.FINEST, "move successful!" + messages.length);

        Flags deleted = new Flags(Flags.Flag.DELETED);
        fromFolder.setFlags(messages, deleted, true);
    }

    public static String getFromAdd(String address) {
        String from_address_regex = ".*?<(.*?)>.*";
        String fromAdd = extractData.getContentFromLine(address, from_address_regex);
        System.out.println("From Address: " + fromAdd);

        return fromAdd;
    }

    public static void printParts(Part p, String subject) throws Exception {
        Object o = p.getContent();
        if (o instanceof String) {
            String input = "";
            if (subject.matches(af_store_ereceipt_regex)) {
                String s;
                HtmlCleaner hc = new HtmlCleaner();
                s = hc.clean((String) o).getText().toString().replaceAll("[\\\r]+", "").replaceAll("&nbsp;", "");

                input = extractData.extractReceiptData(s);
            } else {
                input = extractData.extractEmailData((String) o);
            }
            saveData savedata = new saveData();

            System.out.println("Input : ");
            System.out.println(input);
            System.out.println("///////////////////////////");

            LOG.log(Level.FINEST, "Input :");
            LOG.log(Level.FINEST, input);
            LOG.log(Level.FINEST, "**************************");

            if (input.contains("Order")) {
                savedata.getOrderData(input);
                move = savedata.saveOrderData(db, orderStatusId, userId);

            }
        } else if (o instanceof Multipart) {
            System.out.println("This is a Multipart");
            Multipart mp = (Multipart) o;
            int count = mp.getCount();

            for (int i = 0; i < count; i++) {
                printParts(mp.getBodyPart(i), subject);
            }
        } else if (o instanceof InputStream) {
            System.out.println("This is just an input stream");
            InputStream is = (InputStream) o;
            int c;
        }
    }

    public static Store connectEmail(String email, String password, String host, String protocol) throws NoSuchProviderException, MessagingException {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", protocol);

        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
        Store store = session.getStore(protocol);
        store.connect(host, email, password);

        return store;
    }
}