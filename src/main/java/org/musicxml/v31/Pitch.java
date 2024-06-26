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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Pitch is represented as a combination of the step of the diatonic scale, the chromatic alteration, and the octave.
 * 
 * <p>Java class for pitch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pitch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="step" type="{}step"/>
 *         &lt;element name="alter" type="{}semitones" minOccurs="0"/>
 *         &lt;element name="octave" type="{}octave"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pitch", propOrder = {
    "step",
    "alter",
    "octave"
})
public class Pitch {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Step step;
    protected BigDecimal alter;
    @XmlSchemaType(name = "integer")
    protected int octave;

    /**
     * Gets the value of the step property.
     * 
     * @return
     *     possible object is
     *     {@link Step }
     *     
     */
    public Step getStep() {
        return step;
    }

    /**
     * Sets the value of the step property.
     * 
     * @param value
     *     allowed object is
     *     {@link Step }
     *     
     */
    public void setStep(Step value) {
        this.step = value;
    }

    /**
     * Gets the value of the alter property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAlter() {
        return alter;
    }

    /**
     * Sets the value of the alter property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAlter(BigDecimal value) {
        this.alter = value;
    }

    /**
     * Gets the value of the octave property.
     * 
     */
    public int getOctave() {
        return octave;
    }

    /**
     * Sets the value of the octave property.
     * 
     */
    public void setOctave(int value) {
        this.octave = value;
    }

}
