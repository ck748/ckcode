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
public class DataCenterGetPressingDataInputBO {
  private String sn;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(sn);
    return args;
  }
}
