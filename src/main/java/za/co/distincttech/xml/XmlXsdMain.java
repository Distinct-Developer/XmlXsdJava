package za.co.distincttech.xml;

import jakarta.xml.bind.*;
import org.apache.poi.ss.usermodel.*;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main entry point to demonstrate the XML and XSD functionalities.
 *
 * @author Boiki Mphore
 * @since 12 August 2026
 * */
public class XmlXsdMain {

  public static void main(String[] args) {
    try {
      final String xsdSchemaFile = "book.xsd";
      final String xmlFile = "book.xml";
      final String excelFile = "book_list.xlsx";
      final String excelFileWithFormulas = "formula_excel.xlsx";

      generateXsdSchemaFromObject(xsdSchemaFile);

      boolean isXmlValid = validateXMLSchema(xsdSchemaFile, xmlFile);
      String validationMessage = String.format(">>> Is the file %s valid? [%s]", xmlFile, isXmlValid);
      System.out.println(validationMessage.concat("\n"));

      unmarshalXmlFile(xmlFile);

      Book generatedBook = new Book("Generated Book", "Generated Author", 2026, BigDecimal.valueOf(200.00).setScale(2, BigDecimal.ROUND_HALF_UP));
      generateXmlFileFromObjectAndXsd(generatedBook, xsdSchemaFile, "generatedBook.xml");

      List<Book> booksFromExcelFile = readBooksFromExcel(excelFile);
      String excelBooksMessage = String.format(">>> '[%s]' has a total of %d books", excelFile, booksFromExcelFile.size());
      System.out.println(excelBooksMessage.concat("\n"));

      // For fun
      readDataFromExcelFileAndPrintTheValues(excelFileWithFormulas);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Generates an XSD schema from the {@link Book} object.
   * @throws Exception If an unknown exception occurs.
   *
   * @author Boiki Mphore
   * @since 12 August 2026
   * */
  static void generateXsdSchemaFromObject(String xsdSchemaFile) throws Exception {
    try {
      String filePath;
      // 1. Initialize the JAXBContext for your target class
      JAXBContext context = JAXBContext.newInstance(Book.class);

      // 2. Tell JAXB to generate the schema
      context.generateSchema(new SchemaOutputResolver() {
        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) {
          // 3. Define where the XSD file will be saved
          File file = new File(xsdSchemaFile);
          StreamResult result = new StreamResult(file);

          // JAXB requires a System ID (a URI) to resolve relative paths
          result.setSystemId(file.toURI().toString());

          String xsdMessage = String.format(">>> The XSD file has been created at [%s]", file.getAbsolutePath());
          System.out.println(xsdMessage.concat("\n"));

          return result;
        }
      });

    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  /**
   * Validates an XML file against an XSD schema.
   * @param xsdSchemaFile The XSD schema to use for validation.
   * @param xmlFile The XML file to be validated.
   * @return true if and only if the XML file has been validated successfully.
   * @throws SAXException For schema violations or malformed XML.
   * @throws IOException If a file is missing or has permission issues.
   *
   * @author Boiki Mphore
   * @since 12 August 2026
   * */
  static boolean validateXMLSchema(String xsdSchemaFile, String xmlFile) throws SAXException, IOException {
    try {
      // 1. Create a SchemaFactory capable of understanding W3C schemas
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // 2. Load the XSD file
      Schema schema = factory.newSchema(new File(xsdSchemaFile));

      // 3. Create a Validator instance
      Validator validator = schema.newValidator();

      // 4. Validate the XML file
      validator.validate(new StreamSource(new File(xmlFile)));

      return true;

    } catch (SAXException e) {
      // Catches schema violations and badly formed XML
      throw new SAXException(e);
    } catch (IOException e) {
      // Catches missing files or permission issues
      throw new IOException(e);
    }
  }

  /**
   * Unmarshalls an XML file.
   * @param xmlFile The file to be unmarshalled.
   * @throws JAXBException If there is a problem reading the XML into an object.
   *
   * @author Boiki Mphore
   * @since 12 August 2026
   * */
  static void unmarshalXmlFile(String xmlFile) throws JAXBException {
    try {
      // 1. Create a JAXBContext targeting your generated class
      JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);

      // 2. Create the Unmarshaller
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

      // 3. Deserialize/Unmarshal the XML file into the Book object
      Book book = (Book) unmarshaller.unmarshal(new File(xmlFile));

      // 4. Use the standard getter methods
      System.out.println(">>> '"+ xmlFile +"' unmarshalled as Book object...");
      System.out.println("Title:  " + book.getTitle());
      System.out.println("Author: " + book.getAuthor());
      System.out.println("Year:   " + book.getYear());    // Returned as primitive int
      System.out.println("Price:  $" + book.getPrice());  // Returned as BigDecimal

    } catch (JAXBException e) {
      throw new JAXBException(e);
    }
  }

  /**
   * Generated an XML file from an object, using the specified XSD schema file.
   * @param book The {@link Book} object to be used to generate the XML file.
   * @param xsdSchemaFile The XSD schema to be used for marshalling the {@link Book} and xml file.
   * @param xmlFileToGenerate The generated XML file.
   * @throws JAXBException If the XML file generation fails.
   * @throws Exception If any other unknown exception occurs.
   *
   * @author Boiki Mphore
   * @since 12 August 2026
   * */
  static void generateXmlFileFromObjectAndXsd(Book book, String xsdSchemaFile, String xmlFileToGenerate) throws Exception {
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

      String xmlFileMessage = String.format("\n>>> The XML file has been created at [%s]", xmlFile.getAbsolutePath());
      System.out.println(xmlFileMessage);

      // (Optional) We can print it to the console as well, so we can see it
      // marshaller.marshal(book, System.out);

    } catch (JAXBException e) {
      throw new JAXBException(e);
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  /**
   * Reads an Excel file and write the contents to an ArrayList.
   * @param excelFileToRead The Excel file to be read.
   * @return A list of {@link Book} objects that were read from the Excel file.
   * @throws IOException If there is a problem reading from the specified Excel file.
   *
   * @author Boiki Mphore
   * @since 13 August 2026
   * */
  static List<Book> readBooksFromExcel(String excelFileToRead) throws IOException {
    List<Book> bookList = new ArrayList<>();
    DataFormatter dataFormatter = new DataFormatter();

    try (FileInputStream inputStream = new FileInputStream(excelFileToRead);
         Workbook workbook = WorkbookFactory.create(inputStream)) {

      // Assuming data is on the first sheet
      Sheet sheet = workbook.getSheetAt(0);

      // Iterate through the rows
      for (Row row : sheet) {
        // Skip the header row (assuming row 0 contains headers like Title, Author, etc.)
        if (row.getRowNum() == 0) {
          continue;
        }

        // Safely extract cell values as Strings using DataFormatter
        String title = dataFormatter.formatCellValue(row.getCell(0));
        String author = dataFormatter.formatCellValue(row.getCell(1));
        String yearString = dataFormatter.formatCellValue(row.getCell(2));
        String priceString = dataFormatter.formatCellValue(row.getCell(3));

        // Skip completely empty rows
        if (title.isEmpty() && author.isEmpty()) {
          continue;
        }

        try {
          // Parse numeric fields
          int year = Integer.parseInt(yearString);
          BigDecimal price = new BigDecimal(priceString).setScale(2, BigDecimal.ROUND_HALF_UP);

          // Create Book object and add to list
          Book book = new Book(title, author, year, price);
          bookList.add(book);

        } catch (NumberFormatException e) {
          throw new NullPointerException("Invalid number format in row " + (row.getRowNum() + 1));
        }
      }

    } catch (IOException e) {
      throw new IOException(e);
    }

    return bookList;
  }

  /**
   * Reads an Excel file and print the contents to the console.
   * @param excelFileToRead The Excel file to be read.
   *
   * @author Boiki Mphore
   * @since 13 August 2026
   * */
  static void readDataFromExcelFileAndPrintTheValues(String excelFileToRead) throws IOException {
    // Use try-with-resources to ensure the file stream and workbook are closed
    try (FileInputStream inputStream = new FileInputStream(excelFileToRead);
         Workbook workbook = WorkbookFactory.create(inputStream)) {

      // Assuming there is only one sheet, get the first sheet (index 0)
      Sheet sheet = workbook.getSheetAt(0);

      // Iterate through each row in the sheet
      for (Row row : sheet) {
        // Iterate through each cell in the row
        for (Cell cell : row) {
          // Check the cell type and format accordingly
          switch (cell.getCellType()) {
            case STRING:
              System.out.print(cell.getStringCellValue() + "\t\t");
              break;
            case NUMERIC:
              // Note: Dates are also stored as numeric values in Excel
              if (DateUtil.isCellDateFormatted(cell)) {
                System.out.print(cell.getDateCellValue() + "\t\t");
              } else {
                System.out.print(cell.getNumericCellValue() + "\t\t");
              }
              break;
            case BOOLEAN:
              System.out.print(cell.getBooleanCellValue() + "\t\t");
              break;
            case FORMULA:
              System.out.print(cell.getCellFormula() + "\t\t");
              break;
            case BLANK:
              System.out.print("[BLANK]\t\t");
              break;
            default:
              System.out.print("[UNKNOWN]\t\t");
          }
        }
        System.out.println(); // Move to the next line after finishing a row
      }
    } catch (IOException e) {
      throw new IOException(e);
    }
  }
}
