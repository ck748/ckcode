package org.example.qkl01.model.bo;

import java.lang.Object;
import java.lang.String;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialCreateMaterialBatchInputBO {
  private String batchId;

  private String manufacturer;

  private String shaftBatch;

  private String sn;

  private BigInteger totalQuantity;

  private String destination;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(batchId);
    args.add(manufacturer);
    args.add(shaftBatch);
    args.add(sn);
    args.add(totalQuantity);
    args.add(destination);
    return args;
  }
}
