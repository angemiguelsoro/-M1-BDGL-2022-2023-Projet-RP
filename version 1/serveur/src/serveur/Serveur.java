package serveur;

import java.net.*;
import java.io.*;

public class Serveur {
   private static int port = 8080;
   private static String directory = "/home/angemiguel/Documents/gate/serveur/File_on_server"; // Chemin absolu du répertoire de fichiers
   
   public static void main(String[] args) throws IOException {
      @SuppressWarnings("resource")
	ServerSocket serverSocket = new ServerSocket(port);
      System.out.println("Serveur démarré sur le port " + port);
      
      while (true) {
         Socket clientSocket = serverSocket.accept();
         System.out.println("Connexion entrante de " + clientSocket.getInetAddress().getHostAddress());
         Thread t = new Thread(new ClientHandler(clientSocket));
         t.start();
      }
   }
   
   private static class ClientHandler implements Runnable {
      private Socket clientSocket;
      
      public ClientHandler(Socket socket) {
         this.clientSocket = socket;
      }
      
      public void run() {
         try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Le client est connecté: " + clientSocket.getInetAddress().getHostAddress());
            out.println(getFileList());
            
            String files = in.readLine();
            String[] filenames = files.split(",");
            for (String filename : filenames) {
               sendFile(filename, clientSocket);
            }
         } catch (IOException e) {
            System.err.println("Erreur: " + e.getMessage());
         } finally {
            try {
               clientSocket.close();
            } catch (IOException e) {
               System.err.println("Erreur lors de la fermeture du socket: " + e.getMessage());
            }
         }
      }
      
      private String getFileList() {
         File folder = new File(directory);
         File[] files = folder.listFiles();
         StringBuilder sb = new StringBuilder();
         for (File file : files) {
            if (file.isFile()) {
               sb.append(file.getName() + ",");
            }
         }
         return sb.toString();
      }
      
      private void sendFile(String filename, Socket clientSocket) throws IOException {
         FileInputStream fileInputStream = null;
         BufferedInputStream bufferedInputStream = null;
         OutputStream outputStream = null;
         try {
            File file = new File(directory + "/" + filename);
            byte[] byteArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(byteArray, 0, byteArray.length);
            outputStream = clientSocket.getOutputStream();
            outputStream.write(byteArray, 0, byteArray.length);
            outputStream.flush();
            System.out.println("Fichier envoyé: " + filename);
         } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du fichier: " + e.getMessage());
         } finally {
            if (bufferedInputStream != null) bufferedInputStream.close();
            if (outputStream != null) outputStream.close();
         }
      }
   }
}
