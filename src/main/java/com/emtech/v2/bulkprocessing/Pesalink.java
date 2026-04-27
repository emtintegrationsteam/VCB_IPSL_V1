package com.emtech.v2.bulkprocessing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Pesalink {
  private Long id;
  private String transactionCode;
  private String debitAccount;
  private String recipientType;
  private String recipientAccount;
  private String beneficiaryBankCode;
  private String narration;
  private String recipientName ;
  private Double amount;
  private String createdOn;
  private String responseTime;
  private String status;
  private String ipslResponse;
  private String OriginalMessageId = "-";
  private String messageId = "-";
  private String endToEndId="-";
}
