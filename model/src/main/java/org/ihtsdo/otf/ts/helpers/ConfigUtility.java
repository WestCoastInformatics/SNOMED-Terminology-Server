package org.ihtsdo.otf.ts.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Loads and serves configuration.
 */
public class ConfigUtility {

  /** The date format. */
  public final static FastDateFormat DATE_FORMAT = FastDateFormat
      .getInstance("yyyyMMdd");

  /** The config. */
  public static Properties config = null;

  /** The transformer for DOM -> XML. */
  private static Transformer transformer;

  /** The date format. */
  public final static FastDateFormat format = FastDateFormat
      .getInstance("yyyyMMdd");

  static {
    try {
      TransformerFactory factory = TransformerFactory.newInstance();
      transformer = factory.newTransformer();
      // Indent output.
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
          "{http://xml.apache.org/xslt}indent-amount", "4");
      // Skip XML declaration header.
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Indicates whether or not the server is active.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public static boolean isServerActive() throws Exception {
    if (config == null)
      config = ConfigUtility.getConfigProperties();

    try {
      // Attempt to logout to verify service is up (this works like a "ping").
      Client client = Client.create();
      WebResource resource =
          client.resource(config.getProperty("base.url")
              + "/security/logout/dummy");
      resource.accept(MediaType.APPLICATION_JSON);
      ClientResponse response = resource.get(ClientResponse.class);
      if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the config properties.
   * @return the config properties
   *
   * @throws Exception the exception
   */
  public static Properties getConfigProperties() throws Exception {
    if (config == null) {
      String configFileName = System.getProperty("run.config.ts");
      Logger.getLogger(ConfigUtility.class.getName()).info(
          "  run.config.ts = " + configFileName);
      config = new Properties();
      FileReader in = new FileReader(new File(configFileName));
      config.load(in);
      in.close();
      Logger.getLogger(ConfigUtility.class).info("  properties = " + config);
    }
    return config;
  }

  /**
   * New handler instance.
   *
   * @param handler the handler
   * @param handlerClass the handler class
   * @param type the type
   * @return the object
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public static <T> T newHandlerInstance(String handler, String handlerClass,
    Class<T> type) throws Exception {
    if (handlerClass == null) {
      throw new Exception("Handler class " + handlerClass + " is not defined");
    }
    Class<?> toInstantiate = Class.forName(handlerClass);
    if (toInstantiate == null) {
      throw new Exception("Unable to find class " + handlerClass);
    }
    Object o = null;
    try {
      o = toInstantiate.newInstance();
    } catch (Exception e) {
      // do nothing
    }
    if (o == null) {
      throw new Exception("Unable to instantiate class " + handlerClass
          + ", check for default constructor.");
    }
    if (type.isAssignableFrom(o.getClass())) {
      return (T) o;
    }
    throw new Exception("Handler is not assignable from " + type.getName());
  }

  /**
   * Instantiates a handler using standard setup and configures it with
   * properties.
   *
   * @param <T> the
   * @param property the property
   * @param handlerName the handler name
   * @param type the type
   * @return the t
   * @throws Exception
   */
  public static <T extends Configurable> T newStandardHandlerInstanceWithConfiguration(
    String property, String handlerName, Class<T> type) throws Exception {

    // Instantiate the handler
    // property = "metadata.service.handler" (e.g)
    // handlerName = "SNOMED" (e.g.)
    String classKey = property + "." + handlerName + ".class";
    if (config.getProperty(classKey) == null) {
      throw new Exception("Unexpected null classkey " + classKey);
    }
    String handlerClass = config.getProperty(classKey);
    Logger.getLogger(ConfigUtility.class).info("Instantiate " + handlerClass);
    T handler =
        ConfigUtility.newHandlerInstance(handlerName, handlerClass, type);

    // Look up and build properties
    Properties handlerProperties = new Properties();
    handlerProperties.setProperty("security.handler", handlerName);

    for (Object key : config.keySet()) {
      // Find properties like "metadata.service.handler.SNOMED.class"
      if (key.toString().startsWith(property + "." + handlerName + ".")) {
        String shortKey =
            key.toString().substring(
                (property + "." + handlerName + ".").length());
        Logger.getLogger(ConfigUtility.class).info(
            " property " + shortKey + " = "
                + config.getProperty(key.toString()));
        handlerProperties.put(shortKey, config.getProperty(key.toString()));
      }
    }
    handler.setProperties(handlerProperties);
    return handler;
  }

  /**
   * Returns the graph for string.
   *
   * @param xml the xml
   * @param graphClass the graph class
   * @return the graph for string
   * @throws JAXBException the JAXB exception
   */
  public static Object getGraphForString(String xml, Class<?> graphClass)
    throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(graphClass);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    return unmarshaller.unmarshal(new StreamSource(new StringReader(xml)));
  }

  /**
   * Returns the graph for file.
   *
   * @param file the file
   * @param graphClass the graph class
   * @return the graph for file
   * @throws FileNotFoundException the file not found exception
   * @throws JAXBException the JAXB exception
   */
  @SuppressWarnings("resource")
  public static Object getGraphForFile(File file, Class<?> graphClass)
    throws FileNotFoundException, JAXBException {
    return getGraphForString(new Scanner(file, "UTF-8").useDelimiter("\\A")
        .next(), graphClass);
  }

  /**
   * Returns the graph for stream.
   *
   * @param in the in
   * @param graphClass the graph class
   * @return the graph for stream
   * @throws FileNotFoundException the file not found exception
   * @throws JAXBException the JAXB exception
   */
  @SuppressWarnings("resource")
  public static Object getGraphForStream(InputStream in, Class<?> graphClass)
    throws FileNotFoundException, JAXBException {
    return getGraphForString(new Scanner(in, "UTF-8").useDelimiter("\\A")
        .next(), graphClass);
  }

  /**
   * Returns the XML string for for graph object.
   *
   * @param object the object
   * @return the string for for graph
   * @throws JAXBException the JAXB exception
   */
  public static String getStringForGraph(Object object) throws JAXBException {
    StringWriter writer = new StringWriter();
    JAXBContext jaxbContext = null;
    jaxbContext = JAXBContext.newInstance(object.getClass());
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    jaxbMarshaller.marshal(object, writer);
    return writer.toString();
  }

  /**
   * Returns the node for string.
   *
   * @param xml the xml
   * @return the node for string
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException the SAX exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static Node getNodeForString(String xml)
    throws ParserConfigurationException, SAXException, IOException {

    InputStream in =
        new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    // Parse XML file.
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(in);
    Node rootNode = document.getFirstChild();
    return rootNode;
  }

  /**
   * Returns the node for file.
   *
   * @param file the file
   * @return the node for file
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException the SAX exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static Node getNodeForFile(File file)
    throws ParserConfigurationException, SAXException, IOException {
    InputStream in = new FileInputStream(file);
    // Parse XML file.
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(in);
    Node rootNode = document.getFirstChild();
    in.close();
    return rootNode;
  }

  /**
   * Returns the string for node.
   *
   * @param root the root node
   * @return the string for node
   * @throws TransformerException the transformer exception
   * @throws ParserConfigurationException the parser configuration exception
   */
  public static String getStringForNode(Node root) throws TransformerException,
    ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();
    document.appendChild(document.importNode(root, true));
    DOMSource source = new DOMSource(document);
    StringWriter out = new StringWriter();
    StreamResult result = new StreamResult(out);
    transformer.transform(source, result);
    return out.toString();
  }

  /**
   * Returns the graph for node.
   *
   * @param node the node
   * @param graphClass the graph class
   * @return the graph for node
   * @throws JAXBException the JAXB exception
   * @throws TransformerException the transformer exception
   * @throws ParserConfigurationException the parser configuration exception
   */
  public static Object getGraphForNode(Node node, Class<?> graphClass)
    throws JAXBException, TransformerException, ParserConfigurationException {
    return getGraphForString(getStringForNode(node), graphClass);
  }

  /**
   * Returns the node for graph.
   *
   * @param object the object
   * @return the node for graph
   * @throws ParserConfigurationException the parser configuration exception
   * @throws SAXException the SAX exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws JAXBException the JAXB exception
   */
  public static Node getNodeForGraph(Object object)
    throws ParserConfigurationException, SAXException, IOException,
    JAXBException {
    return getNodeForString(getStringForGraph(object));
  }

  /**
   * Pretty format.
   *
   * @param input the input
   * @param indent the indent
   * @return the string
   */
  public static String prettyFormat(String input, int indent) {
    try {
      Source xmlInput = new StreamSource(new StringReader(input));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", indent);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (Exception e) {
      throw new RuntimeException(e); // simple exception handling, please review
                                     // it
    }
  }

  /**
   * Merge-sort two files.
   * 
   * @param files1 the first set of files
   * @param files2 the second set of files
   * @param comp the comparator
   * @param dir the sort dir
   * @param headerLine the header_line
   * @return the sorted {@link File}
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static File mergeSortedFiles(File files1, File files2,
    Comparator<String> comp, File dir, String headerLine) throws IOException {

    final BufferedReader in1 = new BufferedReader(new FileReader(files1));
    final BufferedReader in2 = new BufferedReader(new FileReader(files2));
    final File outFile = File.createTempFile("t+~", ".tmp", dir);
    final BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
    String line1 = in1.readLine();
    String line2 = in2.readLine();
    String line = null;
    if (!headerLine.isEmpty()) {
      line = headerLine;
      out.write(line);
      out.newLine();
    }
    while (line1 != null || line2 != null) {
      if (line1 == null) {
        line = line2;
        line2 = in2.readLine();
      } else if (line2 == null) {
        line = line1;
        line1 = in1.readLine();
      } else if (comp.compare(line1, line2) < 0) {
        line = line1;
        line1 = in1.readLine();
      } else {
        line = line2;
        line2 = in2.readLine();
      }
      // if a header line, do not write
      if (!line.startsWith("id")) {
        out.write(line);
        out.newLine();
      }
    }
    out.flush();
    out.close();
    in1.close();
    in2.close();
    return outFile;
  }

  /**
   * Delete directory.
   *
   * @param path the path
   * @return true, if successful
   */
  static public boolean deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteDirectory(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return (path.delete());
  }

  /**
   * Sends email.
   *
   * @param subject the subject
   * @param from the from
   * @param recipients the recipients
   * @param body the body
   * @param details the details
   * @param authFlag the auth flag
   * @throws Exception the exception
   */
  public static void sendEmail(String subject, String from, String recipients,
    String body, Properties details, boolean authFlag) throws Exception {
    // avoid sending mail if disabled
    if ("false".equals(details.getProperty("mail.enabled"))) {
      // do nothing
      return;
    }
    Session session = null;
    if (authFlag) {
      Authenticator auth = new SMTPAuthenticator();
      session = Session.getInstance(details, auth);
    } else {
      session = Session.getInstance(details);
    }

    MimeMessage msg = new MimeMessage(session);
    msg.setText(body.toString());
    msg.setSubject(subject);
    msg.setFrom(new InternetAddress(from));
    String[] recipientsArray = recipients.split(";");
    for (String recipient : recipientsArray) {
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    }
    Transport.send(msg);
  }

  /**
   * SMTPAuthenticator.
   */
  public static class SMTPAuthenticator extends javax.mail.Authenticator {

    /**
     * Instantiates an empty {@link SMTPAuthenticator}.
     */
    public SMTPAuthenticator() {
      // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      Properties config = null;
      try {
        config = ConfigUtility.getConfigProperties();
      } catch (Exception e) {
        // do nothing
      }
      if (config == null) {
        return null;
      } else {
        return new PasswordAuthentication(config.getProperty("mail.smtp.user"),
            config.getProperty("mail.smtp.password"));
      }
    }
  }
  
}
