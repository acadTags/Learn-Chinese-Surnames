package com.example.chinesesurnames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class SurnameList extends Activity {
	private WebView webView;
    //private Button button;

	private String AtoZ_Html, RankingHTML=null;
	private Map<String, List<String>> RankingMap;
	public final String HTML_HEADER = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'> <html xmlns='http://www.w3.org/1999/xhtml'> <head>     <meta http-equiv='content-type' content='text/html; charset=utf-8' />     <title></title> <style type='text/css' media='Screen'> #navigation ul {   list-style-type: none;   padding: 0;   margin: 0;   width: 140px; } #navigation a {   text-decoration: none;   display: block;   padding: 3px 12px 3px 8px;   background-color: #D2FFFF;   color: #000;   border: 1px solid #ddd;font-size:18pt;font-weight:bold; } #navigation a:active {   padding: 2px 13px 4px 7px;   background-color: #444;   color: #eee;   border: 1px solid #333; }  #navigation li li a {   text-decoration: none;   padding: 3px 3px 3px 17px; white-space:nowrap;  background-color: #F3F3F3; font-size:12pt;  color: #111111;width: 100%; } #navigation li li a:active {   padding: 2px 4px 4px 16px;   background-color: #888;   color: #000; } </style> <script type='text/javascript'> function swap(targetId){   if (document.getElementById){ target = document.getElementById(targetId); if (target.style.display == 'none'){     target.style.display = ''; } else{     target.style.display = 'none'; } } } </script> </head> <body>     <div id='navigation'> <ul> <!--HEADER SECTION ABOVE --> ";
	public final String HTML_FOOTER = "</ul></div> </body> </html>";
	public final String HTML_CLICK_HEADER = " <li><a href='#' onclick=swap('CLICK_HDR_ID');return false;>_FIRST_CHAR_</a><ul id='CLICK_HDR_ID' style='display: none;'>";
	public final String HTML_CLICK_HEADER_END = " </ul></li>";
	public final String HTML_ITEM_LINE = " <li><a href='_THEURL_' class='namelink'>_THE_NAME_</a></li>";	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("看这里","haha5");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surname_list);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

	//********************* Here Prepare the WebView *********
	webView = (WebView) findViewById(R.id.webView1); // Find in Resource and Assign to variable
	webView.getSettings().setJavaScriptEnabled(true); //allow it to suuport java scripts
	webView.getSettings().setDomStorageEnabled(true);
	webView.getSettings().setLoadsImagesAutomatically(true);
	webView.getSettings().setBuiltInZoomControls(true);
	webView.getSettings().setSupportZoom(true);
	webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
	webView.getSettings().setAppCacheEnabled(true);
	webView.getSettings().setDefaultTextEncodingName("utf-8");
	webView.clearCache(true);
	webView.getSettings().setBlockNetworkImage(false);//不阻塞网络图片
	//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
		//允许混合（http，https）
		//websettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	//}
	//webView.setWebViewClient(new WebViewClient() {
	//	@Override
	//	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
	//		handler.proceed();//接受所有网站的证书
	//	}
	//});
webView.setScrollBarStyle(webView.SCROLLBARS_INSIDE_OVERLAY);
	//=====================[ HERE GET THE URL PASSED FROM First Page(activity)====================================
	Bundle extras = getIntent().getExtras();
	Log.d("看这里", "onCreate: haha");
	if (extras != null) {
		String PageTile = extras.getString("PAGE_TITLE");
		if (PageTile != null) setTitle(PageTile);
		
		String value = extras.getString("LINK_CLICKED");
	    if (value.equals(""))
	    {
	    	AtoZ_Html = extras.getString("RAW_HTML");
            Log.d("看这里atoz",AtoZ_Html);
            webView.loadData(AtoZ_Html, "text/html; charset=utf-8", "UTF-8");

	    	RankingMap = (HashMap<String, List<String>>) getIntent().getSerializableExtra("RankingMap");
            Log.d("看这里","haha");
            //webView.loadUrl(value);
	    }
	    else
	    {
            Log.d("看这里","haha2else");
            // never goes to here.
            value = extras.getString("LINK_CLICKED"); // Get the URL Sent from Calling Activity
            webView.loadUrl(value); //Call the Local WebView Here to go to that Link
            Log.d("看这里","haha3");
	    }
	//}else{
        //Log.d("看这里","haha2");
    }
//==========================================================================

//======================USED TO CALL Next Page with Link ================================================	    
	webView.setWebViewClient(new WebViewClient() {
//        @Override
//		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//			Log.d("看这里url","[" +url.length()+ "]" + url);
//			if (url.length()>300){
//				return super.shouldInterceptRequest(view, url);
//			}
//			Log.d("看这里url","[" +url.length()+ "]" + url);
//			//we have intercepted the desired url call,
//			//Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
//			//The Call sent url in format  HANZHI~URL, we split
//			if (url.substring(0,4).equals("data")){
//				url = url.substring(url.indexOf("type='text/") + 11);
//				String[] StrgArr = url.split("~");
//				//here we Intercept the call to the Default Browser
//				if (StrgArr.length == 3) {
//					generateSurnameDetailsHTML(URLDecode(StrgArr[1]).charAt(0), StrgArr[2], StrgArr[0]);
//				}else{
//					generateSurnameDetailsHTML(URLDecode(StrgArr[0]).charAt(0), StrgArr[1]);
//				}
//			}else{
//				String[] StrgArr = url.split("~");
//				//here we Intercept the call to the Default Browser
//				if (StrgArr.length == 3) {
//					generateSurnameDetailsHTML(URLDecode(StrgArr[1]).charAt(0), StrgArr[2], StrgArr[0]);
//				}else{
//					generateSurnameDetailsHTML(URLDecode(StrgArr[0]).charAt(0), StrgArr[1]);
//				}
//			}
//            return super.shouldInterceptRequest(view, url);
//		}

		public void onPageFinished(WebView view, String url) {
			Log.i("看看", "Finished loading URL: " +url);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			return super.shouldInterceptRequest(view, request);
		}

		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.e("看看", "Error: " + description);
			Toast.makeText(getApplicationContext(), "Page Load Error" + description, Toast.LENGTH_SHORT).show();

		}

	    @Override
	    public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request)
	    {
            //Toast.makeText(getApplicationContext(), "loading basic infomation", Toast.LENGTH_SHORT).show();
			//url = return super.shouldOverrideUrlLoading(view, url);
			Log.d("看这里","shouldOverrideUrlLoading");
			String url = request.getUrl().toString();
            Log.d("看这里url",url);
			//we have intercepted the desired url call,
	    	//Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
	    	//The Call sent url in format  HANZHI~URL, we split
                url = url.substring(7);
                String[] StrgArr = url.split("~");
                Log.d("看看数量","" + StrgArr.length);
				//here we Intercept the call to the Default Browser
                if (StrgArr.length == 3) {
                    String Hanzhi = "";
                    //char charHanzhi = 'x';
                    Log.d("看看", "shouldOverrideUrlLoading: " + StrgArr[2].length() % 5);
                    if (StrgArr[2].length() % 5 == 0) {
                        for (int i=1; i<= StrgArr[2].length()/5; i++){
                            // i is the index of characters in a surname.
							char charHanzhi = (char) Integer.parseInt(StrgArr[2].substring((i-1)*5, i*5).substring(1), 16);
                            Hanzhi = Hanzhi + charHanzhi;
                        }
                        Log.d("看看", "shouldOverrideUrlLoading: " + Hanzhi);
                        Toast.makeText(view.getContext(), "loading surname info: " + Hanzhi , Toast.LENGTH_SHORT).show();
                    }
					if (!StrgArr[0].equals("/#")) {
						Log.d("看这里", "shouldOverrideUrlLoading: " + StrgArr[0]);
						if (Hanzhi.length() == 1) {
							generateSurnameDetailsHTML(Hanzhi.charAt(0), "http://" + StrgArr[0], StrgArr[1]);
						}else if (Hanzhi.length() > 1){
							// handle multiple character surnames.
							generateSurnameDetailsHTML(Hanzhi, "http://" + StrgArr[0], StrgArr[1]);
						}
					}else{
                        // no wikipedia url.
						Log.d("看这里", "shouldOverrideUrlLoading: " + StrgArr[0]);
						if (Hanzhi.length() == 1) {
							generateSurnameDetailsHTML(Hanzhi.charAt(0), StrgArr[0].substring(1), StrgArr[1]);
						}else if (Hanzhi.length() > 1){
							// handle multiple character surnames.
							generateSurnameDetailsHTML(Hanzhi, StrgArr[0].substring(1), StrgArr[1]);
						}
					}
//                }else{
//                    char charHanzhi = (char) Integer.parseInt(StrgArr[1].substring(1,StrgArr[1].length()-1), 16);
//                    generateSurnameDetailsHTML(charHanzhi, StrgArr[0]);
                }
            //}
            return true;

	    } //end of shouldOverrideUrlLoading()
	}); //end of setWebViewClient()

        webView.setWebChromeClient(new WebChromeClient(){

        });
//======================================================================
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.surname_list, menu);
        //Log.d("看这里","haha2");
		return true;
	}

    private void generateSurnameDetailsHTML(char HanzhiChar, String WikiPediaURL, String Pinyin)
    {
		Toast.makeText(getApplicationContext(), "loading basic infomation", Toast.LENGTH_SHORT).show();
//		Log.d("看这里", "start getting gifUrl");
        Surname s = new Surname();
        s.setCharacter(HanzhiChar+"");
//        GifUrl gu = new GifUrl(s);
//        Thread t0 = new Thread(gu);
//        t0.start();
//        try {
//            t0.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        String gifUrl = s.getGifUrl();
//		Log.d("看这里", "ending getting gifUrl");
        String gifUrl;
        //if (CharacterClasses.hasGifUrl(HanzhiChar)){
        //    gifUrl = CharacterClasses.getGifURL(HanzhiChar);
        //}else{
            gifUrl = CharacterClasses.getImageStandardURL(HanzhiChar + "", "200", "2");
        //}
        //String gifUrl = CharacterClasses.getGifURL(HanzhiChar);
	    //String gifUrl = CharacterClasses.getImageStandardURL(HanzhiChar + "", "200", "2");
	    //String gifUrl = CharacterClasses.getImageArtURL(HanzhiChar + "", "060", "200");
	    String wikiUrl = CharacterClasses.getWiktionaryURL(HanzhiChar);
        Log.d("看这里", "start getting shldata");
    	//Surname s = new Surname();
	    //s.setCharacter(HanzhiChar+"");
	    NumOfBooks nb = new NumOfBooks(s);
	    Thread t1 = new Thread(nb);
	    t1.start();
    	try {
	    	t1.join();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
    	}
	    String NumOfBooks = s.getNumOfBooks();
        if (NumOfBooks.equals("no result")){
            NumOfBooks = "no";
        }

    	NumOfOccurranceInNames noin = new NumOfOccurranceInNames(s);
	    Thread t2 = new Thread(noin);
	    t2.start();
	    try {
		    t2.join();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
    	}
	    String NumOfOccurence = s.getNumOfOccurranceInNames();
        if (NumOfOccurence.equals("no result")){
            NumOfOccurence = "no";
        }
        Log.d("看这里", "ending getting shldata");
    	String theHtml = LoadTextFilefromResource2String(R.raw.familyview);
		Log.d("看看theHtml",theHtml);
    	theHtml = theHtml.replaceAll("_GIFURL_", gifUrl);
	    theHtml = theHtml.replaceAll("_WIKI_", wikiUrl);
	    theHtml = theHtml.replaceAll("_PEDIA_", WikiPediaURL);
        theHtml = theHtml.replaceAll("_FAMILY_NAME_", Character.toString(HanzhiChar) + "  ( " +  Pinyin + " )" );
        theHtml = theHtml.replaceAll("_NUM_BOOKS_", NumOfBooks);
        theHtml = theHtml.replaceAll("_NUM_OCCUR_", NumOfOccurence);

		Log.d("看汉字图片",theHtml);
     	Intent intent = new Intent(getApplicationContext(), WikiPage.class);
 	    intent.putExtra("PAGE_TITLE",  Character.toString(HanzhiChar) + " Family Name");
 	    intent.putExtra("LINK_CLICKED", "");
        intent.putExtra("RAW_HTML", theHtml); // here put url in the Intent Data list so it can be retrieved in the called activity
        //Toast.makeText(getApplicationContext(), "In WebClient3", Toast.LENGTH_LONG).show();
 		startActivity(intent);
		Log.d("看载入图片",theHtml);
		Toast.makeText(getApplicationContext(), "loading calligraphy image may take a few seconds more", Toast.LENGTH_LONG).show();
	}

	// for surname with multiple characters.
	private void generateSurnameDetailsHTML(String MultipleHanZhi, String WikiPediaURL, String Pinyin)
	{
		Toast.makeText(getApplicationContext(), "loading basic infomation", Toast.LENGTH_SHORT).show();
		//String gifUrl = CharacterClasses.getGifURL(HanzhiChar);
		String gifUrl = CharacterClasses.getImageStandardURL(MultipleHanZhi, "200", "2"); // use this for image.
		//String gifUrl = CharacterClasses.getImageArtURL(MultipleHanZhi, "060", "200"); // use this for image.
		String wikiUrl = "#"; // no wiktionary url for multiple character surnames.

		Surname s = new Surname();
		s.setCharacter(MultipleHanZhi);
		NumOfBooks nb = new NumOfBooks(s);
		Thread t1 = new Thread(nb);
		t1.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String NumOfBooks = s.getNumOfBooks();
		if (NumOfBooks.equals("no result")){
			NumOfBooks = "no";
		}

		NumOfOccurranceInNames noin = new NumOfOccurranceInNames(s);
		Thread t2 = new Thread(noin);
		t2.start();
		try {
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String NumOfOccurence = s.getNumOfOccurranceInNames();
		if (NumOfOccurence.equals("no result")){
			NumOfOccurence = "no";
		}

		String theHtml = LoadTextFilefromResource2String(R.raw.familyview);
		Log.d("看看theHtml",theHtml);
		theHtml = theHtml.replaceAll("_GIFURL_", gifUrl);
		theHtml = theHtml.replaceAll("_WIKI_", wikiUrl);
		theHtml = theHtml.replaceAll("_PEDIA_", WikiPediaURL);
		theHtml = theHtml.replaceAll("_FAMILY_NAME_", MultipleHanZhi + "  ( " +  Pinyin + " )" );
		theHtml = theHtml.replaceAll("_NUM_BOOKS_", NumOfBooks);
		theHtml = theHtml.replaceAll("_NUM_OCCUR_", NumOfOccurence);

		Log.d("看汉字图片",theHtml);
		Intent intent = new Intent(getApplicationContext(), WikiPage.class);
		intent.putExtra("PAGE_TITLE",  MultipleHanZhi + " Family Name");
		intent.putExtra("LINK_CLICKED", "");
		intent.putExtra("RAW_HTML", theHtml); // here put url in the Intent Data list so it can be retrieved in the called activity
		//Toast.makeText(getApplicationContext(), "In WebClient3", Toast.LENGTH_LONG).show();
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "loading calligraphy image may take a few seconds more", Toast.LENGTH_LONG).show();
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

    public void ShowRankingList(View v)
    {
        //Log.d("看这里","haha5");
    	webView.loadUrl("about:blank"); //clear the webView
	    //if no HTML exist for the Ranking ..then make it
	    if (RankingHTML == null )
    	{
	    	//Toast.makeText(getApplicationContext(), "Rank NULL", Toast.LENGTH_LONG).show();
		    RankingHTML = processRankMaptoHTML(RankingMap);
    	}
        webView.loadData(RankingHTML, "text/html; charset=utf-8", "UTF-8");
    }

    public void ShowAlphabeticalList(View v)
    {
        //Log.d("看这里","haha6");
	    webView.loadUrl("about:blank"); //clear the webView
        webView.loadData(AtoZ_Html, "text/html; charset=utf-8", "UTF-8");
    }

    private String processRankMaptoHTML(Map<String, List<String>> RankingMap)
    {
        String dHtml = null;
        char FirstChar ='$';
        String aLine = null;
	    String HTML_string = "";
	    String TmpStrg,RankingKey;
	    String[] StrgLineArr = null;
	
    	Object[] MapKeyList = RankingMap.keySet().toArray(); //Turn the Rank key set to array
	
    	Arrays.sort(MapKeyList); //Sort it
	
        for (Object eachKeyValue : MapKeyList)
        {
            String key = (String) eachKeyValue;
            String HeaderLabel = key.trim();
            if ( key.contains("001-")) HeaderLabel = key.replaceAll("001-", "1-" );
            if ( key.contains("051-")) HeaderLabel = key.replaceAll("051-", "51-" );

            //Get the family names List maped to that Ranking Key
            List<String> SurnameUrlList = RankingMap.get(key);
		     //Make Click Header--- Using the Key as the Label
		    if(HTML_string != null) HTML_string = HTML_string + HTML_CLICK_HEADER_END;
		    TmpStrg = HTML_CLICK_HEADER;
		    TmpStrg = TmpStrg.replaceAll("_FIRST_CHAR_", HeaderLabel );
		    HTML_string = HTML_string +  TmpStrg.replaceAll("CLICK_HDR_ID", key +"_ID");
            //Generate the HTML for it
            String [] range = HeaderLabel.split("-");
            if (range.length == 2) {
                for (int i = Integer.parseInt(range[0]); i <= Integer.parseInt(range[1]); i++) {
                    for (int x = 0; x < SurnameUrlList.size(); ++x) {
                        aLine = SurnameUrlList.get(x);

                        // Now split the PINYIN~HANZHI~URL~RANKING string to array
                        //zhan~战~http://en.wikipedia.org/wiki/Zhan_(surname)~351-400X
                        StrgLineArr = aLine.split("~");
                        //if (StrgLineArr.length == 4)
                        if (StrgLineArr.length == 5) {
                            if (StrgLineArr[4].equals(i + "")) {
                                TmpStrg = HTML_ITEM_LINE;
                                TmpStrg = TmpStrg.replaceAll("_THE_NAME_", StrgLineArr[0] + " " + StrgLineArr[1]);
                                //if (StrgLineArr[2].length() != 1) {
                                //    HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", "http://" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]) + "~" + StrgLineArr[2].substring(7));
                                //} else {
                                //    // no wikipedia url.
                                //    HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", "http://" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]) + "~" + StrgLineArr[2]);
                                //}
                                HTML_string = HTML_string + TmpStrg.replaceAll("_THEURL_", StrgLineArr[2] + "~" + StrgLineArr[0] + "~" + CharacterClasses.toUnicode(StrgLineArr[1]));
                            } //End if
                        } //End if

                    } //End for loop
                } //End for loop
            } // End if

        }  //End eachKeyValue For Loop
    
        // now form the total HTML

    	String FinalHtml = HTML_HEADER + HTML_string + HTML_CLICK_HEADER_END + HTML_FOOTER;
    	return FinalHtml;
    }

	private static String URLDecode(String str){
		return URLDecoder.decode(str);
	}

    class GifUrl implements Runnable{
        private Surname s;

        GifUrl(Surname s) { this.s = s; }

        public void run(){
            String gifUrl = CharacterClasses.getCalliImageUrlSimp(s.getCharacter().charAt(0));
            s.setGifUrl(gifUrl);
        }
    }

	class NumOfBooks implements Runnable{
		private Surname s;

		NumOfBooks(Surname s){
			this.s = s;
		}

		public void run(){
			String numOfBooks = ShlDataClasses.getNumofBooks(s.getCharacter());
			s.setNumOfBooks(numOfBooks);
		}
	}

	class NumOfOccurranceInNames implements Runnable{
		private Surname s;

		NumOfOccurranceInNames(Surname s){
			this.s = s;
		}

		public void run(){
			String numOfOccurranceInNames = ShlDataClasses.getNumofOccurrenceInNames(s.getCharacter());
			s.setNumOfOccurranceInNames(numOfOccurranceInNames);
		}
	}
}
