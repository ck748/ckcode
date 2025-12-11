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
public class WorkshopTwoRecordTurningInputBO {
  private String sn;

  private String rotationSpeed;

  private String feedRate;

  private String tolerance;

  private String toolType;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(rotationSpeed);
    args.add(feedRate);
    args.add(tolerance);
    args.add(toolType);
    return args;
  }
}
