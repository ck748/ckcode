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
public class WorkshopFourRecordPackingInputBO {
  private String sn;

  private String packTime;

  private String packOperator;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(packTime);
    args.add(packOperator);
    return args;
  }
}
