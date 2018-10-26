package com.pancentric.utilities;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
//import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import javax.naming.*;
//import weblogic.jms.extensions.*;

public class JMSClient {
    ArrayList<InitialContext> ctxs;
    ArrayList<JMXConnector> jmxCons;
    String[] urls;
    String username;
    String password;
    String queueName;
    String topicName;
    String topicSubscriber;
    
    public JMSClient(String[] urlsa, String usernamea, String passworda) throws Exception {
        super();
        ctxs = new ArrayList<InitialContext>();
        jmxCons = new ArrayList<JMXConnector>();
        urls=urlsa;
        username=usernamea;
        password=passworda;
        
        try {
           
            init(urls, username, password);
            
        } catch (Exception e) {
            System.out.println("Exception!");
            System.out.println("--- Message: " + e.getMessage());
            System.out.println("--- Cause: " + e.getCause());
        }
    }
    
    public enum JMSResourceType {
        QUEUE,TOPIC
    }

    @SuppressWarnings("unchecked")
    public void init(String[] urls, String username, String password) throws Exception {
        for (String url : urls) {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
            properties.put(Context.PROVIDER_URL, "t3://" + url);
            properties.put(Context.SECURITY_PRINCIPAL, username);
            properties.put(Context.SECURITY_CREDENTIALS, password);
            
            ctxs.add(new InitialContext(properties));
            
            msg("Got InitialContext for " + url,0);
            
            String fullServerURL = "service:jmx:iiop://" + url + "/jndi/weblogic.management.mbeanservers.runtime";
            JMXServiceURL serviceUrl = new JMXServiceURL(fullServerURL);
                    
            Hashtable env = new Hashtable<String, String>();
            env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
            env.put(Context.PROVIDER_URL, "t3://" + url);
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);

            JMXConnector jmxCon = JMXConnectorFactory.newJMXConnector(serviceUrl, env);
            msg("JMX Connector is " + jmxCon,0);
            jmxCon.connect();
            
            jmxCons.add(jmxCon);   
        }
    }
    
    public void closeConnections() throws Exception {
        msg("Closing JMX connections...", 0);
        Iterator<JMXConnector> jmxIt = jmxCons.iterator();
        JMXConnector jmxCon;
        while (jmxIt.hasNext()) {
            jmxCon = jmxIt.next();
            msg("Closing connection " + jmxCon, 1);
            jmxCon.close();
        }
        msg("Closing Initial Context connections...", 0);
        Iterator<InitialContext> ctxIt = ctxs.iterator();
        while (ctxIt.hasNext()) {
            ctxIt.next().close();
        }
        msg("... done!", 0);
    }
    
    public Long getCurrentMsgCount(String resourceName, JMSResourceType resourceType) throws Exception {           
        msg("Getting current message count for " + resourceType + " " + resourceName, 0);
        Iterator<JMXConnector> it = jmxCons.iterator();
        Long msgCount = new Long(0);
        JMXConnector jmxCon;
        while (it.hasNext()) {
            jmxCon = it.next();
            MBeanServerConnection con = jmxCon.getMBeanServerConnection();
            
            String objectQuery = "com.bea:Type=JMSDestinationRuntime,Name=*!*" + resourceName + "*,*";
            ObjectName query_obj_name = new ObjectName(objectQuery);
            
            Set<ObjectName> searchResult = con.queryNames(query_obj_name, null);
            for (ObjectName obj_name : searchResult) {
                String actualName = obj_name.getKeyProperty("Name");
                Long currentMsgCount = (Long) con.getAttribute(obj_name, "MessagesCurrentCount");
                msg("Found " + currentMsgCount + " msgs in instance " + actualName, 1);
                msgCount += currentMsgCount;
            }
        }
        msg("Found a total of " + msgCount + " messages", 1);
        return msgCount;
    }
    
    public Long getTotalMsgCount(String resourceName, JMSResourceType resourceType) throws Exception {
        msg("Getting total message count for " + resourceType + " " + resourceName, 0);
        Iterator<JMXConnector> it = jmxCons.iterator();
        Long msgCount = new Long(0);
        JMXConnector jmxCon;
        while (it.hasNext()) {
            jmxCon = it.next();
            MBeanServerConnection con = jmxCon.getMBeanServerConnection();
            
            String objectQuery = "com.bea:Type=JMSDestinationRuntime,Name=*!*" + resourceName + "*,*";
            ObjectName query_obj_name = new ObjectName(objectQuery);
            
            Set<ObjectName> searchResult = con.queryNames(query_obj_name, null);
            for (ObjectName obj_name : searchResult) {
                String actualName = obj_name.getKeyProperty("Name");
                Long totalMsgCount = (Long) con.getAttribute(obj_name, "MessagesReceivedCount");
                msg("Found " + totalMsgCount + " msgs in instance " + actualName, 1);
                msgCount += totalMsgCount;
            }
        }
        msg("Found a total of " + msgCount + " messages", 1);
        return msgCount;
    }
    
    public Long getCurrentConsumersCount(String resourceName, JMSResourceType resourceType) throws Exception {
        msg("Getting current consumers count for " + resourceType + " " + resourceName, 0);
        Iterator<JMXConnector> it = jmxCons.iterator();
        Long consumersCount = new Long(0);
        JMXConnector jmxCon;
        while (it.hasNext()) {
            jmxCon = it.next();
            MBeanServerConnection con = jmxCon.getMBeanServerConnection();
            
            String objectQuery = "com.bea:Type=JMSDestinationRuntime,Name=*!*" + resourceName + "*,*";
            ObjectName query_obj_name = new ObjectName(objectQuery);
            
            Set<ObjectName> searchResult = con.queryNames(query_obj_name, null);
            for (ObjectName obj_name : searchResult) {
                String actualName = obj_name.getKeyProperty("Name");
                Long currentConsumersCount = (Long) con.getAttribute(obj_name, "ConsumersCurrentCount");
                msg("Found " + currentConsumersCount + " consumers in instance " + actualName, 1);
                consumersCount += currentConsumersCount;
            }
        }
        msg("Found a total of " + consumersCount + " consumers", 1);
        return consumersCount;
    }
    
    public Long getTopicSubscriberCurrentMsgCount(String subscriberName) throws Exception {
        msg("Getting topic subscriber current message count for " + subscriberName, 0);
        Iterator<JMXConnector> it = jmxCons.iterator();
        Long msgCount = new Long(0);
        JMXConnector jmxCon;
        while (it.hasNext()) {
            jmxCon = it.next();
            MBeanServerConnection con = jmxCon.getMBeanServerConnection();
            
            String objectQuery = "com.bea:Type=JMSDurableSubscriberRuntime,Name=*!*" + subscriberName + "*,*";
            ObjectName query_obj_name = new ObjectName(objectQuery);
            
            Set<ObjectName> searchResult = con.queryNames(query_obj_name, null);
            for (ObjectName obj_name : searchResult) {
                msg("Checking object " + obj_name,1);
                String actualTopicName = obj_name.getKeyProperty("Name");
                Long currentQueueMsgCount = (Long) con.getAttribute(obj_name, "MessagesCurrentCount");
                msg("Found " + currentQueueMsgCount + " msgs in topic " + actualTopicName, 1);
                msgCount += currentQueueMsgCount;
            }
        }
        msg("Found a total of " + msgCount + " messages", 1);
        return msgCount;
    }
    
    private void msg(String message, int level) {
        Character prefixChar = '+';
        Character space = ' ';
        
        for (int i=0; i < level; i++)
            System.out.print(prefixChar);
        
        if (level > 0)
            System.out.println(space + message);
        else
            System.out.println(message);
    }
}
