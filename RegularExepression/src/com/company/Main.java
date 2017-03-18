package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here
        ArrayList<Video> result = new ArrayList<>();
        URL url = new URL("http://www.rarbt.com/");
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.matches("<div class=\"litpic\"><a href=" + ".+")) {
                Video video = new Video();
                String title = inputLine.substring(inputLine.indexOf("title=") + 7, inputLine.indexOf("\" target="));
                String videoUrl = inputLine.substring(inputLine.indexOf("href=") + 6, inputLine.indexOf("\" title="));
                video.setTitle(title);
                video.setUrl(videoUrl);
                video.setImdb(getImdb(videoUrl));
                //System.out.println(line);
                //System.out.println(video);
                System.out.println(video.getTitle());
                //System.out.println(video.getUrl());
                System.out.println(video.getImdb());
                System.out.println();
                result.add(video);
            }
        }
        in.close();
        System.out.println("影片總數:" + result.size());
    }

    private  static String getImdb(String url) throws IOException {
        String imdb = "";
        URL videoUrl = new URL("http://www.rarbt.com/" + url);
        URLConnection yc = videoUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.matches("^\\s+" + "<li>imdb:" + ".+" + "</a></li>")) {
                if ((inputLine.indexOf("</a></li>")) > 0) {
                    imdb = inputLine.substring(inputLine.lastIndexOf("\">") + 2, inputLine.indexOf("</a></li>"));
                }
            }

        }
        in.close();
        if (imdb.equals("")) {
            imdb = "empty";
        }
        return imdb;
    }
}
