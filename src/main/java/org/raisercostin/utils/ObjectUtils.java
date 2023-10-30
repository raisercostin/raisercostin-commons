package org.raisercostin.utils;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.parsers.*;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSOutput;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xml.security.c14n.CanonicalizationException;
//import org.bouncycastle.crypto.RuntimeCryptoException;
//import org.raisercostin.utils.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class ObjectUtils {
  private static final Logger log = LoggerFactory.getLogger(ObjectUtils.class);

  private static final String IGNORED_VALUE = "*****";
  // private static final MyStringStyle myStringStyle = new MyStringStyle();

  private static final boolean DEFAULT_TRANSIENTS = false;
  private static final String TO_STRING_METHOD = "toString";
  private static final int STEP = 4;
  private static final String all = ".   .   .   .   .   .   .   .   .   .   .   .   .   .   ";
  private static final Pattern XML_STRING_PATTERN = Pattern.compile("^<(\\w+)>.+</\\1>$", Pattern.DOTALL);
  // Thread local is needed because multiple cascaded toStrings could be
  // invoked.
  private static final ThreadLocal<ObjectUtilsContext> contextOnThread = new ThreadLocal<>();
  private static final int MAX_SHORT_STRING = 10;
  private static final int MAX_DEEP = 6;
  private static final String END_OF_LINE = "\n";

  // UTILITIES
  public static void copy(Object destination, Object source) {
    CloneBuilder.reflectionCopy(destination, source);
  }

  public static boolean equals(Object object1, Object object2) {
    return EqualsBuilder.reflectionEquals(object1, object2, false, Object.class, null);
  }

  public static int hashCode(Object object) {
    return HashCodeBuilder.reflectionHashCode(object, DEFAULT_TRANSIENTS);
  }

  public static int hashCode(Object object, String commaSeparatedExceptedFields) {
    return HashCodeBuilder.reflectionHashCode(17, 37, object, DEFAULT_TRANSIENTS, null,
      tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true));
  }

  public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {

    if (str == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  public static String[] toStringArray(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  public static BigDecimal normalize(BigDecimal rate) {
    if (rate != null) {
      return rate.stripTrailingZeros();
    }
    return null;
  }

  public static Object rethrow(Throwable e) {
    if (e instanceof RuntimeException) {
      throw (RuntimeException) e;
    }
    if (e instanceof Error) {
      throw (Error) e;
    }
    throw new RuntimeException(e);
  }

  public static void validate(Object object) {
    // BeanUtils.validate(object);
  }

  @SuppressWarnings("restriction")
  public static String formatXml2(String unformattedXml) {
    try {
      DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
      DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
      LSSerializer writer = impl.createLSSerializer();
      ByteArrayOutputStream byteArrayOutputStrm = new ByteArrayOutputStream();
      LSOutput output = impl.createLSOutput();
      output.setByteStream(byteArrayOutputStrm);
      final Document document = parseXmlFile(unformattedXml);
      writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
      writer.getDomConfig().setParameter("{http://xml.apache.org/xslt}indent-amount", "4");
      writer.write(document, output);
      String result = byteArrayOutputStrm.toString();
      return result;
    } catch (ClassNotFoundException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (InstantiationException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (IllegalAccessException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (ClassCastException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (ParserConfigurationException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (SAXException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (IOException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    }
  }

  private static String formatXml(String unformattedXml) {
    try {
      final Document document = parseXmlFile(unformattedXml);
      OutputFormat format = new OutputFormat(document);
      format.setLineWidth(120);
      format.setIndenting(true);
      format.setIndent(2);
      format.setOmitXMLDeclaration(!unformattedXml.startsWith("<?xml"));
      format.setOmitComments(false);
      format.setPreserveEmptyAttributes(true);
      Writer out = new StringWriter();
      XMLSerializer serializer = new XMLSerializer(out, format);
      serializer.serialize(document);
      String result = out.toString();
      if (result.endsWith("\r\n")) {
        result = result.substring(0, result.length() - "\r\n".length());
      }
      if (result.endsWith("\n")) {
        result = result.substring(0, result.length() - "\n".length());
      }
      return result;
    } catch (ParserConfigurationException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (SAXException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    } catch (IOException e) {
      return "!xmlFormatingFailed " + e + " xml=[" + unformattedXml + "]";
    }
  }

  private static Document parseXmlFile(String in) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(in));
    return db.parse(is);
  }

  // TO STRING UTILITIES
  public static String toStringDump(Object object) {
    return internalToStringWithContext(object, false, false, false, "", "", END_OF_LINE, DEFAULT_TRANSIENTS, false,
      false);
  }

  public static String toStringDump(Object object, String ignores, String excludes, boolean showTransient,
      boolean showIds) {
    return internalToStringWithContext(object, false, false, false, ignores, excludes, END_OF_LINE, showTransient,
      false, showIds);
  }

  public static String toStringDump(Object object, String ignores) {
    return toStringDump(object, ignores, ignores);
  }

  public static String toStringDump(Object object, String ignores, String excludes) {
    return internalToStringWithContext(object, false, false, false, ignores, excludes, END_OF_LINE, DEFAULT_TRANSIENTS,
      false, false);
  }

  public static String toString(Object object) {
    return internalToStringWithContext(object, false, true, false, "", "", END_OF_LINE, DEFAULT_TRANSIENTS, false,
      false);
  }

  public static String toString(Object object, String ignores) {
    return internalToStringWithContext(object, false, true, false, ignores, "", END_OF_LINE, DEFAULT_TRANSIENTS, false,
      false);
  }

  public static String toString(Object object, boolean singleLine, boolean useToString, boolean displayTypes,
      String endOfLine, boolean showTransients, boolean showIds) {
    return internalToStringWithContext(object, singleLine, useToString, displayTypes, "", "", endOfLine, showTransients,
      false, showIds);
  }

  // TOSTRING WITH EXCLUDES - should be implemented in another way
  @Deprecated
  private static String toString(Object object, boolean singleLine, boolean classDecorators, String[] excludes) {
    ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object,
      new MyStringStyle(singleLine, true, classDecorators, false, false, DEFAULT_TRANSIENTS), null, null, false,
      false);
    builder.setExcludeFieldNames(excludes);
    return builder.toString();
  }

  @Deprecated
  private static String toStringWithExclusions(Object object, boolean singleLine, boolean classDecorators,
      String commaSeparatedExceptedFields) {
    return toStringWithExclusions(object, singleLine, classDecorators,
      tokenizeToStringArray(commaSeparatedExceptedFields, ",", true, true));
  }

  @Deprecated
  public static String toStringWithExclusions(Object object, boolean singleLine, boolean classDecorators,
      String... excludes) {
    return toString(object, singleLine, classDecorators, excludes);
  }

  // IMPLEMENTATION
  private static String internalToStringWithContext(Object object, boolean singleLine, boolean useToString,
      boolean displayTypes, String ignores, String excludes, String endOfLine, boolean showTransients,
      boolean showStatics, boolean showIds) {
    createContext(ignores, excludes, endOfLine, showIds);
    try {
      return internalToString(object, useToString,
        new MyStringStyle(singleLine, useToString, true, displayTypes, showTransients, showStatics));
    } catch (Throwable e) {
      log.warn("Generic toString operation failed.", new RuntimeException(e));
      return "<invalidToString>";
    } finally {
      removeContext();
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static String internalToString(Object object, boolean useToString, MyStringStyle toStringStyle) {
    ObjectUtilsContext context = getContext();
    if (context.isMaximumLevel()) {
      return "...(more)";
    }
    if (object == null) {
      return null;
    }
    if (isShortType(object.getClass())) {
      return object.toString();
    }
    if (object instanceof String && ((String) object).length() < MAX_SHORT_STRING) {
      return (String) object;
    }
    String old = context.find(object);
    if (old != null) {
      if (!context.isOriginalToStringCalled(object)) {
        return old;
      }
      // else {
      // return "#" + System.identityHashCode(object);
      // }
    }
    String id = context.save(object);
    if (object instanceof String) {
      return context.prefix(id + ":decS:") + declaredToString(id, object);
    }
    try {
      String value = null;
      if (object instanceof Throwable) {
        value = throwableToString((Throwable) object, useToString, toStringStyle);
      } else if (object instanceof Map) {
        value = mapToString((Map<Object, Object>) object, useToString, toStringStyle);
      } else if (object instanceof Collection) {
        value = collectionToString((Collection<Object>) object, useToString, toStringStyle);
      } else if (object.getClass().isArray()) {
        value = arrayToString(object, useToString, toStringStyle);
      } else if ((object instanceof Class) && ((Class<?>) object).isEnum()) {
        value = enumToString((Class<Enum>) object, useToString, toStringStyle);
        // } else if ((object instanceof
        // org.apache.xml.security.signature.XMLSignatureInput)
        // && (((org.apache.xml.security.signature.XMLSignatureInput)
        // object).getBytes() == null)) {
        // // fix a bug in this class
        // return "null";
      } else {
        if (useToString && !context.isOriginalToStringCalled(object)
            && !hasBadToStringImplementation(object.getClass())) {
          value = context.prefix("dec:") + declaredToString(id, object);
        } else {
          value = context.prefix("ref:") + reflectedToString(object, useToString, toStringStyle);
        }
      }
      return context.prefix(id + ":") + value;
    } catch (/* CanonicalizationException | */RuntimeException e) {
      throw new RuntimeException(e);
    }
  }

  private static String toIdentedString(String text) {
    text = text.replaceAll("\r\n|\n|\r", getContext().getRowEnd() + getContext().getRowStart());
    return text;
  }

  private static boolean isXml(Object object) {
    if (!(object instanceof String)) {
      return false;
    }
    String xml = (String) object;
    if (xml.startsWith("<?xml")) {
      return true;
    }
    if (XML_STRING_PATTERN.matcher(xml).matches()) {
      return true;
    }
    return false;
  }

  private static String declaredToString(String id, Object object) {
    if (isXml(object)) {
      return formatXml((String) object);
    }
    getContext().callOriginalToString(object);
    // return id + ":" +
    return callToString(object);
  }

  private static String callToString(Object object) {
    try {
      return (String) object.getClass().getMethod(TO_STRING_METHOD).invoke(object);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Can't call toString on object of type " + object.getClass(), e);
    } catch (NoSuchMethodException e) {
      log.info("ignored", e);
      return object.toString();
    }
  }

  private static String enumToString(@SuppressWarnings("rawtypes") Class<Enum> object, boolean useToString,
      MyStringStyle toStringStyle) {
    @SuppressWarnings("rawtypes")
    Enum[] constants = object.getEnumConstants();
    StringBuilder sb = new StringBuilder();
    sb.append(declaredToString("", object)).append(" enums=[");
    for (@SuppressWarnings("rawtypes")
    Enum oneConstant : constants) {
      sb.append(getContext().getRowEnd())
        .append(oneConstant.name())
        .append("(")
        .append(oneConstant.ordinal())
        .append(")=")
        .append(ObjectUtils.toStringWithExclusions(oneConstant, true, false, "name", "ordinal"));
    }
    sb.append("]");
    return sb.toString();
  }

  // others

  private static void createContext(String ignores, String excludes, String endOfLine, boolean showIds) {
    if (contextOnThread.get() == null) {
      contextOnThread.set(new ObjectUtilsContext(STEP, ignores, excludes, endOfLine, showIds));
    }
    contextOnThread.get().incrementToStringCalls();
  }

  private static void removeContext() {
    ObjectUtilsContext context = contextOnThread.get();
    if (context == null) {
      throw new RuntimeException("Too many removeContext.");
    }
    if (context.decrementToStringCalls()) {
      contextOnThread.set(null);
    }
  }

  private static ObjectUtilsContext getContext() {
    return contextOnThread.get();
  }

  private static final Set<Class> SHORT_TYPES = new HashSet(
    Arrays.asList(Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
      Void.class, Timestamp.class, Date.class, java.sql.Date.class, Time.class, Boolean.class));

  private static boolean isShortType(Class clazz) {
    return SHORT_TYPES.contains(clazz);
    // return clazz.getName().startsWith("java.lang");
    // || clazz.getName().startsWith("java") && hasOriginalToString(clazz)
    // && !clazz.isAssignableFrom(Map.class)
    // && !clazz.isAssignableFrom(Collection.class) &&
    // !clazz.isAssignableFrom(Throwable.class);
  }

  private static boolean hasBadToStringImplementation(Class clazz) {
    try {
      return Throwable.class.isAssignableFrom(clazz)
          || clazz.getMethod("toString").getDeclaringClass().equals(Object.class);
    } catch (NoSuchMethodException e) {
      log.info("ignored", e);
      return false;
    }
  }

  private static class ObjectUtilsContext {
    private int identation = 0;
    private final Map<Object, String> objects = new HashMap<>();
    private final Set<Integer> toStringForSelf = new HashSet<>();
    private int toStringCallsCounter;
    private final int step;
    private final String excludes;
    private final String ignores;
    private final String endOfLine;
    private final boolean showIds;

    public ObjectUtilsContext(int step, String ignores, String excludes, String endOfLine, boolean showIds) {
      this.step = step;
      this.excludes = "," + excludes + ",";
      this.ignores = "," + ignores + ",";
      identation = 0;
      toStringCallsCounter = 0;
      this.endOfLine = endOfLine;
      this.showIds = showIds;
    }

    public String prefix(String prefix) {
      if (showIds) {
        return prefix;
      }
      return "";
    }

    public boolean isMaximumLevel() {
      return toStringCallsCounter > MAX_DEEP;
    }

    public void incrementToStringCalls() {
      toStringCallsCounter++;
    }

    public boolean decrementToStringCalls() {
      toStringCallsCounter--;
      if (toStringCallsCounter < 0) {
        throw new RuntimeException("Too many removeContext [" + toStringCallsCounter + "].");
      }
      return toStringCallsCounter == 0;
    }

    public void deident() {
      identation -= step;
      if (identation < 0) {
        throw new RuntimeException("Too many deidents [" + identation + "].");
      }
    }

    public void ident() {
      identation += step;
    }

    public String find(Object key) {
      return objects.get(key(key));
    }

    public String save(Object object) {
      String value = objects.get(key(object));
      if (value != null) {
        return value;
      }
      String id = Integer.toString(objects.size());
      objects.put(key(object), "@" + id);
      return id;
    }

    public void callOriginalToString(Object object) {
      toStringForSelf.add(key(object));
    }

    public boolean isOriginalToStringCalled(Object object) {
      return toStringForSelf.contains(key(object));
    }

    public String getRowStart() {
      return all.substring(0, getIdentation());
    }

    public String getRowEnd() {
      return endOfLine;
    }

    private int getIdentation() {
      return Math.min(identation, all.length());
    }

    public boolean accept(String field) {
      return !excludes.contains("," + field + ",");
    }

    public boolean shouldIgnore(String field) {
      return ignores.contains("," + field + ",");
    }

    public int key(Object object) {
      // for identical strings we want to get the same hashCode. The system
      // identity hash code might generate
      // different values.
      if (object instanceof String) {
        return object.hashCode();
      }
      return System.identityHashCode(object);
    }

  }

  private static String reflectedToString(Object object, final boolean useToString, final MyStringStyle toStringStyle) {
    // if (object.toString().equals("[saml2p:Response: null]")) {
    // //
    // System.out.println(
    // getContext().isOriginalToStringCalled(object) + ":" +
    // getContext().find(object) + ":" + object);
    // }
    ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, toStringStyle, null, Object.class,
      toStringStyle.showTransients, toStringStyle.showStatics)
      {

        @Override
        protected boolean accept(Field f) {
          // if (this.hideStatic &&
          // java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
          // return false;
          // } else {
          if (f.getName().equals("stackTrace")) {
            return false;
          }
          if (f.getName().equals("cause") && f.getType().isAssignableFrom(Throwable.class)) {
            return false;
          }
          if (!getContext().accept(f.getName())) {
            return false;
          }
          return super.accept(f);
          // }
        }

        @Override
        public ToStringBuilder append(String fieldName, Object object) {
          String value = null;
          if (getContext().shouldIgnore(fieldName)) {
            value = IGNORED_VALUE;
          } else {
            value = internalToString(object, useToString, toStringStyle);
          }
          return super.append(fieldName, value);
        }
      };
    return builder.toString();
  }

  private static class MyStringStyle extends ToStringStyle {
    private static final long serialVersionUID = -3053031248321811775L;
    public final boolean showTransients;
    public final boolean showStatics;
    private final boolean useToString;
    private final boolean displayTypes;
    private boolean singleLine;

    public MyStringStyle(boolean singleLine, boolean useToString, boolean classDecorators, boolean displayTypes,
        boolean showTransients, boolean showStatics)
    {
      super();
      this.showTransients = showTransients;
      this.showStatics = showStatics;
      this.displayTypes = displayTypes;
      this.useToString = useToString;
      this.setUseShortClassName(false);
      this.setUseClassName(classDecorators);
      this.setUseIdentityHashCode(false);
      this.setFieldSeparatorAtEnd(false);
      this.singleLine = singleLine;
      if (singleLine) {
        this.setContentStart("[");
        this.setContentEnd("]");
      } else {
        this.setFieldSeparatorAtStart(true);
        this.setContentStart("");
        this.setContentEnd("");
      }
    }

    @Override
    public void appendStart(StringBuffer buffer, Object object) {
      super.appendStart(buffer, object);
      getContext().ident();
    }

    @Override
    public void appendEnd(StringBuffer buffer, Object object) {
      getContext().deident();
      if (singleLine) {
        super.appendEnd(buffer, object);
      }
    }

    @Override
    protected void appendFieldStart(StringBuffer buffer, String fieldName) {
      buffer.append(getContext().getRowEnd()).append(getContext().getRowStart());
      super.appendFieldStart(buffer, fieldName);
    }

    @Override
    protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
      // buffer.append(getContext().getRowEnd());
    }

    @Override
    protected void appendFieldSeparator(StringBuffer buffer) {
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
      if (useToString) {
        super.appendDetail(buffer, fieldName, (displayTypes ? value.getClass().getName() + ":" : "") + value);
      } else {
        super.appendDetail(buffer, fieldName,
          (displayTypes ? value.getClass().getName() + ":" : "") + ObjectUtils.toString(value));
      }
    }
  }

  private static String mapToString(Map<Object, Object> map, final boolean useToString,
      final MyStringStyle toStringStyle) {
    return collectionToDelimitedString(map.getClass(), map.entrySet(), "", "", "",
      new Mapper<Map.Entry<Object, Object>>()
        {
          @Override
          public String map(Entry<Object, Object> element) {
            Object key = element.getKey();
            String key2 = null;
            if (key != null) {
              key2 = key.toString();
            }
            if (getContext().shouldIgnore(key2)) {
              return key2 + "=" + IGNORED_VALUE;
            }
            return key2 + "=" + internalToString(element.getValue(), useToString, toStringStyle);
          }

          @Override
          public boolean accept(Entry<Object, Object> element) {
            if (element.getKey() == null) {
              return true;
            }
            return getContext().accept(element.getKey().toString());
          }
        });
  }

  private static String arrayToString(Object array, boolean useToString, MyStringStyle toStringStyle) {
    int length = Array.getLength(array);
    StringBuilder sb = new StringBuilder();
    sb.append("array[").append(array.getClass().getComponentType().getName()).append("]");
    getContext().ident();
    try {
      for (int i = 0; i < length; i++) {
        Object item = Array.get(array, i);
        sb.append(getContext().getRowEnd())
          .append(getContext().getRowStart())
          .append(internalToString(item, useToString, toStringStyle));
      }
      return sb.toString();
    } finally {
      getContext().deident();
    }
  }

  private static String collectionToString(Collection<Object> collection, final boolean useToString,
      final MyStringStyle toStringStyle) {
    return collectionToDelimitedString(collection.getClass(), collection, "", "", "", new Mapper<Object>()
      {
        @Override
        public String map(Object element) {
          return internalToString(element, useToString, toStringStyle);
        }

        @Override
        public boolean accept(Object element) {
          return true;
        }
      });
  }

  private static <T> String collectionToDelimitedString(Class clazz, Collection<T> coll, String delim,
      String elementPrefix, String elementSuffix, Mapper<T> mapper) {
    StringBuilder sb = new StringBuilder();
    sb.append(clazz.getName());
    getContext().ident();
    try {
      if (isEmpty(coll)) {
        return sb.toString();
      }
      Iterator<T> it = coll.iterator();
      while (it.hasNext()) {
        T entry = it.next();
        if (mapper.accept(entry)) {
          sb.append(getContext().getRowEnd())
            .append(getContext().getRowStart())
            .append(elementPrefix)
            .append(mapper.map(entry))
            .append(elementSuffix);
          if (it.hasNext()) {
            sb.append(delim);
          }
        }
      }
      return sb.toString();
    } finally {
      getContext().deident();
    }
  }

  // copied from org.springframework.util.CollectionUtils
  public static boolean isEmpty(Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }

  interface Mapper<T> {
    boolean accept(T element);

    String map(T element);
  }

  // TO STRING SPECIALS

  private static String throwableToString(Throwable throwable, boolean useToString, MyStringStyle toStringStyle) {
    return getFullStackTrace(throwable, useToString, toStringStyle);
  }

  private static String getFullStackTrace(Throwable throwable, boolean useToString, MyStringStyle toStringStyle) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    Throwable[] ts = getThrowables(throwable);
    for (Throwable element : ts) {
      _printStackTrace(element, pw, useToString, toStringStyle);
      if (isNestedThrowable(element)) {
        break;
      }
    }
    return sw.getBuffer().toString();
  }

  private static void _printStackTrace(Throwable t, PrintWriter printwriter, boolean useToString,
      MyStringStyle toStringStyle) {
    printwriter.print(reflectedToString(t, useToString, toStringStyle));
    printwriter.print(getContext().getRowEnd());
    getContext().ident();
    printwriter.print(getContext().getRowStart());
    printwriter.print("Stacktrace:");
    StackTraceElement astacktraceelement[] = t.getStackTrace();
    getContext().ident();
    for (StackTraceElement element : astacktraceelement) {
      printwriter.print(getContext().getRowEnd());
      printwriter.print(getContext().getRowStart());
      printwriter.print("at ");
      printwriter.print(element);
    }
    getContext().deident();
    getContext().deident();

    Throwable throwable = t.getCause();
    if (throwable != null) {
      _printStackTraceAsCause(throwable, printwriter, astacktraceelement, useToString, toStringStyle);
    }
  }

  private static void _printStackTraceAsCause(Throwable throwable, PrintWriter printstream,
      StackTraceElement[] astacktraceelement, boolean useToString, MyStringStyle toStringStyle) {
    StackTraceElement astacktraceelement1[] = throwable.getStackTrace();
    int i = astacktraceelement1.length - 1;
    for (int j = astacktraceelement.length - 1; i >= 0 && j >= 0
        && astacktraceelement1[i].equals(astacktraceelement[j]); j--) {
      i--;
    }

    int k = astacktraceelement1.length - 1 - i;
    printstream.print(getContext().getRowEnd());
    getContext().ident();
    printstream.print(getContext().getRowStart());
    printstream.print("Caused by: ");
    printstream.print(reflectedToString(throwable, useToString, toStringStyle));
    printstream.print(getContext().getRowEnd());
    getContext().ident();
    printstream.print(getContext().getRowStart());
    printstream.print("Stacktrace:");
    getContext().ident();
    for (int l = 0; l <= i; l++) {
      printstream.print(getContext().getRowEnd());
      printstream.print(getContext().getRowStart());
      printstream.print("at ");
      printstream.print(astacktraceelement1[l]);
    }

    if (k != 0) {
      printstream.print(getContext().getRowEnd());
      printstream.print(getContext().getRowStart());
      printstream.print("... ");
      printstream.print(k);
      printstream.print(" more");
    }
    getContext().deident();
    getContext().deident();
    getContext().deident();

    Throwable throwable2 = throwable.getCause();
    if (throwable2 != null) {
      _printStackTraceAsCause(throwable2, printstream, astacktraceelement1, useToString, toStringStyle);
    }
  }

  private static Throwable[] getThrowables(Throwable throwable) {
    List list = getThrowableList(throwable);
    return (Throwable[]) list.toArray(new Throwable[list.size()]);
  }

  private static List getThrowableList(Throwable throwable) {
    List list = new ArrayList();
    while (throwable != null && list.contains(throwable) == false) {
      list.add(throwable);
      throwable = ExceptionUtils.getCause(throwable);
    }
    return list;
  }

  private static boolean isNestedThrowable(Throwable throwable) {
    if (throwable == null) {
      return false;
    }

    if (throwable instanceof SQLException) {
      return true;
    } else if (throwable instanceof InvocationTargetException) {
      return true;
    } else if (isThrowableNested()) {
      return true;
    }

    Class cls = throwable.getClass();
    synchronized (CAUSE_METHOD_NAMES_LOCK) {
      for (String element : CAUSE_METHOD_NAMES) {
        try {
          Method method = cls.getMethod(element);
          if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            return true;
          }
        } catch (NoSuchMethodException ignored) {
          log.info("ignored", ignored);
        } catch (SecurityException ignored) {
          log.info("ignored", ignored);
        }
      }
    }

    try {
      Field field = cls.getField("detail");
      if (field != null) {
        return true;
      }
    } catch (NoSuchFieldException ignored) {
      log.info("ignored", ignored);
    } catch (SecurityException ignored) {
      log.info("ignored", ignored);
    }

    return false;
  }

  /**
   * <p>
   * The names of methods commonly used to access a wrapped exception.
   * </p>
   */
  private static String[] CAUSE_METHOD_NAMES = {
      "getCause",
      "getNextException",
      "getTargetException",
      "getException",
      "getSourceException",
      "getRootCause",
      "getCausedByException",
      "getNested",
      "getLinkedException",
      "getNestedException",
      "getLinkedCause",
      "getThrowable",
      // costin: added for batch sql exceptions
      "getNextException" };

  private static boolean isThrowableNested() {
    return THROWABLE_CAUSE_METHOD != null;
  }

  /**
   * <p>
   * The Method object for Java 1.4 getCause.
   * </p>
   */
  private static final Method THROWABLE_CAUSE_METHOD;

  /**
   * <p>
   * The Method object for Java 1.4 initCause.
   * </p>
   */
  private static final Method THROWABLE_INITCAUSE_METHOD;

  // Lock object for CAUSE_METHOD_NAMES
  private static final Object CAUSE_METHOD_NAMES_LOCK = new Object();
  static {
    Method causeMethod;
    try {
      causeMethod = Throwable.class.getMethod("getCause", (Class[]) null);
    } catch (Exception e) {
      log.info("ignored", e);
      causeMethod = null;
    }
    THROWABLE_CAUSE_METHOD = causeMethod;
    try {
      causeMethod = Throwable.class.getMethod("initCause", new Class[] { Throwable.class });
    } catch (Exception e) {
      log.info("ignored", e);
      causeMethod = null;
    }
    THROWABLE_INITCAUSE_METHOD = causeMethod;
  }
}
