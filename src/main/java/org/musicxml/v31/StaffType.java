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
 * <p>Java class for staff-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="staff-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ossia"/>
 *     &lt;enumeration value="cue"/>
 *     &lt;enumeration value="editorial"/>
 *     &lt;enumeration value="regular"/>
 *     &lt;enumeration value="alternate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "staff-type")
@XmlEnum
public enum StaffType {

    @XmlEnumValue("ossia")
    OSSIA("ossia"),
    @XmlEnumValue("cue")
    CUE("cue"),
    @XmlEnumValue("editorial")
    EDITORIAL("editorial"),
    @XmlEnumValue("regular")
    REGULAR("regular"),
    @XmlEnumValue("alternate")
    ALTERNATE("alternate");
    private final java.lang.String value;

    StaffType(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static StaffType fromValue(java.lang.String v) {
        for (StaffType c: StaffType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
