package com.example.chinesesurnames;

import android.app.Activity;
import android.net.Uri;

import com.spreada.utils.chinese.ZHConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CharacterClasses {
    
    //get the gif link to download/display the stroke and caligraphy of a chinese character.
    //the web service is from https://dictionary.writtenchinese.com/
    //The word dictionary of this website is based on CC-CEDICT. 
    //CC-CEDICT is a continuation of the CEDICT project started by Paul Denisowski in 1997 
    //with the aim to provide a complete downloadable Chinese to English dictionary 
    //with pronunciation in pinyin for the Chinese characters. 
    //This website allows you to easily add new entries or correct existing entries in CC-CEDICT. 
    //Submitted entries will be checked and processed frequently and released for download in CEDICT format on this page.
    public static String getGifURL(char Character){
        return "https://dictionary.writtenchinese.com/giffile.action?&localfile=true&fileName=" + URLEncoder.encode(URLEncoder.encode(Character + "")) + ".gif";
    }

    public static String getImageStandardURL(String character, String size, String font_style_code){
        //return "http://pic16.nipic.com/20111006/6239936_092702973000_2.jpg";
        return "http://www.chinesetools.eu/names/gen_boutons.php"
                + "?text=" + URLEncoder.encode(buildHtmlEntityCode(character))
                + "&s=" + size
                + "&police=" + font_style_code + "&dispo=1";
        //return Uri.parse("android.resource://com.example.chinesesurnames/" + R.drawable.capture).toString();
    }

    public static String getImageArtURL(String character, String font_style_code, String size){
        return "http://www.2d-code.cn/ysz/api.php?key="
                + "c_4711NjVeWWKaItvNzZMsD4rU1vt3m8sLF21L8093Q&transparent=1&text=" +
                URLEncoder.encode(character) + "&fontid=" + font_style_code + "&fontsize=" + size;
    }

    @SuppressWarnings("deprecation")
	public static String getWiktionaryURL(char Character){
        return "https://en.m.wiktionary.org/wiki/" + URLEncoder.encode(Character + "");
    }

    public static String toUnicode(String character){
        char[] c = character.toCharArray();
        String unicode = "";
        for (char tmp: c){
            unicode = unicode + "\\u" + Integer.toHexString(tmp);
        }
        return unicode;
    }

    public static String getCalliImageUrlSimp(char Character){
        if (exists(getGifURL(Character))){
            return getGifURL(Character);
        }else{
            return getImageStandardURL(Character + "", "200", "2");
        }
    }

    public static String getCalliImageUrlTrad(char Character){
        char tradCharacter = simp2trad(Character + "").charAt(0);
        if (exists(getGifURL(tradCharacter))){
            return getGifURL(tradCharacter);
        }else{
            return getImageArtURL(Character + "", "096", "200");
        }
    }

    public static boolean hasGifUrl(char Character){
        String GifCharStrs = "龚覃翟靳祁滕缪娄瞿佟臧闵邬卞栾刁甄佘邝胥鄢谌冼蔺仝郜阚扈芮亓邰逯茹亢嵇檀昝";
        return GifCharStrs.indexOf(Character) == -1;
    }

    public static String simp2trad(String Characters){
        ZHConverter converter = ZHConverter.getInstance(ZHConverter.TRADITIONAL);
        String traditionalStr = converter.convert(Characters);
        return traditionalStr;
    }

    private static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need

            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            //con.setInstanceFollowRedirects(false);
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // code by shankarpshetty http://shankarpshetty.blogspot.com/2009/11/java-function-to-convert-string-to-html.html
    private static String buildHtmlEntityCode(String input) {
        StringBuffer output = new StringBuffer(input.length() * 2);

        int len = input.length();
        int code,code1,code2,code3,code4;
        char ch;

        for( int i=0; i<len; ) {
            code1 = input.codePointAt(i);
            if( code1 >> 3 == 30 ){
                code2 = input.codePointAt(i + 1);
                code3 = input.codePointAt(i + 2);
                code4 = input.codePointAt(i + 3);
                code = ((code1 & 7) << 18) | ((code2 & 63) << 12) | ((code3 & 63) << 6) | ( code4 & 63 );
                i += 4;
                output.append("&#" + code + ";");
            }
            else if( code1 >> 4 == 14 ){
                code2 = input.codePointAt(i + 1);
                code3 = input.codePointAt(i + 2);
                code = ((code1 & 15) << 12) | ((code2 & 63) << 6) | ( code3 & 63 );
                i += 3;
                output.append("&#" + code + ";");
            }
            else if( code1 >> 5 == 6 ){
                code2 = input.codePointAt(i + 1);
                code = ((code1 & 31) << 6) | ( code2 & 63 );
                i += 2;
                output.append("&#" + code + ";");
            }
            else {
                code = code1;
                i += 1;

                ch = (char)code;
                if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
                    output.append(ch);
                }
                else {
                    output.append("&#" + code + ";");
                }
            }
        }

        return output.toString();
    }
}
