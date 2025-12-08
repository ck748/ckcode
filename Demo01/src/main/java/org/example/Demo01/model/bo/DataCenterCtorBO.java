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
public class DataCenterCtorBO {
  private String _raw;

  private String _ws1;

  private String _ws2;

  private String _ws3;

  private String _ws4;

  private String _ws5;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_raw);
    args.add(_ws1);
    args.add(_ws2);
    args.add(_ws3);
    args.add(_ws4);
    args.add(_ws5);
    return args;
  }
}
