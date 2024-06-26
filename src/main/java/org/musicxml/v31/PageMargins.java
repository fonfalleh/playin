//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Page margins are specified either for both even and odd pages, or via separate odd and even page number values. The type attribute is not needed when used as part of a print element. If omitted when the page-margins type is used in the defaults element, "both" is the default value.
 * 
 * <p>Java class for page-margins complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="page-margins">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}all-margins"/>
 *       &lt;attribute name="type" type="{}margin-type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "page-margins", propOrder = {
    "leftMargin",
    "rightMargin",
    "topMargin",
    "bottomMargin"
})
public class PageMargins {

    @XmlElement(name = "left-margin", required = true)
    protected BigDecimal leftMargin;
    @XmlElement(name = "right-margin", required = true)
    protected BigDecimal rightMargin;
    @XmlElement(name = "top-margin", required = true)
    protected BigDecimal topMargin;
    @XmlElement(name = "bottom-margin", required = true)
    protected BigDecimal bottomMargin;
    @XmlAttribute(name = "type")
    protected MarginType type;

    /**
     * Gets the value of the leftMargin property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLeftMargin() {
        return leftMargin;
    }

    /**
     * Sets the value of the leftMargin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLeftMargin(BigDecimal value) {
        this.leftMargin = value;
    }

    /**
     * Gets the value of the rightMargin property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRightMargin() {
        return rightMargin;
    }

    /**
     * Sets the value of the rightMargin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRightMargin(BigDecimal value) {
        this.rightMargin = value;
    }

    /**
     * Gets the value of the topMargin property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTopMargin() {
        return topMargin;
    }

    /**
     * Sets the value of the topMargin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTopMargin(BigDecimal value) {
        this.topMargin = value;
    }

    /**
     * Gets the value of the bottomMargin property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBottomMargin() {
        return bottomMargin;
    }

    /**
     * Sets the value of the bottomMargin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBottomMargin(BigDecimal value) {
        this.bottomMargin = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link MarginType }
     *     
     */
    public MarginType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link MarginType }
     *     
     */
    public void setType(MarginType value) {
        this.type = value;
    }

}
