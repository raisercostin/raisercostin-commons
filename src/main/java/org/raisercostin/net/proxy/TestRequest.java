package org.raisercostin.net.proxy;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author cgr
 * @version
 */
public class TestRequest {
  final static char CR = 13;

  final static char LF = 10;

  /** Creates new WebServer */
  public String getContent(final String req) throws IOException {
    try (final Socket client = new Socket("http://org.raisercostin.home.ro", 80)) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(
        client.getInputStream()));
      final PrintWriter writer = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(client.getOutputStream())), true);
      System.out.println("Cerere:\n(" + req + ")");
      writer.print(req);

      System.out.println("gata");
      final StringBuffer buf = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        buf.append(line + "\n");
        System.out.println("Primesc:(" + line + ")");
      }
      return buf.toString();
    }
  }

  public static void main(final String argv[]) {
    try {
      final TestRequest th = new TestRequest();
      System.out.println(th
        .getContent("GET http://org.raisercostin.home.ro/ HTTP/1.0" + CR + LF
            + CR + LF));
    } catch (final Exception e) {
      e.printStackTrace();
    }
    System.out.println("gata");
  }
}
