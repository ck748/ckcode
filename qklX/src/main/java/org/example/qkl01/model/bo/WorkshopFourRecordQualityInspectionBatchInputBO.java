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
public class WorkshopFourRecordQualityInspectionBatchInputBO {
  private String sn;

  private BigInteger qualifiedCount;

  private BigInteger scratchCount;

  private BigInteger crackCount;

  private String inspectionTime;

  private String inspector;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(qualifiedCount);
    args.add(scratchCount);
    args.add(crackCount);
    args.add(inspectionTime);
    args.add(inspector);
    return args;
  }
}
