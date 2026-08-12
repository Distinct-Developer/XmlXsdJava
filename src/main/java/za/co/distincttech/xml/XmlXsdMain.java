package za.co.distincttech.xml;

import jakarta.xml.bind.*;
import org.xml.sax.SAXException;
import za.co.distincttech.xml.models.Book;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

public class XmlXsdMain {

  public static void main(String[] args) {
    final String xsdSchemaFile = "book.xsd";
    final String xmlFile = "book.xml";

    generateXsdSchemaFromObject();
    validateXMLSchema(xsdSchemaFile, xmlFile);
    unmarshalXmlFile(xmlFile);

    Book generatedBook = new Book("Generated Book", "Generated Author", 2026, BigDecimal.valueOf(200.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    generateXmlFileFromObjectAndXsd(generatedBook, xsdSchemaFile, "generatedBook.xml");
  }

  static void generateXsdSchemaFromObject()
  {
    try {
      // 1. Initialize the JAXBContext for your target class
      JAXBContext context = JAXBContext.newInstance(Book.class);

      // 2. Tell JAXB to generate the schema
      context.generateSchema(new SchemaOutputResolver() {
        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) {
          // 3. Define where the XSD file will be saved
          File file = new File("book.xsd");
          StreamResult result = new StreamResult(file);

          // JAXB requires a System ID (a URI) to resolve relative paths
          result.setSystemId(file.toURI().toString());

          return result;
        }
      });

      System.out.println("XSD file generated.\n");

    } catch (Exception e) {
      System.err.println("Error generating XSD: " + e.getMessage());
      e.printStackTrace();
    }
  }

  static boolean validateXMLSchema(String xsdSchemaFile, String xmlFile) {
    try {
      // 1. Create a SchemaFactory capable of understanding W3C schemas
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // 2. Load the XSD file
      Schema schema = factory.newSchema(new File(xsdSchemaFile));

      // 3. Create a Validator instance
      Validator validator = schema.newValidator();

      // 4. Validate the XML file
      validator.validate(new StreamSource(new File(xmlFile)));

      System.out.println("Success: '" + xmlFile + "' is valid!\n");
      return true;

    } catch (SAXException e) {
      // Catches schema violations and badly formed XML
      System.out.println("XML validation Error: " + e.getMessage());
      return false;
    } catch (IOException e) {
      // Catches missing files or permission issues
      System.out.println("I/O Error: " + e.getMessage());
      return false;
    }
  }

  static void unmarshalXmlFile(String xmlFile) {
    try {
      // 1. Create a JAXBContext targeting your generated class
      JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);

      // 2. Create the Unmarshaller
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

      // 3. Deserialize/Unmarshal the XML file into the Book object
      Book book = (Book) unmarshaller.unmarshal(new File(xmlFile));

      // 4. Use the standard getter methods
      System.out.println("'"+ xmlFile +"' unmarshalled as Book object!\n");
      System.out.println("Title:  " + book.getTitle());
      System.out.println("Author: " + book.getAuthor());
      System.out.println("Year:   " + book.getYear());    // Returned as primitive int
      System.out.println("Price:  $" + book.getPrice());  // Returned as BigDecimal

    } catch (JAXBException e) {
      System.err.println("Error reading XML into Java object: " + e.getMessage());
      e.printStackTrace();
    }
  }

  static void generateXmlFileFromObjectAndXsd(Book book, String xsdSchemaFile, String xmlFileToGenerate) {
    try {
      // 1. Initialize the JAXBContext
      JAXBContext context = JAXBContext.newInstance(Book.class);
      Marshaller marshaller = context.createMarshaller();

      // 2. Format the XML output so it is indented and readable
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      // 3. Attach the XSD for validation (Optional, but recommended)
      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = sf.newSchema(new File(xsdSchemaFile));
      marshaller.setSchema(schema);

      // 4. Marshal (convert) the Java object into an XML file
      File xmlFile = new File(xmlFileToGenerate);
      marshaller.marshal(book, xmlFile);

      System.out.println("Success! XML written to: " + xmlFile.getAbsolutePath());

      // (Optional) Print it to the console as well so you can see it
      // marshaller.marshal(book, System.out);

    } catch (JAXBException e) {
      System.err.println("JAXB/Validation Error: The Java object data violates the XSD rules!");
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("General Error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
