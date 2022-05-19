package com.mazxn.korpux.queryengine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mazxn.korpux.Constants;
import com.mazxn.korpux.formatter.Formatter;
import com.mazxn.korpux.persistence.DocumentManager;
import com.mazxn.korpux.persistence.Entry;
import com.mazxn.korpux.persistence.EntryManager;
import com.mazxn.korpux.ranker.Ranker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class QueryEngine {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(Constants.QUERY_ENGINE_PORT);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.out.println("KORPUX QUERY ENGINE SHUTTING DOWN");
                        try {
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("KORPUX QUERY ENGINE READY");

            while (true) {
                Socket client = socket.accept();
                new QueryEngineWorker(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class QueryEngineWorker extends Thread {
        private Socket socket;

        public QueryEngineWorker(Socket socket) {
            this.socket = socket;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());

                String query = (String) in.readUTF();
                System.out.println(query);
                List<String> words = new ArrayList<>();
                for (String w : query.split(" ")) {
                    words.add(w);
                }
                words = new Formatter().formatWords(words);

                Hashtable<String, List<Entry>> entries = new Hashtable<>();
                for (String word : words) {
                    entries.put(word, EntryManager.getByWord(word));
                }
                List<String> result = Ranker.rank(entries);
                for (String r : result) {
                    System.out.println(r);
                }
                JSONObject json = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (String r : result) {
                    String html = DocumentManager.get(r);
                    Document document = Jsoup.parse(html.toLowerCase(), r);
                    JSONObject j = new JSONObject();
                    j.put("title", document.title());
                    j.put("url", r);
                    jsonArray.add(j);
                } 
                json.put("result", jsonArray);
                System.out.println("SENT JSON: " + json.toString());
                dout.writeUTF(json.toString());

                dout.flush();
                dout.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
