//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.06.15 at 09:15:47 PM CEST 
//


package org.musicxml.v31;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for glass-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="glass-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="glass harmonica"/>
 *     &lt;enumeration value="glass harp"/>
 *     &lt;enumeration value="wind chimes"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "glass-value")
@XmlEnum
public enum GlassValue {

    @XmlEnumValue("glass harmonica")
    GLASS_HARMONICA("glass harmonica"),
    @XmlEnumValue("glass harp")
    GLASS_HARP("glass harp"),
    @XmlEnumValue("wind chimes")
    WIND_CHIMES("wind chimes");
    private final java.lang.String value;

    GlassValue(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static GlassValue fromValue(java.lang.String v) {
        for (GlassValue c: GlassValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
