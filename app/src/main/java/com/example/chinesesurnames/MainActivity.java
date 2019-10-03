package com.example.chinesesurnames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button button;
	private Button buttonStart;
	// create map to store This Map has a Key that is a string and Value that is a ListArray of string
	public HashMap<String, List<String>> RankingMap = new HashMap<String, List<String>>();
	public final String HTML_HEADER = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'> <html xmlns='http://www.w3.org/1999/xhtml'> <head>     <meta http-equiv='content-type' content='text/html; charset=utf-8' />     <title></title> <style type='text/css' media='Screen'> #navigation ul {   list-style-type: none;   padding: 0;   margin: 0;   width: 140px; } #navigation a {   text-decoration: none;   display: block;   padding: 3px 12px 3px 8px;   background-color: #D2FFFF;   color: #000;   border: 1px solid #ddd;font-size:18pt;font-weight:bold; } #navigation a:active {   padding: 2px 13px 4px 7px;   background-color: #444;   color: #eee;   border: 1px solid #333; }  #navigation li li a {   text-decoration: none;   padding: 3px 3px 3px 17px; white-space:nowrap;  background-color: #F3F3F3; font-size:12pt;  color: #111111;width: 100%; } #navigation li li a:active {   padding: 2px 4px 4px 16px;   background-color: #888;   color: #000; } </style> <script type='text/javascript'> function swap(targetId){   if (document.getElementById){ target = document.getElementById(targetId); if (target.style.display == 'none'){     target.style.display = ''; } else{     target.style.display = 'none'; } } } </script> </head> <body>     <div id='navigation'> <ul> <!--HEADER SECTION ABOVE --> ";
	public final String HTML_FOOTER = "</ul></div> </body> </html>";
	public final String HTML_CLICK_HEADER = " <li><a href='#' onclick=swap('CLICK_HDR_ID');return false;>_FIRST_CHAR_</a><ul id='CLICK_HDR_ID' style='display: none;'>";
	public final String HTML_CLICK_HEADER_END = " </ul></li>";
	public final String HTML_ITEM_LINE = " <li><a href='_THEURL_' class='namelink'>_THE_NAME_</a></li>";
	
	ArrayList<String> SurnameUrlList = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		buttonStart = (Button) findViewById(R.id.button1);
		buttonStart.getBackground().setAlpha(210);
		button = (Button) findViewById(R.id.button2);
		button.getBackground().setAlpha(210);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), AboutPage.class);
				startActivity(intent);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public int LoadTextFilefromResource(int resourceId) {
        // The InputStream opens the resourceId and sends it to the buffer
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        SurnameUrlList.clear();
        
        try {
            // While the BufferedReader readLine is not null 
            while ((readLine = br.readLine()) != null) {
            //Log.d("TEXT", readLine);
                readLine = readLine.replace("~#~","~http://en.wikipedia.org/wiki~");
            	SurnameUrlList.add(readLine);
        }

        // Close the InputStream and BufferedReader
        is.close();
        br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return SurnameUrlList.size();
    } 
    
    
    public String LoadTextFilefromResource2String(int resourceId) {
        // The InputStream opens the resourceId and sends it to the buffer
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        StringBuilder theSb = new StringBuilder();
        
        
        try {
            // While the BufferedReader readLine is not null 
            while ((readLine = br.readLine()) != null) {
            //Log.d("TEXT", readLine);
            	theSb.append(readLine); 
            	
        }

        // Close the InputStream and BufferedReader
        is.close();
        br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return theSb.toString();
    	
    }
    
    public void doReadList(View v)
    {
    	//LoadTextFilefromResource(R.raw.surnameurllist);
		LoadTextFilefromResource(R.raw.surnameurllist_new);
    	
    	//OpenWebPage(HTML_HEADER + HTML_CLICK_HEADER + HTML_ITEM_LINE + HTML_ITEM_LINE + HTML_ITEM_LINE + HTML_ITEM_LINE + HTML_CLICK_HEADER_END + HTML_FOOTER ,"Testing");
    	genHTMLPage();
    }

    public void OpenWebPage(String url,String PageTitle,HashMap<String, List<String>> rankingMap) 
    { // handles the opening of a webpage in the webpage Activity
		Log.d("看这里","hahaopenwebpage");
     	Intent intent = new Intent(getApplicationContext(), SurnameList.class);
		Log.d("看PageTitle", "kk"+PageTitle);
		intent.putExtra("PAGE_TITLE", PageTitle);
 	    intent.putExtra("LINK_CLICKED", "");
 	    intent.putExtra("RankingMap",rankingMap); //Send the Ranking Map
        intent.putExtra("RAW_HTML", url); // here put url in the Intent Data list so it can be retrieved in the called activity
		Log.d("看url", url);
		startActivity(intent);
		Log.d("看intent", url);
    }
    
    public void genHTMLPage()
    {
		Log.d("kkk","haha5genhtml");
    	char FirstChar ='$';
    	String aLine = null;
    	String HTML_string = "";
    	String TmpStrg,RankingKey;
    	String[] StrgLineArr = null;
    	
    	RankingMap.clear(); // Clear this Map
    	
    	for(int x = 0; x < SurnameUrlList.size(); ++x )
    	{
    		aLine = SurnameUrlList.get(x);
    		
    		if (Character.toUpperCase(FirstChar) != Character.toUpperCase(aLine.charAt(0)))
    		{ //Make Click Header
    			if(HTML_string != null) HTML_string = HTML_string + HTML_CLICK_HEADER_END; 
    			FirstChar = Character.toUpperCase(aLine.charAt(0));
    			TmpStrg = HTML_CLICK_HEADER;
    			TmpStrg = TmpStrg.replaceAll("_FIRST_CHAR_", Character.toString(FirstChar)); //Character.toString(FirstChar)
    			HTML_string = HTML_string +  TmpStrg.replaceAll("CLICK_HDR_ID", FirstChar+"_ID");    
    			
    		}
    		// Now split the PINYIN~HANZHI~URL~RANKING string to array 
    		//zhan~战~http://en.wikipedia.org/wiki/Zhan_(surname)~351-400X
    		StrgLineArr = aLine.split("~");
    		//if (StrgLineArr.length == 4)
            if (StrgLineArr.length == 5)
    		{
    			TmpStrg =  HTML_ITEM_LINE;
    			TmpStrg = TmpStrg.replaceAll("_THE_NAME_", StrgLineArr[0] + " " + StrgLineArr[1]);
                //if (StrgLineArr[2].length() != 1) {
                //    HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", "http://" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]) + "~" + StrgLineArr[2].substring(7));
                //}else{
                    //HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", "http://" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]) + "~" + StrgLineArr[2]);
                //}
                //if (StrgLineArr[2] == "#"){
                //    Log.d("看空url",aLine);
                //    StrgLineArr[2] = "http://" + "#";
                //    Log.d("看改好的url",StrgLineArr[2]);
                //}
				HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", StrgLineArr[2] + "~" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]));
    			RankingKey = StrgLineArr[3]; // get the Ranking Value
    			
    			
    //===================================================================================================================			
    			//---- this was added to do the Ranking Data to be used for HTML 
    			
    			if ((RankingKey.length() > 0)  ) //if there is a ranking for this Family name
    			{
        			//----- First check if that Ranking key exist 
        			if ( RankingMap.containsKey(RankingKey) == false)   // if not Rank Key exist Create it
        			{
        			  RankingMap.put(RankingKey,new ArrayList<String>()); // Create a Map item for this Key, and map the key to a new String ArrayList 
        			}
        			
        			//Now A ranking Key exist for this Rank , lets get the Array list that holds family names in this rank
        			List<String> TmpFamilyNameArrayList = RankingMap.get(RankingKey); 
        			
        			TmpFamilyNameArrayList.add(aLine); //Add this Familyname Line under this Ranking Key
    			}
   //===================================================================================================================
    		} //End if 

    	} //End for loop

    	String FinalHtml = HTML_HEADER + HTML_string + HTML_CLICK_HEADER_END + HTML_FOOTER;
		Log.d("看HTML",FinalHtml);
    	OpenWebPage(FinalHtml ,"Surname List",RankingMap);
    	//System.out.println("In am here");
    }

    
} //end class
