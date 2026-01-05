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
public class WorkshopFourRecordQualityInspectionSingleInputBO {
  private String sn;

  private BigInteger qualityLevel;

  private String inspectionTime;

  private String inspector;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(qualityLevel);
    args.add(inspectionTime);
    args.add(inspector);
    return args;
  }
}
