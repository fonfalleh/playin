//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * The glass type represents pictograms for glass percussion instruments. The smufl attribute is used to distinguish different SMuFL glyphs for wind chimes in the chimes pictograms range, including those made of materials other than glass.
 * 
 * <p>Java class for glass complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="glass">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;>glass-value">
 *       &lt;attribute name="smufl" type="{}smufl-pictogram-glyph-name" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "glass", propOrder = {
    "value"
})
public class Glass {

    @XmlValue
    protected GlassValue value;
    @XmlAttribute(name = "smufl")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected java.lang.String smufl;

    /**
     * The glass-value type represents pictograms for glass percussion instruments.
     * 
     * @return
     *     possible object is
     *     {@link GlassValue }
     *     
     */
    public GlassValue getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlassValue }
     *     
     */
    public void setValue(GlassValue value) {
        this.value = value;
    }

    /**
     * Gets the value of the smufl property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSmufl() {
        return smufl;
    }

    /**
     * Sets the value of the smufl property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSmufl(java.lang.String value) {
        this.smufl = value;
    }

}
