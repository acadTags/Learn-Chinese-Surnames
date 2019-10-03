package com.example.chinesesurnames;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.spreada.utils.chinese.ZHConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WikiPage extends Activity {
    private String TAG = "看这里";
	private WebView webView;
	private Button buttonNextPage;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wiki_page);

        buttonNextPage = (Button) findViewById(R.id.buttonNextPage);
        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("看哈哈","ok");
                Bundle extras = getIntent().getExtras();
                String Hanzhi = "";
                String PageTitle = "";
                if (extras != null) {
                    PageTitle = extras.getString("PAGE_TITLE");
                    String [] arr = PageTitle.split(" ");
                    Hanzhi = arr[0];
                }
                if (Hanzhi.length() == 1) {
                    generateWorkInfoDetailsHTML(Hanzhi.charAt(0));
                }else if (Hanzhi.length() > 1){
                    generateWorkInfoDetailsHTML(Hanzhi);
                }
            }
        });

// in onCreate

		//********************* Here Prepare the WebView *********
		webView = (WebView) findViewById(R.id.webView1); // Find in Resource and Assign to variable
		webView.getSettings().setJavaScriptEnabled(true); //allow it to suuport java scripts
		webView.getSettings().setLoadsImagesAutomatically(true);
		//webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.clearCache(true);

	    webView.setScrollBarStyle(webView.SCROLLBARS_INSIDE_OVERLAY);

		//=====================[ HERE GET THE URL PASSED FROM First Page(activity)====================================
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String PageTitle = extras.getString("PAGE_TITLE");
			if (PageTitle != null) setTitle(PageTitle);
			
			String UrlLink = extras.getString("LINK_CLICKED");
			String rawHtml = extras.getString("RAW_HTML");
			
			if (rawHtml.length() > 5) 
			{
                //Log.d("看哟",rawHtml);
                //Toast.makeText(getApplicationContext(), "loading calligraphy image would take a few seconds", Toast.LENGTH_LONG).show();
                webView.loadDataWithBaseURL(null,rawHtml, "text/html", "UTF-8","about:blank");
                //webView.loadData(rawHtml, "text/html; charset=utf-8", "UTF-8");
                //Log.d("看结束咯","呵呵");
				return;
			}
			
			if (UrlLink.length() > 5) 
			{
                //Log.d("看哦",UrlLink);
                webView.loadUrl(UrlLink);
			    return;
			}
		}

        //======================USED TO CALL Next Page with Link ================================================
		webView.setWebViewClient(new WebViewClient() {
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url)
		    {
                //Log.d("看这里",url);
				view.loadUrl(url);
				//we have intercepted the desired url call
				return true;
		    } //end of shouldOverrideUrlLoading()

		}); //end of setWebViewClient()
	//======================================================================
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wiki_page, menu);
		return true;
	}

    public void generateWorkInfoDetailsHTML(char HanzhiChar){
        Toast.makeText(getApplicationContext(), "loading early family book records", Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "start getting gifUrl");
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
//        Log.d(TAG, "ending getting gifUrl");
        Log.d(TAG, "start getting gifUrl");
        String gifUrl;;
        //if (CharacterClasses.hasGifUrl(HanzhiChar)){
        //    gifUrl = CharacterClasses.getGifURL(CharacterClasses.simp2trad(HanzhiChar+"").charAt(0));
        //}else{
            //gifUrl = CharacterClasses.getImageArtURL(HanzhiChar + "", "096", "200");
            gifUrl = CharacterClasses.getImageStandardURL(HanzhiChar+"", "200", "8");
        //}
        Log.d(TAG, "ending getting gifUrl");

        //String gifUrl = CharacterClasses.getCalliImageUrlTrad(HanzhiChar);
        //String gifUrl = CharacterClasses.getGifURL(simp2trad(HanzhiChar+"").charAt(0));
        //String gifUrl = CharacterClasses.getImageStandardURL(HanzhiChar + "", "200", "2");
        //String gifUrl = CharacterClasses.getImageArtURL(HanzhiChar + "", "096", "200");

        Log.d(TAG, "start getting shldata");
        //Surname s = new Surname();
        //s.setCharacter(HanzhiChar + "");
        WorkInfo wi = new WorkInfo(s, 3);
        Thread t1 = new Thread(wi);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String [] workInfoArr = s.getWorkInfoArr();
        Log.d(TAG, "ending getting shldata");
        String workInfo = "";
        if (workInfoArr != null) {
            for (String ele : workInfoArr) {
                Log.d(TAG, "generateWorkInfoDetailsHTML: " + ele);
                if (workInfo.equals("")) {
                    workInfo = ele;
                } else {
                    //Log.d(TAG, "generateWorkInfoDetailsHTML: " + ele.substring(0,3));
                    if (ele.length()>=3) {
                        if (android.text.TextUtils.isDigitsOnly(ele.substring(0, 3))) {
                            ele = "<font size=\"2px\">&nbsp;&nbsp;" + ele + "</font>"; // indentation with items.
                        } else {
                            if (ele.substring(ele.length() - 14).equals("Taiwan, Taiwan")) {
                                ele = ele.substring(0, ele.length() - 14) + "Taiwan, China";
                            }
                            ele = "<br>" + ele;
                        }
                    }else{
                        ele = "<br>" + ele;
                    }
                    workInfo = workInfo + "<br>" + ele;

                }
            }
        }

        String theHtml = LoadTextFilefromResource2String(R.raw.workinfoview);

        theHtml = theHtml.replaceAll("_GIFURL_", gifUrl);
        theHtml = theHtml.replaceAll("_FAMILY_NAME_", Character.toString(HanzhiChar) );
        if (!workInfo.equals("")) {
            theHtml = theHtml.replaceAll("_FAMILY_BOOKS_", workInfo);
        }else{
            theHtml = theHtml.replaceAll("_FAMILY_BOOKS_", "No family book records found in any libraries.");
        }

        Intent intent = new Intent(getApplicationContext(), WorkInfoPage.class);
        intent.putExtra("PAGE_TITLE",  Character.toString(HanzhiChar) + " Family Name");
        intent.putExtra("LINK_CLICKED", "");
        intent.putExtra("RAW_HTML", theHtml); // here put url in the Intent Data list so it can be retrieved in the called activity
        //Toast.makeText(getApplicationContext(), "In WebClient3", Toast.LENGTH_LONG).show();
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "loading calligraphy image may take a few seconds more", Toast.LENGTH_LONG).show();

    }

    public void generateWorkInfoDetailsHTML(String Hanzhi){
        Toast.makeText(getApplicationContext(), "loading early family book records", Toast.LENGTH_SHORT).show();
        //String gifUrl = CharacterClasses.getGifURL(simp2trad(HanzhiChar+"").charAt(0));
        String gifUrl = CharacterClasses.getImageStandardURL(Hanzhi, "200", "8");
        //String gifUrl = CharacterClasses.getImageArtURL(Hanzhi, "096", "200"); // this will show traditional chinese as well.

        Surname s = new Surname();
        s.setCharacter(Hanzhi + "");
        WorkInfo wi = new WorkInfo(s, 3);
        Thread t1 = new Thread(wi);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String [] workInfoArr = s.getWorkInfoArr();
        String workInfo = "";
        if (workInfoArr != null) {
            for (String ele : workInfoArr) {
                Log.d(TAG, "generateWorkInfoDetailsHTML: " + ele);
                if (workInfo.equals("")) {
                    workInfo = ele;
                } else {
                    //Log.d(TAG, "generateWorkInfoDetailsHTML: " + ele.substring(0,3));
                    if (android.text.TextUtils.isDigitsOnly(ele.substring(0, 3))) {
                        ele = "<font size=\"2px\">&nbsp;&nbsp;" + ele + "</font>"; // indentation with items.
                    } else {
                        //Log.d(TAG, "generateWorkInfoDetailsHTML: " + ele.substring(ele.length() - 14));
                        if (ele.substring(ele.length() - 14).equals("Taiwan, Taiwan")){
                            ele = ele.substring(0,ele.length() - 14) + "Taiwan, China";
                        }
                        ele = "<br>" + ele;
                    }
                    workInfo = workInfo + "<br>" + ele;
                }
            }
        }

        String theHtml = LoadTextFilefromResource2String(R.raw.workinfoview);

        theHtml = theHtml.replaceAll("_GIFURL_", gifUrl);
        theHtml = theHtml.replaceAll("_FAMILY_NAME_", Hanzhi);
        if (!workInfo.equals("")) {
            theHtml = theHtml.replaceAll("_FAMILY_BOOKS_", workInfo);
        }else{
            theHtml = theHtml.replaceAll("_FAMILY_BOOKS_", "No family book records found in any libraries.");
        }

        Intent intent = new Intent(getApplicationContext(), WorkInfoPage.class);
        intent.putExtra("PAGE_TITLE",  Hanzhi + " Family Name");
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

    class GifUrl implements Runnable{
        private Surname s;

        GifUrl(Surname s) { this.s = s; }

        public void run(){
            String gifUrl = CharacterClasses.getCalliImageUrlTrad(s.getCharacter().charAt(0));
            s.setGifUrl(gifUrl);
        }
    }

    class WorkInfo implements Runnable{
        private Surname s;
        private int num;

        WorkInfo(Surname s, int num){
            this.s = s;
            this.num = num;
        }

        public void run(){
            String [] WorkInfo = ShlDataClasses.getInfoAboutWorks(ShlDataClasses.getEarliestWorkURLs(s.getCharacter(), num));
            s.setWorkInfo(WorkInfo);
        }
    }
}
