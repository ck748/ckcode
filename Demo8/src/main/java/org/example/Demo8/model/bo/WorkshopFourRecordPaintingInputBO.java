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
public class WorkshopFourRecordPaintingInputBO {
  private String sn;

  private String paintThickness;

  private String sprayPressure;

  private String paintBatch;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    args.add(paintThickness);
    args.add(sprayPressure);
    args.add(paintBatch);
    return args;
  }
}
