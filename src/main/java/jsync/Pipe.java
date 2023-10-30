// -< Pipe.java >-----------------------------------------------------*--------*
// JSYNC Version 1.04 (c) 1998 GARRET * ? *
// (Java synchronization classes) * /\| *
//                                                                   * / \ *
//                          Created: 20-Jun-98 K.A. Knizhnik * / [] \ *
//                          Last update: 6-Jul-98 K.A. Knizhnik * GARRET *
//-------------------------------------------------------------------*--------*
// Link two existed input and output threads.
//-------------------------------------------------------------------*--------*

package jsync;

import java.io.*;

/**
 * This class links input and output streams so that data taken from input
 * stream is transfered to the output stream. This class can be used to connect
 * standard input/ouput stream of Java application with output/input streams of
 * spawned child process, so that all user's input is redirected to the child
 * and all it's output is visible for the user.
 * <P>
 * This class starts a thread, which transfers data from input stream to output
 * stream until End Of File is reached or IOException caused by IO error is
 * catched.
 */
public class Pipe {
  /**
   * Default size of buffer used to transfer data from the input stream to the
   * output stream.
   */
  static public int defaultBufferSize = 4096;

  /**
   * Establish connection between input and output streams with specified size
   * of buffer used for data transfer.
   *
   * @param in
   *            input stream
   * @param out
   *            output stream
   * @param bufferSize
   *            size of buffer used to transfer data from the input stream to
   *            the output stream
   */
  static public void between(final InputStream in, final OutputStream out,
      final int bufferSize) {
    (new PipeThread(in, out, bufferSize)).start();
  }

  /**
   * Establish connection between input and output streams with default buffer
   * size.
   *
   * @param in
   *            input stream
   * @param out
   *            output stream
   */
  static public void between(final InputStream in, final OutputStream out) {
    (new PipeThread(in, out, defaultBufferSize)).start();
  }
}

class PipeThread extends Thread {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PipeThread.class);

  InputStream in;

  OutputStream out;

  byte[] buffer;

  PipeThread(final InputStream in, final OutputStream out,
      final int bufferSize)
  {
    this.in = in;
    this.out = out;
    buffer = new byte[bufferSize];
  }

  @Override
  public void run() {
    try {
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
    } catch (final IOException ex) {
      log.info("ignored", ex);
    }
  }
}
