package org.example.Demo9.model.bo;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopTwoRecordHeatTreatmentInputBO {
  private String sn;

  private String heatingTemp;

  private String holdTime;

  private String coolingRate;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(heatingTemp);
    args.add(holdTime);
    args.add(coolingRate);
    return args;
  }
}
