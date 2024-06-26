//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The figure type represents a single figure within a figured-bass element.
 * 
 * <p>Java class for figure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="figure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prefix" type="{}style-text" minOccurs="0"/>
 *         &lt;element name="figure-number" type="{}style-text" minOccurs="0"/>
 *         &lt;element name="suffix" type="{}style-text" minOccurs="0"/>
 *         &lt;element name="extend" type="{}extend" minOccurs="0"/>
 *         &lt;group ref="{}editorial"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "figure", propOrder = {
    "prefix",
    "figureNumber",
    "suffix",
    "extend",
    "footnote",
    "level"
})
public class Figure {

    protected StyleText prefix;
    @XmlElement(name = "figure-number")
    protected StyleText figureNumber;
    protected StyleText suffix;
    protected Extend extend;
    protected FormattedText footnote;
    protected Level level;

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible object is
     *     {@link StyleText }
     *     
     */
    public StyleText getPrefix() {
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link StyleText }
     *     
     */
    public void setPrefix(StyleText value) {
        this.prefix = value;
    }

    /**
     * Gets the value of the figureNumber property.
     * 
     * @return
     *     possible object is
     *     {@link StyleText }
     *     
     */
    public StyleText getFigureNumber() {
        return figureNumber;
    }

    /**
     * Sets the value of the figureNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link StyleText }
     *     
     */
    public void setFigureNumber(StyleText value) {
        this.figureNumber = value;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * @return
     *     possible object is
     *     {@link StyleText }
     *     
     */
    public StyleText getSuffix() {
        return suffix;
    }

    /**
     * Sets the value of the suffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link StyleText }
     *     
     */
    public void setSuffix(StyleText value) {
        this.suffix = value;
    }

    /**
     * Gets the value of the extend property.
     * 
     * @return
     *     possible object is
     *     {@link Extend }
     *     
     */
    public Extend getExtend() {
        return extend;
    }

    /**
     * Sets the value of the extend property.
     * 
     * @param value
     *     allowed object is
     *     {@link Extend }
     *     
     */
    public void setExtend(Extend value) {
        this.extend = value;
    }

    /**
     * Gets the value of the footnote property.
     * 
     * @return
     *     possible object is
     *     {@link FormattedText }
     *     
     */
    public FormattedText getFootnote() {
        return footnote;
    }

    /**
     * Sets the value of the footnote property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormattedText }
     *     
     */
    public void setFootnote(FormattedText value) {
        this.footnote = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link Level }
     *     
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link Level }
     *     
     */
    public void setLevel(Level value) {
        this.level = value;
    }

}
