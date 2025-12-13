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
public class WorkshopTwoRecordGearProcessingInputBO {
  private String sn;

  private String gearAccuracy;

  private String surfaceHardness;

  private String equipmentNo;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(gearAccuracy);
    args.add(surfaceHardness);
    args.add(equipmentNo);
    return args;
  }
}
