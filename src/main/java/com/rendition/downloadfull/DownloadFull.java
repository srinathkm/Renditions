package com.rendition.downloadfull;

import java.io.*;
import oracle.stellent.ridc.*;
import oracle.stellent.ridc.model.*;
import oracle.stellent.ridc.protocol.intradoc.*;
import oracle.stellent.ridc.common.log.*;
import oracle.stellent.ridc.model.serialize.*;
import oracle.stellent.ridc.protocol.http.*;
import java.util.List;
import java.util.*;
import java.text.*;
import java.sql.*;

/**
 * Created by srinath on 5/12/16.
 */
public class DownloadFull {
    // RIDC connection variables initialized
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

    public static ResultSet searchResults;

    public static DataResultSet.Field dDocName = new DataResultSet.Field("dDocName");
    public static DataResultSet.Field dID = new DataResultSet.Field("dID");
    public static DataResultSet.Field dWebExtension = new DataResultSet.Field("dWebExtension");
    public static DataResultSet.Field dRendition1 = new DataResultSet.Field("dRendition1");
    public static DataResultSet.Field dRendition2 = new DataResultSet.Field("dRendition2");



    public static void main(final String[] args){
        try {
                        /*Search contents based on query and create full search list,pdf,jpg and thumbnail lists*/

            searchResults=searchUCM();
            searchResults.last();
            int total = searchResults.getRow();
            searchResults.beforeFirst();
            System.out.println("\n\n" + "Total results for search query is :" + " " + total + "\n\n");
            FullExportReport.createReports(searchResults);
            System.out.println("###### Completed generating Reports ######");
                        /*
                          Download renditions :
                          //AccessRenditions.downloadFiles(searchList,renditionList);
                        */

                        /* Download only pdf web renditions from the search results*/
            searchResults=searchUCM();
            PDFDownload.downloadFiles(searchResults);
            System.out.println("###### Completed Downloading PDF Files ######");

                        /* Download only jpg web renditions from the search results*/
            searchResults=searchUCM();
            JPGDownload.downloadFiles(searchResults);
            System.out.println("###### Completed Downloading JPG Files ######");

                       /* Download thumbnails for all the files which has dRendition1=T set from the search results*/
            searchResults=searchUCM();
            ThumbnailDownload.downloadFiles(searchResults);
            System.out.println("###### Completed Downloading Thumbnail Files ######");

                       /* Download Preview renditions for all files where image is jpg format and renditions exist */
            searchResults=searchUCM();
            PreviewDownload.downloadFiles(searchResults);
            System.out.println("###### Completed Downloading Preview rendition Files ######");


        } catch (final IdcClientException ice){
            ice.printStackTrace(System.out);
        }catch (final SQLException se){
            se.printStackTrace(System.out);
        }
    }



    private static ResultSet searchUCM() throws IdcClientException,SQLException {

        // DataBinder for service call

        try{
            // Read the config file
            input = new FileInputStream("config");

            // Load the config file
            prop.load(input);

            System.out.println("\n\n"+"Running Search Query: " +  prop.getProperty("query") + "\n\n");

            // Initialize Database connection details - read from config file
            String url="jdbc:oracle:thin:@" + prop.getProperty("dbhostname") + ":" +prop.getProperty("dblistenport") +":" + prop.getProperty("dbsid");

            // Initialize connection to DB schema
            Connection conn = DriverManager.getConnection(url,prop.getProperty("dbwccuser"),prop.getProperty("dbwccpassword"));
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            //Execute query based on the query read from config file
            String sql = prop.getProperty("query");

            //Store the results in result set
            searchResults = stmt.executeQuery(sql);


        } //Try block closed
        catch (IOException ioe){
            ioe.printStackTrace();
        } // Catch block closed
        catch (SQLException e){
            e.printStackTrace();
        }return searchResults;
    } // Main block closed
}

