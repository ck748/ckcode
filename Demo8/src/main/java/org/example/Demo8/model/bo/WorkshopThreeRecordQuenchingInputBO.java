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
public class WorkshopThreeRecordQuenchingInputBO {
  private String sn;

  private String quenchingTemp;

  private String coolingMedium;

  private String hardness;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(quenchingTemp);
    args.add(coolingMedium);
    args.add(hardness);
    return args;
  }
}
