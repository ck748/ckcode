package org.example.Demo01.model.bo;

import java.lang.Object;
import java.lang.String;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workshop3SetDataInputBO {
  private BigInteger _unqualified;

  private String _manager;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_unqualified);
    args.add(_manager);
    return args;
  }
}
