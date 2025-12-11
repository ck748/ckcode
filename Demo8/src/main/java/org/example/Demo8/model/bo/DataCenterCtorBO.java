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
public class DataCenterCtorBO {
  private String _workshopOneAddr;

  private String _workshopTwoAddr;

  private String _workshopThreeAddr;

  private String _workshopFourAddr;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_workshopOneAddr);
    args.add(_workshopTwoAddr);
    args.add(_workshopThreeAddr);
    args.add(_workshopFourAddr);
    return args;
  }
}
