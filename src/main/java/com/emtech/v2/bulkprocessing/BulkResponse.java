package com.emtech.v2.bulkprocessing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BulkResponse {
  private String messageId;
  private String status;
  private String message;
  private String endToEndId;
}
