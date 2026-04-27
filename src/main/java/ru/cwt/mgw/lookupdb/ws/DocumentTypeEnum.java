
package ru.cwt.mgw.lookupdb.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="documentTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NATIONAL_ID"/>
 *     &lt;enumeration value="PASSPORT"/>
 *     &lt;enumeration value="MILITARY_ID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "documentTypeEnum")
@XmlEnum
public enum DocumentTypeEnum {

    NATIONAL_ID,
    PASSPORT,
    MILITARY_ID;

    public String value() {
        return name();
    }

    public static DocumentTypeEnum fromValue(String v) {
        return valueOf(v);
    }

}
