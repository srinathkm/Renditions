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
 * @author Srinath Menon 
 * 
 * Overview :  RIDC utility that searches for content items and downloads a file with all the content ID's and the corresponding Web Extensions
               
*/

public class FullExportReport {
	// RIDC connection information
	private static final String IDC_PROTOCOL = "";
	private static final String RIDC_SERVER_SOURCE = "";
	private static final String RIDC_PORT_SOURCE = "";
        private static IdcClientManager m_idcClientManager;
        private static IntradocClient m_idcClient;

	private static final String UTF8 = "UTF-8";
        
        private static Properties prop = new Properties();
        private static InputStream input = null;
        InputStream fileStream = null;
	

	
        // User to execute service calls as
	private static final String USER = "";

	// Resultset to store search and rendition information :
        public static DataResultSet SearchResults = null;
        public static DataResultSet manifest = null;
               
        //List initialized to store the values for search and renditions  
        /*public static  List<DataResultSet> searchList = new ArrayList<DataResultSet>(); 
        public static  List<DataResultSet> renditionList = new ArrayList<DataResultSet>(); 
        public static ArrayList<String> rowList = new ArrayList<String>(5);*/
       
        public static  ResultSet searchResults; 
        public static DataResultSet.Field dDocName = new DataResultSet.Field("dDocName");
        public static DataResultSet.Field dID = new DataResultSet.Field("dID");
        public static DataResultSet.Field dWebExtension = new DataResultSet.Field("dWebExtension"); 
        public static DataResultSet.Field dRendition1 = new DataResultSet.Field("dRendition1");
        public static DataResultSet.Field dRendition2 = new DataResultSet.Field("dRendition2");


          public static void createReports(final ResultSet searchResults) throws IdcClientException,SQLException { 
                // DataBinder for service call
         
                try{
                    System.out.println("Creating reports for Full Search, PDF , JPG and Thumbnails");
                    input = new FileInputStream("config");

                     //Load the input to read details from config file
                     prop.load(input);
                

                    /*      This is done to ensure that debugs can be written to console as well*/
                    /*      Fileoutputstream will write data to printstream using System.setOut 
                            System.out.println to console for debug information*/
                 
                    FileOutputStream fosSearch = new FileOutputStream(prop.getProperty("ListDownloadPath")+"Searchlist.txt");
                    FileOutputStream fosPdf = new FileOutputStream((prop.getProperty("ListDownloadPath")+"pdf-list.txt"));
                    FileOutputStream fosJpg = new FileOutputStream((prop.getProperty("ListDownloadPath")+"jpg-list.txt"));                    
                    FileOutputStream fosThumb = new FileOutputStream((prop.getProperty("ListDownloadPath")+"thumb-list.txt"));
                    FileOutputStream fosPreview = new FileOutputStream((prop.getProperty("ListDownloadPath")+"preview-list.txt"));
                    
                    /*Printstreams initialized to write to text files for search reports*/
                    PrintStream SearchList = new PrintStream(fosSearch);
                    PrintStream pdf = new PrintStream(fosPdf);
                    PrintStream jpg = new PrintStream(fosJpg);
                    PrintStream thumb = new PrintStream(fosThumb); 
                    PrintStream preview = new PrintStream(fosPreview);

                    
                  while (searchResults.next()) {
                  
                  //System.setOut(SearchList);
                  //System.out.println("Content Id is : " + searchResults.getString("dDocName") + " " +" and its WebExtenstion is:"+ searchResults.getString("dWebExtension"));
                  
                  SearchList.println("Content Id is : " + searchResults.getString("dDocName") + " " +" and its WebExtension is:"+ searchResults.getString("dWebExtension"));
                   
                  //if(searchResults.getString("dWebExtension") != null && !searchResults.getString("dWebExtension").isEmpty()){      

                   // Null Value check to ensure NullPointerException is handled
                    if(searchResults.getString("dWebExtension") != null && !searchResults.getString("dWebExtension").isEmpty()&&searchResults.getString("dRendition1") != null && searchResults.getString("dRendition2") != null && !searchResults.getString("dRendition1").isEmpty() && !searchResults.getString("dRendition2").isEmpty()){

                 
                  if(searchResults.getString("dWebExtension").equals("pdf") ){
                                //System.setOut(pdf);
                                pdf.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dWebExtension"));
                             }//pdf file format block closed
                  
                  if(searchResults.getString("dWebExtension").equals("jpg") ){
                                //System.setOut(jpg);
                                jpg.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dWebExtension"));
                             } // jpg file format block closed
                  
                  if(searchResults.getString("dRendition1").equals("T") || searchResults.getString("dRendition2").equals("T")){
                          //System.out.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dRendition1"));
                                //System.setOut(thumb);
                                thumb.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dWebExtension"));
                             } // thumbnail block closed
                  
                  if(searchResults.getString("dWebExtension").equals("jpg") &&( searchResults.getString("dRendition1").equals("Z")||searchResults.getString("dRendition2").equals("Z"))) 
                        {
                          //System.out.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dRendition1"));
                                preview.println(searchResults.getString("dDocName") +"."+ searchResults.getString("dWebExtension"));
                        } // Preview block closed
                      } // IF block closed - checking for null values
                 } //While loop closed 
                 /*Close the print streams */  
                 SearchList.close();
                 pdf.close();
                 jpg.close();
                 thumb.close(); 
                 preview.close();
                 fosSearch.close();
                 fosPdf.close();
                 fosJpg.close();
                 fosThumb.close();
                 fosPreview.close();
               } //Try block closed 
                catch (IOException ioe){
                ioe.printStackTrace();
               } // Catch block closed  
                catch (SQLException e){
                e.printStackTrace();
              }
        } // Main block closed
}
