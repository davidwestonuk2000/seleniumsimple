package com.pancentric.utilities;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import com.pancentric.utilities.StopWatchTest;

    /**
     * This class contains methods for interacting with web services
     * This includes formatting the call and handling the response.
     */

public class WebServiceCall {
    
    private SOAPMessage soapRequestMessage;
    private String nodeName = "";
    private Map<String, String> responseValues = new HashMap<String,String>();
    private List<Map<String, String>> responseValuesList = new ArrayList<Map<String,String>>();
    private List<String> fieldNames = new ArrayList<String>();
    
    private SOAPMessage createSOAPRequest(String wsdlURL) throws Exception {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // start the response timer
        StopWatchTest webserviceReponseTimer = new StopWatchTest();
        webserviceReponseTimer.start();
        
        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = soapConnection.call(soapRequestMessage, wsdlURL);

        // stop the timer
        webserviceReponseTimer.stop();
        
        // get the request as a string
        ByteArrayOutputStream request = new ByteArrayOutputStream();
        soapRequestMessage.writeTo(request);
        String stringRequest = new String(request.toByteArray());
        
        // get the response as a string
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        soapResponse.writeTo(response);
        String stringResponse = new String(response.toByteArray());
        
                
        // print SOAP Response
        //System.out.print("Response SOAP Message:");
        //soapResponse.writeTo(System.out);
        
        return soapResponse;
    }
    
    // build the body of the SOAP message
    
    public SOAPBody createSOAPBody(Map<String, String> nameSpace) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        soapRequestMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapRequestMessage.getSOAPPart();
        
        // add cmm to name space
        //nameSpace.put("cmm", "http://schemas.yodel.co.uk/cdm/v1/cmm");
        
        // add soap envelope and name space declaration
        SOAPEnvelope envelope = soapPart.getEnvelope();
        Iterator it = nameSpace.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            envelope.addNamespaceDeclaration(pair.getKey().toString(), pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        // get SOAP body
        return envelope.getBody();
    }
    
    // build the body of the SOAP message for ODI invoke
    
    private SOAPBody createSOAPBodyOdiInvoke() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        soapRequestMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapRequestMessage.getSOAPPart();

        // add soap envelope and name space declaration
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("odi", "xmlns.oracle.com/odi/OdiInvoke/");
        
        // get SOAP body
        return envelope.getBody();
    }   
    
    
    // a public method to trigger an ODI scenario
    
    public Map<String, String> initScenwebServiceRequest(String wsdlURL, String bodyElem, List<String[]> credentials, List<String[]> request, List<String[]> variables, List<String[]> debug) throws Exception {
        // create soap body
        SOAPElement soapBody = createSOAPBodyOdiInvoke();
        SOAPElement soapBodyElem = soapBody.addChildElement(bodyElem, "odi");
        
        SOAPElement creds = soapBodyElem.addChildElement("Credentials");
        // loop to add credentials parameters
        for (Iterator<String[]> credentialsIt = credentials.iterator(); credentialsIt.hasNext(); ) {
                String[] param = credentialsIt.next();
                if(param[1]!="") {
                    SOAPElement childElement = creds.addChildElement(param[0]);
                    childElement.addTextNode(param[1]);
                }
        }
        
        
        SOAPElement req = soapBodyElem.addChildElement("Request");
        // loop to add request parameters
        for (Iterator<String[]> requestIt = request.iterator(); requestIt.hasNext(); ) {
                String[] param = requestIt.next();
                if(param[1]!="") {
                    SOAPElement childElement = req.addChildElement(param[0]);
                    childElement.addTextNode(param[1]);
                }
        }
        /*
        // loop to add variables and values
        for (Iterator<String[]> variablesIt = variables.iterator(); variablesIt.hasNext(); ) {
                String[] param = variablesIt.next();
                if(param[2]!="") {
                    SOAPElement childElement = req.addChildElement(param[0],param[1]);
                    childElement.addTextNode(param[2]);
                }
        }
        */
        
        SOAPElement deb = soapBodyElem.addChildElement("Debug");
        // loop to add debug params values
        for (Iterator<String[]> debugIt = debug.iterator(); debugIt.hasNext(); ) {
                String[] param = debugIt.next();
                if(param[1]!="") {
                    SOAPElement childElement = deb.addChildElement(param[0]);
                    childElement.addTextNode(param[1]);
                }
        }
        
        // save the message
        soapRequestMessage.saveChanges();
        /* Print the request message */
        System.out.print("Request SOAP Message:");
        soapRequestMessage.writeTo(System.out);
        
        // send request and get response
        SOAPMessage soapResponse = createSOAPRequest(wsdlURL);
        
        
        // store response
        SOAPBody responseSoapBody = soapResponse.getSOAPBody();
        Iterator nodeIt = responseSoapBody.getChildElements();
                
        // get the full contents of the SOAP message
        return getContents(nodeIt,"   ");
        
    }

    // a public method to build up a web service request and get the response
    
    public List<Map<String, String>> webServiceRequestRepeating(String wsdlURL, String requestType, String elementType, String elementNameSpace, List<List<String[]>> elements, Map<String, String> nameSpaceList) throws Exception {
        
        // create soap body
        SOAPElement soapBody = createSOAPBody(nameSpaceList);
        SOAPElement soapBodyElem = soapBody.addChildElement(requestType, elementNameSpace);
        
        // loop the elements and add to list
        for (Iterator<List<String[]>> elementIt = elements.iterator(); elementIt.hasNext(); ) {
            List<String[]> element = elementIt.next();
            SOAPElement listElementContainer = soapBodyElem.addChildElement(elementType,elementNameSpace);
            
            // iterate fields   
            for (Iterator<String[]> attributueIt = element.iterator(); attributueIt.hasNext(); ) {
                String[] attributes = attributueIt.next();
                //if(attributes[2]!="") {
                    SOAPElement childElement = listElementContainer.addChildElement(attributes[0],attributes[1]);
                    attributes[2] = attributes[2].replace(" ", "");
                    childElement.addTextNode(attributes[2]);
                //}
            }   
        }
        
        // save the message
        soapRequestMessage.saveChanges();

        // send request and get response
        SOAPMessage soapResponse = createSOAPRequest(wsdlURL);
        
        // store response
        SOAPBody responseSoapBody = soapResponse.getSOAPBody();
        Iterator nodeIt = responseSoapBody.getChildElements();
        
        // get the full contents of the SOAP message
        List<Map<String, String>> result = getContentsList(nodeIt,"   ");
        result.add(responseValues);
        return result;
    }
    
    // repeating web service request
    
    // a public method to build up a web service request and get the response
    
    public Map<String, String> webServiceRequestBasic(String wsdlURL, String bodyElem, String nameSpace, List<String[]> parameters, Map<String, String> nameSpaceList) throws Exception {
        
        // create soap body
        SOAPElement soapBody = createSOAPBody(nameSpaceList);
        SOAPElement soapBodyElem = soapBody.addChildElement(bodyElem, nameSpace);        
        
        // loop to add service parameters
        for (Iterator<String[]> paramsIt = parameters.iterator(); paramsIt.hasNext(); ) {
                String[] param = paramsIt.next();
                if(param[2]!="") {
                    SOAPElement childElement = soapBodyElem.addChildElement(param[0],param[1]);
                    childElement.addTextNode(param[2]);
                }
        }
        
        // save the message
        soapRequestMessage.saveChanges();
        /* Print the request message */
        //System.out.print("Request SOAP Message:");
       // soapRequestMessage.writeTo(System.out);

        // send request and get response
        SOAPMessage soapResponse = createSOAPRequest(wsdlURL);
        
        // store response
        SOAPBody responseSoapBody = soapResponse.getSOAPBody();
        Iterator nodeIt = responseSoapBody.getChildElements();
        
        // get the full contents of the SOAP message
        return getContents(nodeIt,"   ");
    
    }
    
    public void addParameters(SOAPElement element,  List<String[]> parameters) throws Exception {
        // loop to add service parameters
        for (Iterator<String[]> paramsIt = parameters.iterator(); paramsIt.hasNext(); ) {
                String[] param = paramsIt.next();
                if(param[2]!="") {
                    SOAPElement childElement = element.addChildElement(param[0],param[1]);
                    childElement.addTextNode(param[2]);
                }
        }
        
        // save the message
        soapRequestMessage.saveChanges();
    }
    
    public Map<String, String> makeSoapRequest(String wsdlURL) throws Exception {
    
        //System.out.print("Request SOAP Message:");
        soapRequestMessage.writeTo(System.out);
    
        // send request and get response
        SOAPMessage soapResponse = createSOAPRequest(wsdlURL);
        
        
        // store response
        SOAPBody responseSoapBody = soapResponse.getSOAPBody();
        Iterator nodeIt = responseSoapBody.getChildElements();
        
        // get the full contents of the SOAP message
        return getContents(nodeIt,"   ");
    
    }
    
    // get the response of the SOAP message
    public Map<String, String> getContents(Iterator iterator, String indent) {
        // SOAP response iterator
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            SOAPElement element = null;
            Text text = null;
            if (node instanceof SOAPElement) {
              element = (SOAPElement)node;
              //QName name = element.getElementQName();
              //System.out.println("elem"+element.getNodeName());
              if (element.getNodeName()!=null) {
                  nodeName = element.getNodeName();
              }
              
              Iterator attrs = element.getAllAttributesAsQNames();
              while (attrs.hasNext()){
                QName attrName = (QName)attrs.next();
              //  System.out.println(indent + " Attribute name is " + 
              //    attrName.toString());
              //  System.out.println(indent + " Attribute value is " + 
              //    element.getAttributeValue(attrName));
                }
              Iterator iter2 = element.getChildElements();
              getContents(iter2, indent + " ");
            }
            else {
              text = (Text) node;
              String content = text.getValue();
              //  System.out.println(indent + "Name is " + nodeName);
             // System.out.println(indent + "Content is: " + content);
                //nodeName=nodeName.substring((nodeName.indexOf(":")+1));
                if (responseValues.containsKey(nodeName)==false) {
                    responseValues.put(nodeName, content);
                }
            }
        }
        
        return responseValues;
    }
    
    // get the response of the SOAP message
    public List<Map<String, String>> getContentsList(Iterator iterator, String indent) {
        
        // SOAP response iterator
        while (iterator.hasNext()) {
            Node node = (Node) iterator.next();
            SOAPElement element = null;
            Text text = null;
            if (node instanceof SOAPElement) {
              element = (SOAPElement)node;

              if (element.getNodeName()!=null) {
                  nodeName = element.getNodeName();
              }
              
              Iterator attrs = element.getAllAttributesAsQNames();
              while (attrs.hasNext()){
                QName attrName = (QName)attrs.next();
                //System.out.println(indent + " Attribute name is " + 
                //attrName.toString());
                //System.out.println(indent + " Attribute value is " + 
                //element.getAttributeValue(attrName));
                }
              Iterator iter2 = element.getChildElements();
              getContentsList(iter2, indent + " ");
            }
            else {
                text = (Text) node;
                String content = text.getValue();

                //nodeName=nodeName.substring((nodeName.indexOf(":")+1));
                
                if(fieldNames.contains(nodeName)) {
                    responseValuesList.add(responseValues);
                    fieldNames = new ArrayList<String>();
                    responseValues = new HashMap<String,String>();
                }
                
                //System.out.println(indent + "Name is " + nodeName);
                //System.out.println(indent + "Content is: " + content);
                
                fieldNames.add(nodeName);
                responseValues.put(nodeName, content);
            }
            
        }
        
        return responseValuesList;
    }
}