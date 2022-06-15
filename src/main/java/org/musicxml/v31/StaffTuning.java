//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * The staff-tuning type specifies the open, non-capo tuning of the lines on a tablature staff.
 * 
 * <p>Java class for staff-tuning complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="staff-tuning">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}tuning"/>
 *       &lt;attribute name="line" type="{}staff-line" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "staff-tuning", propOrder = {
    "tuningStep",
    "tuningAlter",
    "tuningOctave"
})
public class StaffTuning {

    @XmlElement(name = "tuning-step", required = true)
    @XmlSchemaType(name = "string")
    protected Step tuningStep;
    @XmlElement(name = "tuning-alter")
    protected BigDecimal tuningAlter;
    @XmlElement(name = "tuning-octave")
    @XmlSchemaType(name = "integer")
    protected int tuningOctave;
    @XmlAttribute(name = "line")
    protected BigInteger line;

    /**
     * Gets the value of the tuningStep property.
     * 
     * @return
     *     possible object is
     *     {@link Step }
     *     
     */
    public Step getTuningStep() {
        return tuningStep;
    }

    /**
     * Sets the value of the tuningStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link Step }
     *     
     */
    public void setTuningStep(Step value) {
        this.tuningStep = value;
    }

    /**
     * Gets the value of the tuningAlter property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTuningAlter() {
        return tuningAlter;
    }

    /**
     * Sets the value of the tuningAlter property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTuningAlter(BigDecimal value) {
        this.tuningAlter = value;
    }

    /**
     * Gets the value of the tuningOctave property.
     * 
     */
    public int getTuningOctave() {
        return tuningOctave;
    }

    /**
     * Sets the value of the tuningOctave property.
     * 
     */
    public void setTuningOctave(int value) {
        this.tuningOctave = value;
    }

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLine(BigInteger value) {
        this.line = value;
    }

}
