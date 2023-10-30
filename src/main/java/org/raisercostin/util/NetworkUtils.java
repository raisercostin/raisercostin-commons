package org.raisercostin.util;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.xml.ws.BindingProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.raisercostin.utils.ObjectUtils;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.*;

public class NetworkUtils {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NetworkUtils.class);
  private static final Map<String, InetAddress> allInterfaces = listAllInterfaces();

  static {
    LOG.debug("Available network addresses: " + allInterfaces + " detectedNonLocal is " + getCurrentExternalIp());
  }

  public static InetAddress getCurrentLoopbackAddress() {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Available network addresses: " + allInterfaces);
    }
    InetAddress myAddress = null;
    for (InetAddress addr : allInterfaces.values()) {
      if (addr.isLoopbackAddress()) {
        myAddress = addr;
      }
    }
    if (myAddress == null) {
      throw new RuntimeException("Server does not have LoopbackAddress to use");
    } else {
      return myAddress;
    }
  }

  public static InetAddress getCurrentAddress() {
    if (LOG.isTraceEnabled()) {
      LOG.trace("Available network addresses: " + allInterfaces);
    }
    InetAddress myAddress = null;
    InetAddress any = null;
    for (InetAddress addr : allInterfaces.values()) {
      if (any == null) {
        any = addr;
      }
      boolean isLocal = isLocalNetworkAddress(addr);
      if (LOG.isTraceEnabled()) {
        LOG.trace("IsLocal " + isLocal + " - " + toString(addr));
      }
      boolean isIPv6add = isIpV6(addr);
      if (!isIPv6add) {
        if (!isLocal) {
          myAddress = addr;
        }
      }
    }
    // if (myAddress == null) {
    // myAddress = any;
    // }

    if (myAddress == null) {
      try {
        myAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);

      }
    }
    return myAddress;
  }

  public static String getCurrentExternalIp() {
    return getCurrentAddress().getHostAddress();
  }

  public static String getCurrentLoopbackIp() {
    return getCurrentLoopbackAddress().getHostAddress();
  }

  private static Map<String, InetAddress> listAllInterfaces() {
    Map<String, InetAddress> all = new TreeMap<>();
    try {
      Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      for (NetworkInterface iface : Collections.list(ifaces)) {
        if (LOG.isTraceEnabled()) {
          LOG.trace(iface.toString());
        }
        Enumeration<InetAddress> vaddrs = iface.getInetAddresses();
        for (InetAddress vaddr : Collections.list(vaddrs)) {
          if (LOG.isTraceEnabled()) {
            LOG.trace("-" + toString(vaddr));
          }
          if (!all.containsKey(vaddr.getHostAddress())) {
            all.put(vaddr.getHostAddress(), vaddr);
          }
        }
        Enumeration<NetworkInterface> virtualIfaces = iface.getSubInterfaces();
        ArrayList<NetworkInterface> list = Collections.list(virtualIfaces);
        if (list.size() > 0) {
          for (NetworkInterface viface : list) {
            if (LOG.isTraceEnabled()) {
              LOG.trace("*" + iface);
            }
            Enumeration<InetAddress> avaddrs = viface.getInetAddresses();
            for (InetAddress vaddr : Collections.list(avaddrs)) {
              if (LOG.isTraceEnabled()) {
                LOG.trace("-" + toString(vaddr));
              }
              if (!all.containsKey(vaddr.getHostAddress())) {
                all.put(vaddr.getHostAddress(), vaddr);
              }
            }
          }
        }
      }
    } catch (SocketException e) {
      throw new WrappedCheckedException2(e);
    }
    return all;
  }

  public static boolean isLocalNetworkAddress(InetAddress addr) {
    return addr.isLoopbackAddress() || addr.isSiteLocalAddress();
    // LOG.debug(toString(addr));
    // String LocalNetworkIPClassA = "158";
    // return addr.getHostAddress().startsWith(LocalNetworkIPClassA);
  }

  public static String toShortString(InetAddress addr) {
    StringBuilder sb = new StringBuilder();
    sb.append(addr.getHostAddress()).append('/').append(addr.getCanonicalHostName());
    return sb.toString();
  }

  public static String toString(InetAddress addr) {
    StringBuilder sb = new StringBuilder();
    sb.append("InetAddress[");
    sb.append("HostAddress=" + addr.getHostAddress());
    sb.append(", CanonicalHostName=" + addr.getCanonicalHostName());
    sb.append(", HostName=" + addr.getHostName());
    sb.append(", isAnyLocalAddress=" + addr.isAnyLocalAddress());
    sb.append(", isLinkLocalAddress=" + addr.isLinkLocalAddress());
    sb.append(", isLoopbackAddress=" + addr.isLoopbackAddress());
    sb.append(", isMulticastAddress=" + addr.isMulticastAddress());
    sb.append(", isSiteLocalAddress=" + addr.isSiteLocalAddress());
    sb.append(']');
    return sb.toString();
  }

  public static final int CONNECT_TIMEOUT = 1 * 60 * 1000;
  public static final int REQUEST_TIMEOUT = 1 * 60 * 1000;

  @SuppressWarnings("boxing")
  public static <T> void configureWebServiceClientCapabilities(T client) {
    Map<String, Object> properties = ((BindingProvider) client).getRequestContext();
    // HACK to always have timeouts
    boolean configureTimeouts = false;
    if (configureTimeouts) {
      properties.put("javax.xml.ws.client.receiveTimeout", REQUEST_TIMEOUT);
      // properties.put(com.sun.xml.internal.ws.client.BindingProviderProperties.REQUEST_TIMEOUT,
      // REQUEST_TIMEOUT);
      properties.put("com.sun.xml.ws.request.timeout", REQUEST_TIMEOUT);
      properties.put("com.sun.xml.internal.ws.request.timeout", REQUEST_TIMEOUT);

      properties.put("javax.xml.ws.client.connectionTimeout", CONNECT_TIMEOUT);
      // properties.put(com.sun.xml.internal.ws.client.BindingProviderProperties.CONNECT_TIMEOUT,
      // CONNECT_TIMEOUT);
      properties.put("com.sun.xml.ws.connect.timeout", CONNECT_TIMEOUT);
      properties.put("com.sun.xml.internal.ws.connect.timeout", CONNECT_TIMEOUT);

      // MyInterface myInterface = new
      // MyInterfaceService().getMyInterfaceSOAP();
      // Map<String, Object> requestContext =
      // ((BindingProvider)myInterface).getRequestContext();
      // requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT,
      // 3000); // Timeout in millis
      // requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT,
      // 1000); // Timeout in millis
      // myInterface.callMyRemoteMethodWith(myParameter);
    }
  }

  private static boolean isIpV6(InetAddress addr) {
    return addr.getAddress().length == 16;
  }

  public static int getRandomFreePort() {
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(0);
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static Map<String, InetAddress> listAllIps() {
    return allInterfaces;
  }

  public static boolean isConnectionAvailable(String url) {
    Pair<HttpStatus, String> response = getUrlResult(url);
    if (response._1.series() == Series.INFORMATIONAL || response._1.series() == Series.SUCCESSFUL
        || response._1.series() == Series.REDIRECTION) {
      return true;
    }
    LOG.info("The server replied but with the following invalid status code [" + response
        + "] that is not among the goodCodes [200,300).");
    return false;
  }

  public static Pair<HttpStatus, String> getUrlResult(String url) {
    String msg = "no message";
    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpParams params = httpClient.getParams();
      HttpConnectionParams.setConnectionTimeout(params, 1000);
      HttpConnectionParams.setSoTimeout(params, 1000);
      HttpGet method = new HttpGet(new URI(url));
      LOG.debug("HTTP method {}", method);
      LOG.trace("HTTP params {}", params);
      LOG.trace("Connecting to {}", method.getURI());
      HttpResponse response = httpClient.execute(method);
      return Pair.create(HttpStatus.valueOf(response.getStatusLine().getStatusCode()), response.toString());
    } catch (ClientProtocolException e) {
      // TODO we need more fine grained errors to be caught here.
      LOG.trace("HTTP protocol error", e);
      msg = "HTTP protocol error:" + ObjectUtils.toStringDump(e);
    } catch (org.apache.http.conn.ConnectTimeoutException e) {
      LOG.trace("Connection unavailable", e);
      msg = e.getMessage() + "(" + e.getMessage() + ")";
    } catch (org.apache.http.conn.HttpHostConnectException e) {
      LOG.trace("Connection unavailable", e);
      msg = e.getClass() + "(" + e.getMessage() + ")";
    } catch (IOException e) {
      // TODO we need more fine grained errors to be caught here.
      LOG.trace("Connection unavailable", e);
      msg = "Connection unavailable:" + ObjectUtils.toStringDump(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Url [" + url + "] is not wellformed.", e);
    }
    return Pair.create(HttpStatus.SERVICE_UNAVAILABLE, msg);
  }
}
