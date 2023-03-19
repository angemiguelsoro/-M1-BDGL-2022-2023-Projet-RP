package client;

import java.net.*;
import java.io.*;

public class Client {
   private static int port = 8080;
   private static String serverAddress = "127.0.0.1";
   private static String downloadDirectory = "../"; // Chemin absolu du répertoire de téléchargement
   
   public static void main(String[] args) throws IOException {
      Socket socket = null;
      BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
      
      try {
         socket = new Socket(serverAddress, port);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         System.out.println("Connecté au serveur " + serverAddress + ":" + port);
         System.out.println("Liste des fichiers hébergés:");
         String fileList = in.readLine();
         String[] files = fileList.split(",");
         for (String file : files) {
            System.out.println(file);
         }
         
         System.out.print("Entrez le nom du fichier à télécharger (séparé par une virgule si plusieurs fichiers): ");
         String input = consoleIn.readLine();
         out.println(input);
         
         String[] filenames = input.split(",");
         for (String filename : filenames) {
            byte[] byteArray = new byte[1024*1024];
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(downloadDirectory + "/" + filename);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(byteArray)) != -1) {
               bufferedOutputStream.write(byteArray, 0, bytesRead);
               bufferedOutputStream.flush();
            }
            bufferedOutputStream.close();
            System.out.println("Fichier téléchargé: " + filename);
         }
      } catch (IOException e) {
         System.err.println("Erreur: " + e.getMessage());
      } finally {
         if (socket != null) socket.close();
      }
   }
}







