package com.example.chinesesurnames;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

/**
 * Created by hang dong on 14/05/2016.
 */
public class AboutPage extends Activity{
    private TextView myText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        myText = (TextView) findViewById(R.id.textViewAboutPage);
        myText.setMovementMethod(new ScrollingMovementMethod());
        myText.setText("Learn Chinese Surnames is an app designed for worldwide chinese learners to get familiar with chinese surnames and characters.\n" +
                "\n" +
                "The app is made during April 1st 2016 to May 16th 2016 as a submission to the Shanghai Library Open Data App Dev Competition.\n" +
                "\n" +
                "Authors are from Xi'an Jiaotong-Liverpool University.\n" +
                "\t\tHang Dong (project leader and coder)\n" +
                "\t\tIlesanmi Olade (coder)\n" +
                "\t\tKunquan Zhong (start page & icon design)\n" +
                "\t\n" +
                "Acknowledgement\n" +
                "\t\tto Shanghai Library.\n" +
                "\t\tto WrittenChinese.Com.\n" +
                "\t\tto ChineseTools.eu.\n" +
                "\t\tto Wikipedia and Wiktionary.\n" +
                "\t\tto DBpedia.\n" +
                "\t\tto GeoNames.\n" +
                "\t\tto Undergraduate student Yuxin Fu.\n" +
                "\t\tto Colleague Wei Wang.\n" +
                "\t\n" +
                "For my mother.\n" +
                "\n" +
                "First Version: May 15 2016.\n" +
                "Last Edit: Oct 03 2019.\n" +
                "Contact: hang.dong@xjtlu.edu.cn");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wiki_page, menu);
        return true;
    }
}
