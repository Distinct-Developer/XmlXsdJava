package za.co.distincttech.xml.models;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Book object to marshall and unmarshall as XML and validate with an XSD schema.
 *
 * @author Boiki Mphore
 * @since 12 August 2026
 * */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "book")
public class Book {

  @XmlElement(required = true)
  protected String title;

  @XmlElement(required = true)
  protected String author;

  protected int year;

  @XmlElement(required = true)
  protected BigDecimal price;

  public Book() {
  }

  public Book(String title, String author, int year, BigDecimal price) {
    this.title = title;
    this.author = author;
    this.year = year;
    this.price = price;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }
}
