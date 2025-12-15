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
public class WorkshopThreeRecordStraighteningInputBO {
  private String sn;

  private String straighteningForce;

  private String temperingTemp;

  private String holdTime;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(straighteningForce);
    args.add(temperingTemp);
    args.add(holdTime);
    return args;
  }
}
