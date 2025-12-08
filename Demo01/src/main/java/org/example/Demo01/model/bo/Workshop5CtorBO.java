package org.example.Demo01.model.bo;

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
public class Workshop5CtorBO {
  private String _workshop4Addr;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_workshop4Addr);
    return args;
  }
}
