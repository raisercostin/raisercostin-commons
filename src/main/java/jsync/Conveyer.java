// -< Conveyer.java >-------------------------------------------------*--------*
// JSYNC Version 1.04 (c) 1998 GARRET * ? *
// (Java synchronization classes) * /\| *
//                                                                   * / \ *
//                          Created: 20-Jun-98 K.A. Knizhnik * / [] \ *
//                          Last update: 10-Jul-98 K.A. Knizhnik * GARRET *
//-------------------------------------------------------------------*--------*
// Parallel data input and processing
//-------------------------------------------------------------------*--------*

package jsync;

/**
 * Class for parallel input data reading and processing. Two concurrent threads
 * are started: one of them reads data from input stream in cyclic buffer and
 * second thread performs processing of data, previously placed in another part
 * of the buffer.
 * <P>
 * Processing of data is performed by overridden method <code>process()</code>
 * of class derived from <code>Conveyer</code>. Processing of data continues
 * until end of stream is reached or <code>process()</code> method returns
 * <code>false</code>.
 */
public abstract class Conveyer {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Conveyer.class);

  /**
   * Create <code>Conveyer</code> object with specified size of cyclic
   * buffer and block size.
   *
   * @param stream
   *            input stream from which data is extracted
   * @param blockSize
   *            size of block for reading and processing data. Read and data
   *            processing operation will not take more than
   *            <code>blockSize</code> bytes of data.
   * @param bufferSize
   *            sizeof of cyclic buffer. Should be not less than
   *            <code>blockSize</code>.
   */
  public Conveyer(final java.io.InputStream stream, final int blockSize,
      final int bufferSize)
  {
    Assert.that(bufferSize > blockSize);
    buffer = new byte[bufferSize];
    this.bufferSize = bufferSize;
    this.blockSize = blockSize;
    this.stream = stream;
    consumer = new Consumer(this);
    producer = new Producer(this);
    consumerCS = new Object();
    producerCS = new Object();
  }

  /**
   * Create <code>Conveyer</code> object with specified block size. Size of
   * buffer is set by multiplying block size by two. So read and process
   * operations can be executed in parallel for different parts of the buffer.
   *
   * @param stream
   *            input stream from which data is extracted
   * @param blockSize
   *            size of block for reading and processing data. Input stream
   *            read and data processing operation will not take more than
   *            <code>blockSize</code> bytes of data.
   */
  public Conveyer(final java.io.InputStream stream, final int blockSize) {
    this(stream, blockSize, blockSize * 2);
  }

  /**
   * Set new input stream for conveyer.
   *
   * @param stream
   *            new input stream
   */
  public synchronized void setInputStream(final java.io.InputStream stream) {
    this.stream = stream;
  }

  /**
   * Start execution of reading and processing threads. This method doesn't
   * wait termination of these threads.
   */
  public synchronized void start() {
    result = false;
    endOfStream = false;
    producer.start();
    consumer.start();
  }

  /**
   * Wait termination of data processing thread. Reading thread is terminated
   * before data processing thread.
   *
   * @return <code>true</code> if all data from input stream is processed,
   *         <code>false</code> if processing of data is interrupted because
   *         <code>process()</code> method returns <code>false</code>.
   */
  public boolean waitTermination() {
    try {
      consumer.join();
    } catch (final InterruptedException ex) {
      log.info("ignored", ex);
      throw new InterruptedError();
    }
    return result;
  }

  /**
   * Abstract method to be implemented in derived class. This method should
   * perform processing of data placed in specified segment of cyclic buffer.
   *
   * @param buffer
   *            cyclic buffer maintained by <code>Conveyer</code> class
   * @param offset
   *            position in buffer of data to be processed
   * @param length
   *            number of bytes of data available for processing. Value of
   *            this parameter never exceeds <code>blockSize</code>.
   * @return if this method returns <code>false</code> processing of data is
   *         finished, and both threads are stopped.
   */
  public abstract boolean process(byte[] buffer, int offset, int length);

  protected void consume() {
    while (true) {
      int available;
      synchronized (consumerCS) {
        while (getPos == putPos) { // no data available for processing
          if (endOfStream) {
            result = true;
            return;
          }
          try {
            consumerCS.wait();
          } catch (final InterruptedException ex) {
            log.info("ignored", ex);
            throw new InterruptedError();
          }
        }
        if (putPos >= getPos) {
          available = putPos - getPos;
        } else {
          available = bufferSize - getPos;
        }
      }
      if (available > blockSize) {
        available = blockSize;
      }
      if (!process(buffer, getPos, available)) {
        if (!endOfStream) {
          synchronized (producerCS) {
            terminate = true;
            producerCS.notify();
          }
        }
        result = false;
        return;
      }
      synchronized (producerCS) {
        getPos += available;
        if (getPos == bufferSize) {
          getPos = 0;
        }
        producerCS.notify();
      }
    }
  }

  protected void produce() {
    while (!terminate) {
      int available;
      synchronized (producerCS) {
        while (getPos == putPos + 1) { // buffer is full
          try {
            producerCS.wait();
          } catch (final InterruptedException ex) {
            log.info("ignored", ex);
            throw new InterruptedError();
          }
        }
        if (terminate) {
          return;
        }
        if (putPos >= getPos) {
          available = bufferSize - putPos;
        } else {
          available = getPos - putPos - 1;
        }
      }
      Assert.that(available > 0);
      if (available > blockSize) {
        available = blockSize;
      }
      int len;
      try {
        len = stream.read(buffer, putPos, available);
      } catch (final java.io.IOException ex) {
        log.info("ignored", ex);
        len = -1;
      }

      if (len <= 0) {
        endOfStream = true;
        try {
          stream.close();
        } catch (final java.io.IOException ex) {
          log.info("ignored", ex);
        }
        synchronized (consumerCS) {
          consumerCS.notify();
        }
        return;
      }
      synchronized (consumerCS) {
        putPos += len;
        if (putPos == bufferSize) {
          putPos = 0;
        }
        consumerCS.notify();
      }
    }
  }

  protected int blockSize;

  protected int bufferSize;

  protected int putPos;

  protected int getPos;

  protected byte[] buffer;

  protected Thread consumer;

  protected Thread producer;

  protected boolean endOfStream;

  protected boolean result;

  protected boolean terminate;

  Object consumerCS, producerCS;

  protected java.io.InputStream stream;
}

class Consumer extends Thread {
  Conveyer conveyer;

  @Override
  public void run() {
    conveyer.consume();
  }

  Consumer(final Conveyer conveyer) {
    this.conveyer = conveyer;
  }
}

class Producer extends Thread {
  Conveyer conveyer;

  @Override
  public void run() {
    conveyer.produce();
  }

  Producer(final Conveyer conveyer) {
    this.conveyer = conveyer;
  }
}
