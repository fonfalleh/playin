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
 * <p>Java class for start-note.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="start-note">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="upper"/>
 *     &lt;enumeration value="main"/>
 *     &lt;enumeration value="below"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "start-note")
@XmlEnum
public enum StartNote {

    @XmlEnumValue("upper")
    UPPER("upper"),
    @XmlEnumValue("main")
    MAIN("main"),
    @XmlEnumValue("below")
    BELOW("below");
    private final java.lang.String value;

    StartNote(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static StartNote fromValue(java.lang.String v) {
        for (StartNote c: StartNote.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
