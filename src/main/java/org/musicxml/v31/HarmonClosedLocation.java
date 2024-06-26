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
 * <p>Java class for harmon-closed-location.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="harmon-closed-location">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="right"/>
 *     &lt;enumeration value="bottom"/>
 *     &lt;enumeration value="left"/>
 *     &lt;enumeration value="top"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "harmon-closed-location")
@XmlEnum
public enum HarmonClosedLocation {

    @XmlEnumValue("right")
    RIGHT("right"),
    @XmlEnumValue("bottom")
    BOTTOM("bottom"),
    @XmlEnumValue("left")
    LEFT("left"),
    @XmlEnumValue("top")
    TOP("top");
    private final java.lang.String value;

    HarmonClosedLocation(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static HarmonClosedLocation fromValue(java.lang.String v) {
        for (HarmonClosedLocation c: HarmonClosedLocation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
