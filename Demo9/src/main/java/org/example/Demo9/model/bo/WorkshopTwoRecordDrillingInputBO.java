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
public class WorkshopTwoRecordDrillingInputBO {
  private String sn;

  private String holeSize;

  private String holeDepth;

  private String equipmentSpeed;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(holeSize);
    args.add(holeDepth);
    args.add(equipmentSpeed);
    return args;
  }
}
