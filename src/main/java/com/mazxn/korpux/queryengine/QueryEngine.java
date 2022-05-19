package com.mazxn.korpux.queryengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mazxn.korpux.Constants;
import com.mazxn.korpux.formatter.Formatter;

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

        public void run() {
            try {
                Scanner scanner = new Scanner(socket.getInputStream());
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                String query;
                while ((query = scanner.nextLine()) != null) {
                    List<String> words = new ArrayList<>();
                    for (String w : query.split(" ")) {
                        words.add(w);
                    }
                    words = new Formatter().formatWords(words);
                }

                scanner.close();
                writer.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
