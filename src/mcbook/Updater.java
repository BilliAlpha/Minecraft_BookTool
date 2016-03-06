/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mcbook;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Updater {
    private String url = "https://api.github.com/repos/BilliAlpha/Minecraft_BookTool/releases/latest";
    private JsonElement infos;
    
    public Updater() {
        try {
            fillInfos();
        } catch(Exception e) {}
    }
    
    private ArrayList<String> getText() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Application Minecraft_BookTool/"+Editor.VERSION);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();
        ArrayList<String> res = new ArrayList<String>();
        res.add(Integer.toString(responseCode));
        res.add(response.toString());
        return res;
    }
    
    private void fillInfos() throws Exception {
        String str = getText().get(1);
        JsonParser parser = new JsonParser();
        infos = parser.parse(str);
    }
    
    public boolean isUpToDate() {
        return latestVersion().equals(Editor.VERSION);
    }
    
    public String latestVersion() {
        String upVersion = infos.getAsJsonObject().get("tag_name").getAsString();
        return upVersion;
    }
    
    public String updateURL() {
        String upURL = infos.getAsJsonObject().get("html_url").getAsString();
        return upURL;
    }
    
    public String updateMessage() {
        return "An update of this software is avaible.\n"
                +"Current version: "+Editor.VERSION+"\n"
                +"Latest version: "+latestVersion()+"\n"
                +"Download at: "+updateURL();
    }
    
    public void check(Component c) {
        if (!isUpToDate())
            if (JOptionPane.showConfirmDialog(c, updateMessage()+"\n Do you want to download it ?", "Update avaible", JOptionPane.YES_NO_OPTION)==0) {
                try {
                    Desktop.getDesktop().browse(new URL(updateURL()).toURI());
                } catch(Exception e) {}
            }
    }
}
