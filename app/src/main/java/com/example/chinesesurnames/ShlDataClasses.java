package com.example.chinesesurnames;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 *
 * @author hang dong
 */
public class ShlDataClasses {

    // get the ranked surnames by pinyin from an English letter.
    // input is an English letter.
    // output as character + space + pinyin.
    // NOTE: return null when JSONException caught.
    public static String[] getRankedSurnames(String letter){
        String query = "select distinct ?suren ?surchs\n" +
                "where{\n" +
                "graph <http://gen.library.sh.cn/graph/baseinfo> {\n" +
                "?n bf:label ?suren. \n" +
                "filter (contains(str(?n),\"http://data.library.sh.cn/authority/familyname/\")).\n" +
                "filter(lang(?suren) = \"en\" && regex(?suren, \"^" + letter + "\")).\n" +
                "?n bf:label ?surchs.\n" +
                "filter(lang(?surchs) = \"chs\").}\n" +
                "}\n" +
                "order by asc(UCASE(str(?suren)))";
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray jsonArr = json.getJSONArray("bindings");
            String[] arr = new String[jsonArr.length()];
            for (int i = 0; i < jsonArr.length(); i++) {
                arr[i] = jsonArr.getJSONObject(i).getJSONObject("surchs").getString("value") +
                        " " + jsonArr.getJSONObject(i).getJSONObject("suren").getString("value");
            }
            return arr;
        }catch(JSONException jsone){
            //throw new RuntimeException(jsone);
            return null;
        }
    }
    
    // get the list of surname counts.
    // the output is an array containing 26 lines, each line corresponds an English letter from a to z,
    // showing the number of surnames which has pinyin starting from this letter.
    public static String[] getListOfSurnameCounts(){
        String[] counts = new String[26];
        for (int i=1;i<=26;i++){
            counts[i-1] = getRankedSurnameCounts((char)(i + 96) + "");
        }
        return counts;
    }
    
    // get the count of surnames from an English letter.
    // the output is a numeric String.
    // NOTE: return "no result" when JSONException is caught.
    private static String getRankedSurnameCounts(String letter){
        String query = "select count(?suren) as ?count\n" +
                "where{\n" +
                "graph <http://gen.library.sh.cn/graph/baseinfo> {\n" +
                "?n bf:label ?suren. \n" +
                "filter (contains(str(?n),\"http://data.library.sh.cn/authority/familyname/\")).\n" +
                "filter(lang(?suren) = \"en\" && regex(?suren, \"^" + letter + "\")).}\n" +
                "}";
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray jsonArr = json.getJSONArray("bindings");
            String count = jsonArr.getJSONObject(0).getJSONObject("count").getString("value");
            return count;
        }catch (JSONException jsone){
            return "no result";
        }
    }
    
    // get the URL for the Restful Service of shl sparql endpoint.
    // the input is a query written in SPARQL. 
    private static String getURL(String query){
        return "http://data.library.sh.cn:8890/sparql?query=" + URLEncoder.encode(query) + "&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on";
    }
    
    // get the URL of a surname in shl data.
    // it will return "no result" if the surname is not included in shl data.
    // NOTE: return "no result" when JSONException is caught.
    private static String getSurnameURL(String surname){
        String query = "select distinct ?url\n" +
            "where { graph <http://gen.library.sh.cn/graph/baseinfo> {\n" +
            "?url bf:label ?surchs.\n" +
            "filter(str(?surchs) = \"" + surname + "\" && contains(str(?url),\"http://data.library.sh.cn/authority/familyname/\")).}\n" +
            "}";
        Log.d("看看query-surname",query);
        JSONObject json = null;
        try {
            String jsonTxt = getJsonFromURL(getURL(query));
            Log.d("看这里",jsonTxt+getURL(query));
            json = new JSONObject(jsonTxt);
            Log.d("看这里","gaga1");
            json = json.getJSONObject("results");
            Log.d("看这里","gaga1");
            JSONArray arr = json.getJSONArray("bindings");
            Log.d("看这里","gaga1");
            if (arr.length() > 0) {
                return arr.getJSONObject(0).getJSONObject("url").getString("value");
            } else {
                Log.d("看这里","gaga1");
                return "no result";
            }
        }catch(JSONException jsone){
            Log.d("看这里","gaga2");
            return "no result";
        }
    }
    
    // get the number of books of a surname in shl data.
    // it will return "no result" if the surname is not included in shl data.
    // NOTE: return "no result" when JSONException is caught.
    public static String getNumofBooks(String surname){
        String url = getSurnameURL(surname);
        if (url.equals("no result")){
            return "no result";
        }
        String query = "select distinct (count(?work) as ?count)\n" +
                "where { graph <http://gen.library.sh.cn/graph/work> {\n" +
                "?work bf:subject <" + url + ">.}\n" +
                "}";
        Log.d("看看query",query);
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray arr = json.getJSONArray("bindings");
            if (arr.length() > 0) {
                return arr.getJSONObject(0).getJSONObject("count").getString("value");
            } else {
                return "no result";
            }
        }catch(JSONException jsone){
            return "no result";
        }
    }
    
    // get the number of name occurrence of a surname in shl data.
    // it will return "no result" if the surname is not included in shl data.
    // here occurrence count repetitions as many rather than 1.
    // NOTE: return "no result" when JSONException is caught.
    public static String getNumofOccurrenceInNames(String surname){
        String url = getSurnameURL(surname);
        //Log.d("看看哦", surname + " " + url + " " + getSurnameURL("董"));
        if (url.equals("no result")){
            return "no result";
        }
        String query = "select count (distinct ?p) as ?count\n" +
            "where{ graph <http://gen.library.sh.cn/graph/person> {\n" +
            "?p foaf:familyName <" + url + ">}}";
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray arr = json.getJSONArray("bindings");
            if (arr.length() > 0){
                return arr.getJSONObject(0).getJSONObject("count").getString("value");
            }else{
                return "no result";
            }
        }catch(JSONException jsone){
            return "no result";
        }
    }

    // get the ealiest works from a surname,
    // the number of works is specified in the param number.
    // each surname is repressented as a URL.
    // it will return null if the surname is not included in shl data.
    // NOTE：only can get those works labelled with a temporal value.
    // NOTE: return null when JSONException is caught.
    public static String[] getEarliestWorkURLs(String surname, int number){
        String url = getSurnameURL(surname);
        if (url.equals("no result")){
            return null;
        }
        String query = "select distinct ?work\n" +
                "where{ graph <http://gen.library.sh.cn/graph/work> {\n" +
                "?work bf:subject <" + url + ">.\n" +
                "graph <http://gen.library.sh.cn/graph/instance> {\n" +
                "?instance bf:instanceOf ?work.\n" +
                "?instance shl:temporalValue ?y.\n" +
                "filter (regex(str(?y),\"\\\\d\\\\d\\\\d\\\\d\")).}\n" +
                "}}\n" +
                "order by asc(str(?y))\n" +
                "limit " + number;

        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray arr = json.getJSONArray("bindings");
            String[] works;
            if (arr.length() > 0) {
                works = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    works[i] = arr.getJSONObject(i).getJSONObject("work").getString("value");
                }
            } else {
                return null;
            }
            return works;
        }catch (JSONException jsone){
            return null;
        }
    }

    // get the information about a set of works: including title, place, items for each work,
    // and completed year, engraved organisation name and notes for each item.
    public static String[] getInfoAboutWorks(String[] workURLs){
        if (workURLs == null){
            return null;
        }
        String[] workInfo;
        String[] tempArr = new String[1];
        tempArr[0] = getInfoAboutAWork(workURLs[0]);
        workInfo = tempArr;
        workInfo = concat(workInfo, getInfoAboutItems(workURLs[0]));
        for (int i=1;i<workURLs.length;i++){
            tempArr[0] = getInfoAboutAWork(workURLs[i]);
            workInfo = concat(workInfo, tempArr);
            workInfo = concat(workInfo, getInfoAboutItems(workURLs[i]));
        }
        return workInfo;
    }

    // get information about on the work level, including title and place info, 
    // english place info is acquired from GeoNames.
    // the input is a work, represented as a URL.
    // NOTE: return "" when JSONException is caught.
    private static String getInfoAboutAWork(String workURL){
        if (workURL == null){
            return null;
        }
        String query = "select ?title ?countyname ?cityname ?provincename ?countryname\n" +
                "where{ graph <http://gen.library.sh.cn/graph/work> {\n" +
                "<" + workURL + "> dc:title ?title.\n" +
                "<" + workURL + "> shl:place ?place.\n" +
                "filter (lang(?title) = \"chs\")}\n" +
                "graph <http://gen.library.sh.cn/graph/place> {\n" +
                "?place shl:country ?countryname.\n" +
                "filter(lang(?countryname) = \"chs\").\n" +
                "optional {?place shl:province ?provincename.\n" +
                "optional {?place shl:city ?cityname.\n" +
                "optional {?place shl:county ?countyname.}}}}}";
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray arr = json.getJSONArray("bindings");
            json = arr.getJSONObject(0);
            String dcTitle = json.getJSONObject("title").getString("value"); // title defined using dc:title
            String placeName;
            String upperlevelplaceName;
            String placeNameinEnglish;

            if (json.has("countyname")) {
                placeName = json.getJSONObject("countyname").getString("value");
                upperlevelplaceName = json.getJSONObject("provincename").getString("value");
                if (upperlevelplaceName.equals("台湾省")) {
                    placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM2");
                    if (!placeNameinEnglish.equals("")) {
                        String[] temp = placeNameinEnglish.split(",");
                        String placeNameinEnglishNew = "";
                        for (int i = 0; i < temp.length - 1; i++) {
                            placeNameinEnglishNew = placeNameinEnglishNew + temp[i] + ",";
                        }
                        placeNameinEnglishNew = placeNameinEnglishNew + " China";
                    }
                } else {
                    placeNameinEnglish = getPlaceInfoInEnglish(placeName, upperlevelplaceName, "ADM3");
                }
                if (placeNameinEnglish.equals("")) {
                    placeName = json.getJSONObject("cityname").getString("value");
                    placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM2");
                    if (placeNameinEnglish.equals("")) {
                        placeName = json.getJSONObject("provincename").getString("value");
                        if (placeName.equals("台湾省")) {
                            placeName = "台湾";
                            //placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                            placeNameinEnglish = "Taiwan, China";
                        } else {
                            placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM1");
                        }
                        if (placeNameinEnglish.equals("")) {
                            placeName = json.getJSONObject("countryname").getString("value");
                            placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                        }
                    }
                }
            } else if (json.has("cityname")) {
                placeName = json.getJSONObject("cityname").getString("value");
                placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM2");
                if (placeNameinEnglish.equals("")) {
                    placeName = json.getJSONObject("provincename").getString("value");
                    if (placeName.equals("台湾省")) {
                        placeName = "台湾";
                        //placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                        placeNameinEnglish = "Taiwan, China";
                    } else {
                        placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM1");
                    }
                    if (placeNameinEnglish.equals("")) {
                        placeName = json.getJSONObject("countryname").getString("value");
                        placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                    }
                }
            } else if (json.has("provincename")) {
                placeName = json.getJSONObject("provincename").getString("value");
                if (placeName.equals("台湾省")) {
                    placeName = "台湾";
                    //placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                    placeNameinEnglish = "Taiwan, China";
                } else {
                    placeNameinEnglish = getPlaceInfoInEnglish(placeName, "ADM1");
                }
                if (placeNameinEnglish.equals("")) {
                    placeName = json.getJSONObject("countryname").getString("value");
                    placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
                }
            } else {
                placeName = json.getJSONObject("countryname").getString("value");
                placeNameinEnglish = getPlaceInfoInEnglish(placeName, "PCLI");
            }
            //String placeNameinEnglish = getPlaceInfoInEnglish(placeName);
            return dcTitle + " | " + placeNameinEnglish;
        } catch (JSONException jsone){
            return "";
        }
    }
    
    // get the items info from a work, including completing year, engraved organisation and notes for an item. 
    // A work is represented as a URL.
    // the output is a String array since a work can have multiple items and each item has its own information.
    // NOTE: return null when JSONException is caught.
    private static String[] getInfoAboutItems(String workURL){
        if (workURL == null){
            return null;
        }
        String query = "select ?y ?n ?orgname\n" +
                "where{\n" +
                "graph <http://gen.library.sh.cn/graph/instance> {\n" +
                "?instance bf:instanceOf <" + workURL + ">.\n" +
                "?instance shl:temporalValue ?y.\n" +
                "filter(isnumeric(?y)).}\n" +
                "graph <http://gen.library.sh.cn/graph/item> {\n" +
                "?item bf:itemOf ?instance.\n" +
                "optional {?item shl:description ?n.}\n" +
                "optional {?item bf:heldBy ?organisation.\n" +
                "graph <http://gen.library.sh.cn/graph/baseinfo> {\n" +
                "?organisation bf:label ?orgname.\n" +
                "filter(lang(?orgname)=\"en\")}}}}\n" +
                "order by asc(?y)";
        try {
            JSONObject json = new JSONObject(getJsonFromURL(getURL(query)));
            json = json.getJSONObject("results");
            JSONArray arr = json.getJSONArray("bindings");
            String[] itemInfo;
            String notes;
            if (arr.length() > 0) {
                itemInfo = new String[arr.length()];
                for (int j = 0; j < arr.length(); j++) {
                    json = arr.getJSONObject(j);
                    notes = "";
                    if (json.has("orgname")) {
                        notes = json.getJSONObject("orgname").getString("value");
                    }
                    if (json.has("n")) {
                        if (notes.equals("")) {
                            notes = json.getJSONObject("n").getString("value");
                        } else {
                            notes = notes + " " + json.getJSONObject("n").getString("value");
                        }
                    }
                    //itemInfo[j] = "Title: " + json.getJSONObject("title").getString("value") +
                    //        "\nPublication Year: " + json.getJSONObject("y").getString("value") +
                    //        "\nNotes: " + notes.trim();
                    itemInfo[j] = json.getJSONObject("y").getString("value") +
                            " " + notes.trim();
                }
            } else {
                return null;
            }
            return itemInfo;
        }catch (JSONException jsone){
            return null;
        }
    }
    
    // get place information in English from geonames.
    // input: a place name in Chinese
    // output: the same place name in English
    private static String getPlaceInfoInEnglish(String placeName){
        String URL = getGeoNamePlaceURL(placeName);
        try {
            JSONObject json = new JSONObject(getJsonFromURL(URL));
            JSONArray arr = json.getJSONArray("geonames");
            if (arr.length() == 0) {
                return "";
            }
            json = arr.getJSONObject(0);
            String name = json.getString("name");
            if (name.equals("China") || name.equals("Taiwan")) {
                return name;
            } else {
                String province = json.getString("adminName1");
                String country = json.getString("countryName");
                if (json.getString("fcode").equals("ADM1")) {
                    if (name.equals(province)) {
                        return province + ", " + country;
                    } else {
                        return name + ", " + province
                                + ", " + country;
                    }
                } else {
                    return name + ", " + province
                            + ", " + country;
                }
            }
        }catch (JSONException jsone){
            return "";
        }
    }
    
    // for a placeName which its fcode is ADM3 (third-order administrative division),
    // there can be polysemy for solely a placeName, e.g, 歙县,泾县 are the county names in a few provinces.
    // in this case it is important to bring in the upperlevelplaceName.
    private static String getPlaceInfoInEnglish(String placeName, String upperlevelplaceName, String fcode){
        //get the upperlevel english name first, 
        //which should be a province place holding the fcode "ADM1".
        try{
            String URL = getGeoNamePlaceURL(upperlevelplaceName, "ADM1");
            JSONObject jsonupper = new JSONObject(getJsonFromURL(URL));
            JSONArray arr = jsonupper.getJSONArray("geonames");
            if (arr.length() == 0){
                // if the upper level place is not found in the GeoNames,
                // then return an empty String.
                // but I guess this is really unlikely to happen.
                return "";
            }
            jsonupper = arr.getJSONObject(0);
            String nameUpper = jsonupper.getString("name");

            URL = getGeoNamePlaceURL(placeName, fcode);
            JSONObject json = new JSONObject(getJsonFromURL(URL));
            arr = json.getJSONArray("geonames");
            if (arr.length() == 0){
                return "";
            }

            json = arr.getJSONObject(0);
            if (arr.length() > 1){
                for (int i=0;i<arr.length();i++){
                    JSONObject jsonCandidate = arr.getJSONObject(i);
                    if (jsonCandidate.getString("adminName1").equals(nameUpper)){
                        json = jsonCandidate;
                        break;
                    }
                }
            }

            String name = json.getString("name");
            if (name.equals("China") || name.equals("Taiwan")){
                return name;
            }else{
                String province = json.getString("adminName1");
                String country = json.getString("countryName");
                if (json.getString("fcode").equals("ADM1")){
                    if (name.equals(province)){
                        return province + ", " + country;
                    }else{
                        return name + ", " + province 
                            + ", " + country;
                    }
                }else{     
                    return name + ", " + province 
                        + ", " + country;
                }    
            }
        }catch (JSONException je){
            return "";
        }    
    }
    
    private static String getPlaceInfoInEnglish(String placeName, String fcode){
        String URL = getGeoNamePlaceURL(placeName, fcode);
        try {
            JSONObject json = new JSONObject(getJsonFromURL(URL));
            JSONArray arr = json.getJSONArray("geonames");
            if (arr.length() == 0) {
                return "";
            }
            json = arr.getJSONObject(0);
            String name = json.getString("name");
            if (name.equals("China") || name.equals("Taiwan")) {
                return name;
            } else {
                String province = json.getString("adminName1");
                String country = json.getString("countryName");
                if (json.getString("fcode").equals("ADM1")) {
                    if (name.equals(province)) {
                        return province + ", " + country;
                    } else {
                        return name + ", " + province
                                + ", " + country;
                    }
                } else {
                    return name + ", " + province
                            + ", " + country;
                }
            }
        }catch (JSONException jsone){
            return "";
        }
    }
    
    // input placeName in Chinese
    // output Geonames URL to call the Rest API
    private static String getGeoNamePlaceURL(String placeName){
        return "http://api.geonames.org/searchJSON?name_equals=" + URLEncoder.encode(placeName) + "&featureCode=ADM3&featureCode=ADM2&featureCode=ADM1&featureCode=PCLI&country=CN&country=TW&maxRows=10&username=clement116";
    }
    
    // can specify the specific fcode representing the level of the place.
    private static String getGeoNamePlaceURL(String placeName, String fcode){
        return "http://api.geonames.org/searchJSON?name_equals=" + URLEncoder.encode(placeName) + "&featureCode=" + fcode + "&country=CN&country=TW&maxRows=10&username=clement116";
    }
    
    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    private static String getJsonFromURL(String url_str){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(url_str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //Log.d("看这里看这里！", "卡卡");
            //Log.d("看这里看这里！", "卡卡" + connection.getResponseCode() + "1");
            int respCode = connection.getResponseCode();
            //Log.d("看这里看这里！", "code:" + respCode);
            if (respCode == 200) {
                //Log.d("再看这里！","200");
                return ConvertStream2Json(connection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("错误哦！",e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }

    private static String ConvertStream2Json(InputStream inputStream) {
        String jsonStr = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            jsonStr = new String(out.toByteArray());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonStr;
    }
}