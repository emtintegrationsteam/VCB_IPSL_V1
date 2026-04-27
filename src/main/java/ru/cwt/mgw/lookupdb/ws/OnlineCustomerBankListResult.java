
package ru.cwt.mgw.lookupdb.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for onlineCustomerBankListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="onlineCustomerBankListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="bank" type="{http://ws.lookupdb.mgw.cwt.ru/}onlineCustomerBank" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "onlineCustomerBankListResult", propOrder = {
    "destName",
    "bankList"
})
public class OnlineCustomerBankListResult {

    protected String destName;
    protected OnlineCustomerBankListResult.BankList bankList;

    /**
     * Gets the value of the destName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestName() {
        return destName;
    }

    /**
     * Sets the value of the destName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestName(String value) {
        this.destName = value;
    }

    /**
     * Gets the value of the bankList property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineCustomerBankListResult.BankList }
     *     
     */
    public OnlineCustomerBankListResult.BankList getBankList() {
        return bankList;
    }

    /**
     * Sets the value of the bankList property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineCustomerBankListResult.BankList }
     *     
     */
    public void setBankList(OnlineCustomerBankListResult.BankList value) {
        this.bankList = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="bank" type="{http://ws.lookupdb.mgw.cwt.ru/}onlineCustomerBank" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "bank"
    })
    public static class BankList {

        protected List<OnlineCustomerBank> bank;

        /**
         * Gets the value of the bank property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the bank property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBank().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OnlineCustomerBank }
         * 
         * 
         */
        public List<OnlineCustomerBank> getBank() {
            if (bank == null) {
                bank = new ArrayList<OnlineCustomerBank>();
            }
            return this.bank;
        }

    }

}
