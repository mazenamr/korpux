package com.mazxn.korpux.indexer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import com.mazxn.korpux.Constants;
import com.mazxn.korpux.persistence.AsyncWriter;

public class Indexer {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(Constants.INDEXER_PORT);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.out.println("KORPUX INDEXER SHUTTING DOWN");
                        if (args.length > 1) {
                            if (args[1].toLowerCase().equals("printkeys")) {
                                System.out.println("Printing keys");
                            }
                            else {
                                System.out.println(args[1]);
                            }
                        }
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
                System.out.print("RECEIVED REQUEST FROM: " + client.getInetAddress().getHostAddress());
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

        public void run() {
            while (true) {
                ObjectInputStream in = null;
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    @SuppressWarnings("unchecked")
                    Hashtable<String, String> docs = (Hashtable<String, String>) in.readObject();

                    System.out.println("RECEIVED " + docs.size() + " DOCUMENTS");

                    for (String key : docs.keySet()) {
                        AsyncWriter.write(Parser.parse(docs.get(key), key));
                        System.out.println("PARSED: " + key);
                    }

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
}