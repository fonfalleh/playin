//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The beat-unit-tied type indicates a beat-unit within a metronome mark that is tied to the preceding beat-unit. This allows or two or more tied notes to be associated with a per-minute value in a metronome mark, whereas the metronome-tied element is restricted to metric relationship marks.
 * 
 * <p>Java class for beat-unit-tied complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="beat-unit-tied">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}beat-unit"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "beat-unit-tied", propOrder = {
    "beatUnit",
    "beatUnitDot"
})
public class BeatUnitTied {

    @XmlElement(name = "beat-unit", required = true)
    protected java.lang.String beatUnit;
    @XmlElement(name = "beat-unit-dot")
    protected List<Empty> beatUnitDot;

    /**
     * Gets the value of the beatUnit property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getBeatUnit() {
        return beatUnit;
    }

    /**
     * Sets the value of the beatUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setBeatUnit(java.lang.String value) {
        this.beatUnit = value;
    }

    /**
     * Gets the value of the beatUnitDot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the beatUnitDot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBeatUnitDot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Empty }
     * 
     * 
     */
    public List<Empty> getBeatUnitDot() {
        if (beatUnitDot == null) {
            beatUnitDot = new ArrayList<Empty>();
        }
        return this.beatUnitDot;
    }

}
