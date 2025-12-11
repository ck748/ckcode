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
public class DataCenterBatchCheckStatusInputBO {
  private List<String> snList;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(snList);
    return args;
  }
}
