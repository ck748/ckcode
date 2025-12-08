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
public class RawMaterialSetRawMaterialInputBO {
  private String _rawProducerAddr;

  private String _axleProducerAddr;

  private BigInteger _totalRawWeight;

  private String _batchId;

  public List<Object> toArgs() {
    List args = new ArrayList();
    args.add(_rawProducerAddr);
    args.add(_axleProducerAddr);
    args.add(_totalRawWeight);
    args.add(_batchId);
    return args;
  }
}
