package com.rendition.downloadfull;
import java.io.*;
import oracle.stellent.ridc.*;
import oracle.stellent.ridc.model.*;
import oracle.stellent.ridc.protocol.*;
import oracle.stellent.ridc.protocol.intradoc.*;
import oracle.stellent.ridc.common.log.*;
import oracle.stellent.ridc.model.serialize.*;
import oracle.stellent.ridc.protocol.http.*;
import java.util.List;
import java.util.*;
import java.text.*;
import java.sql.*;
/*
   RIDC utility to download all web renditions for contents from searchResults resultset which are PDF
*/

public class PDFDownload{

private static InputStream input = null;
InputStream fileStream = null;
 
      public static void downloadFiles(final ResultSet searchResults) throws IdcClientException,SQLException {
               
   	        // Create a new IdcClientManager
		IdcClientManager manager = new IdcClientManager ();
                int i=0;
                int count =0;
                Properties prop = new Properties();
		try{
                         System.out.println("###### Starting PDF Download ######");
                         input = new FileInputStream("config");
                         prop.load(input);
                         //for (DataObject search : searchResults.get(i).getRows())
                           while(searchResults.next()){
                             //System.out.println("Downloading PDF:"+ "" + searchResults.getRow()); 
                             if (searchResults.getString("dWebExtension") != null && !searchResults.getString("dWebExtension").isEmpty() && searchResults.getString("dWebExtension").equals("pdf"))
                                 {
                                  count++;     
                                 System.out.println("Downloading PDF File # :" + " " + count + " " + "and Content ID is : " + " " + searchResults.getString("dDocName"));
                                  
			         // Create a new IdcClient Connection using idc protocol (i.e. socket connection to Content Server)
                                 IdcClient idcClient = manager.createClient (prop.getProperty("IDC_PROTOCOL")+"://" + prop.getProperty("RIDC_SERVER_SOURCE") + ":" + prop.getProperty("RIDC_PORT_SOURCE"));
                                  //User to execute
                                   IdcContext userContext = new IdcContext(prop.getProperty("RIDC_SOURCE_USER"));

                        
                                   // Create an HdaBinderSerializer; this is not necessary, but it allows us to serialize the request and response data binders
			           HdaBinderSerializer serializer = new HdaBinderSerializer ("UTF-8", idcClient.getDataFactory ());
                                   DataBinder dataBinder1 = idcClient.createBinder();  
                                   dataBinder1.putLocal("IdcService", "GET_FILE");
                                   dataBinder1.putLocal("dDocName",searchResults.getString("dDocName"));
                                   dataBinder1.putLocal("dID",searchResults.getString("dID"));
                                   dataBinder1.putLocal("RevisionSelectionMethod","latest");
                                   dataBinder1.putLocal("Rendition","web");


                                  // Write the data binder for the request to stdout - Optional - Used for debugging
                                  //serializer.serializeBinder (System.out, dataBinder1);
                        
                              // Send the request to Content Server
                              ServiceResponse response1 = idcClient.sendRequest(userContext,dataBinder1);
                              //String ctype = response1.getHeader("Content-Length");
                              //System.out.println("Content Type: " + ctype);
 
                              // Create an input stream from the response
                              InputStream fis = response1.getResponseStream();
                              
                              FileOutputStream fos = new FileOutputStream(prop.getProperty("PDFDownloadPath")+searchResults.getString("dDocName")+"."+searchResults.getString("dWebExtension"));                             
 
                              // Read the data in 1KB chunks and write it to the file
                              byte[] readData = new byte[1024];
                              int j = fis.read(readData);
                              while (j != -1) 
                               {
                                 fos.write(readData, 0, j);
                                 j = fis.read(readData);
                               }
                             // Close the socket connection
                             response1.close();
                             // Close the streams
                             fos.close();
                             fis.close();
		         }i++;
                      }
                
                } catch (IdcClientException ice){
			ice.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} catch (SQLException se){
                        se.printStackTrace();
                }
	}
}

