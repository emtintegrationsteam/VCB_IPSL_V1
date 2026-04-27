
package ru.cwt.mgw.lookupdb.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.cwt.mgw.lookupdb.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DeleteCustomer_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "deleteCustomer");
    private final static QName _GetCustomerBankList_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "getCustomerBankList");
    private final static QName _RegisterCustomer_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "registerCustomer");
    private final static QName _LookupDbFault_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "LookupDbFault");
    private final static QName _SetDefaultAccountResponse_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "setDefaultAccountResponse");
    private final static QName _UpdateCustomer_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "updateCustomer");
    private final static QName _UpdateCustomerResponse_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "updateCustomerResponse");
    private final static QName _GetCustomerBankListResponse_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "getCustomerBankListResponse");
    private final static QName _DeleteCustomerResponse_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "deleteCustomerResponse");
    private final static QName _SetDefaultAccount_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "setDefaultAccount");
    private final static QName _RegisterCustomerResponse_QNAME = new QName("http://ws.lookupdb.mgw.cwt.ru/", "registerCustomerResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.cwt.mgw.lookupdb.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnlineCustomerBankListResult }
     * 
     */
    public OnlineCustomerBankListResult createOnlineCustomerBankListResult() {
        return new OnlineCustomerBankListResult();
    }

    /**
     * Create an instance of {@link RegisterCustomerResponse }
     * 
     */
    public RegisterCustomerResponse createRegisterCustomerResponse() {
        return new RegisterCustomerResponse();
    }

    /**
     * Create an instance of {@link GetCustomerBankListResponse }
     * 
     */
    public GetCustomerBankListResponse createGetCustomerBankListResponse() {
        return new GetCustomerBankListResponse();
    }

    /**
     * Create an instance of {@link UpdateCustomerResponse }
     * 
     */
    public UpdateCustomerResponse createUpdateCustomerResponse() {
        return new UpdateCustomerResponse();
    }

    /**
     * Create an instance of {@link SetDefaultAccount }
     * 
     */
    public SetDefaultAccount createSetDefaultAccount() {
        return new SetDefaultAccount();
    }

    /**
     * Create an instance of {@link DeleteCustomerResponse }
     * 
     */
    public DeleteCustomerResponse createDeleteCustomerResponse() {
        return new DeleteCustomerResponse();
    }

    /**
     * Create an instance of {@link LookupDbFault }
     * 
     */
    public LookupDbFault createLookupDbFault() {
        return new LookupDbFault();
    }

    /**
     * Create an instance of {@link RegisterCustomer }
     * 
     */
    public RegisterCustomer createRegisterCustomer() {
        return new RegisterCustomer();
    }

    /**
     * Create an instance of {@link UpdateCustomer }
     * 
     */
    public UpdateCustomer createUpdateCustomer() {
        return new UpdateCustomer();
    }

    /**
     * Create an instance of {@link SetDefaultAccountResponse }
     * 
     */
    public SetDefaultAccountResponse createSetDefaultAccountResponse() {
        return new SetDefaultAccountResponse();
    }

    /**
     * Create an instance of {@link DeleteCustomer }
     * 
     */
    public DeleteCustomer createDeleteCustomer() {
        return new DeleteCustomer();
    }

    /**
     * Create an instance of {@link GetCustomerBankList }
     * 
     */
    public GetCustomerBankList createGetCustomerBankList() {
        return new GetCustomerBankList();
    }

    /**
     * Create an instance of {@link OnlineResult }
     * 
     */
    public OnlineResult createOnlineResult() {
        return new OnlineResult();
    }

    /**
     * Create an instance of {@link OnlineRequest }
     * 
     */
    public OnlineRequest createOnlineRequest() {
        return new OnlineRequest();
    }

    /**
     * Create an instance of {@link OnlineCustomerBankListRequest }
     * 
     */
    public OnlineCustomerBankListRequest createOnlineCustomerBankListRequest() {
        return new OnlineCustomerBankListRequest();
    }

    /**
     * Create an instance of {@link OnlineCustomerBank }
     * 
     */
    public OnlineCustomerBank createOnlineCustomerBank() {
        return new OnlineCustomerBank();
    }

    /**
     * Create an instance of {@link OnlineCustomerBankListResult.BankList }
     * 
     */
    public OnlineCustomerBankListResult.BankList createOnlineCustomerBankListResultBankList() {
        return new OnlineCustomerBankListResult.BankList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCustomer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "deleteCustomer")
    public JAXBElement<DeleteCustomer> createDeleteCustomer(DeleteCustomer value) {
        return new JAXBElement<DeleteCustomer>(_DeleteCustomer_QNAME, DeleteCustomer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomerBankList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "getCustomerBankList")
    public JAXBElement<GetCustomerBankList> createGetCustomerBankList(GetCustomerBankList value) {
        return new JAXBElement<GetCustomerBankList>(_GetCustomerBankList_QNAME, GetCustomerBankList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterCustomer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "registerCustomer")
    public JAXBElement<RegisterCustomer> createRegisterCustomer(RegisterCustomer value) {
        return new JAXBElement<RegisterCustomer>(_RegisterCustomer_QNAME, RegisterCustomer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupDbFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "LookupDbFault")
    public JAXBElement<LookupDbFault> createLookupDbFault(LookupDbFault value) {
        return new JAXBElement<LookupDbFault>(_LookupDbFault_QNAME, LookupDbFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDefaultAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "setDefaultAccountResponse")
    public JAXBElement<SetDefaultAccountResponse> createSetDefaultAccountResponse(SetDefaultAccountResponse value) {
        return new JAXBElement<SetDefaultAccountResponse>(_SetDefaultAccountResponse_QNAME, SetDefaultAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCustomer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "updateCustomer")
    public JAXBElement<UpdateCustomer> createUpdateCustomer(UpdateCustomer value) {
        return new JAXBElement<UpdateCustomer>(_UpdateCustomer_QNAME, UpdateCustomer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCustomerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "updateCustomerResponse")
    public JAXBElement<UpdateCustomerResponse> createUpdateCustomerResponse(UpdateCustomerResponse value) {
        return new JAXBElement<UpdateCustomerResponse>(_UpdateCustomerResponse_QNAME, UpdateCustomerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomerBankListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "getCustomerBankListResponse")
    public JAXBElement<GetCustomerBankListResponse> createGetCustomerBankListResponse(GetCustomerBankListResponse value) {
        return new JAXBElement<GetCustomerBankListResponse>(_GetCustomerBankListResponse_QNAME, GetCustomerBankListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCustomerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "deleteCustomerResponse")
    public JAXBElement<DeleteCustomerResponse> createDeleteCustomerResponse(DeleteCustomerResponse value) {
        return new JAXBElement<DeleteCustomerResponse>(_DeleteCustomerResponse_QNAME, DeleteCustomerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDefaultAccount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "setDefaultAccount")
    public JAXBElement<SetDefaultAccount> createSetDefaultAccount(SetDefaultAccount value) {
        return new JAXBElement<SetDefaultAccount>(_SetDefaultAccount_QNAME, SetDefaultAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterCustomerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lookupdb.mgw.cwt.ru/", name = "registerCustomerResponse")
    public JAXBElement<RegisterCustomerResponse> createRegisterCustomerResponse(RegisterCustomerResponse value) {
        return new JAXBElement<RegisterCustomerResponse>(_RegisterCustomerResponse_QNAME, RegisterCustomerResponse.class, null, value);
    }

}
