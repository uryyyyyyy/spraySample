package com.github.uryyyyyyy.spray.helloWorld;


import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Sample {

    public static void main(String args[]) throws Exception{
        // このファクトリインスタンスは再利用可能でスレッドセーフです
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("ZPxFUE6GHEaICMflTjHlbgw0E", "7MDUMMwz0LWdHatOh1J3qVubgsvGkryg433VovfHAt8sgy7b8B");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try{
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                }else{
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
        //将来の参照用に accessToken を永続化する
        storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
        Status status = twitter.updateStatus(args[0]);
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
        System.exit(0);
    }
    private static void storeAccessToken(long useId, AccessToken accessToken){
        System.out.println(useId);
        System.out.println(accessToken);
    }
}
