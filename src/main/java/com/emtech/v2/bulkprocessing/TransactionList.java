package com.emtech.v2.bulkprocessing;

import com.emtech.v2.bulkprocessing.generated.CdtTrfTxInf;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TransactionList")
public class TransactionList {
  @XmlElement(name = "CdtTrfTxInf")
  private List<CdtTrfTxInf> transactions;
  
  public List<CdtTrfTxInf> getTransactions() {
    return this.transactions;
  }
}
