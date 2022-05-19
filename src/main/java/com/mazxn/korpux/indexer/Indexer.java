package com.mazxn.korpux.indexer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import com.mazxn.korpux.Constants;
import com.mazxn.korpux.formatter.Parser;
import com.mazxn.korpux.persistence.AsyncEntryWriter;
import com.mazxn.korpux.persistence.DocumentManager;

public class Indexer {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(Constants.INDEXER_PORT);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.out.println("KORPUX INDEXER SHUTTING DOWN");
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

            System.out.println("KORPUX INDEXER READY");

            while (true) {
                Socket client = socket.accept();
                new IndexerWorker(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class IndexerWorker extends Thread {
        private Socket socket;

        public IndexerWorker(Socket socket) {
            this.socket = socket;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(socket.getInputStream());
                Hashtable<String, String> docs = (Hashtable<String, String>) in.readObject();

                System.out.println("RECEIVED " + docs.size() + " DOCUMENTS");

                for (String key : docs.keySet()) {
                    DocumentManager.put(key, docs.get(key));
                    AsyncEntryWriter.write(Parser.parse(docs.get(key), key));
                    System.out.println("PARSED: " + key);
                }

                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}