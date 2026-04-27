package com.emtech.v2.bulkprocessing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BulkCallback {
  private String messageId;
  private String originalMessageId;
  private String status;
  private String endToEndId;
  private String recipientName="NA";
  private String senderName="NA";
  private String remarks;
}
