package org.example.Demo8.model.bo;

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
public class WorkshopOneRecordForgingInputBO {
  private String sn;

  private String forgingTemp;

  private String pressure;

  private String holdTime;

  private String defect;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(forgingTemp);
    args.add(pressure);
    args.add(holdTime);
    args.add(defect);
    return args;
  }
}
