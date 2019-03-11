package com.tallate.tidp.util;

import com.tallate.tidp.IdpKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hgc
 * @date 1/27/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair implements Serializable {

  private IdpKey idpKey;
  private Integer count;

  @Override
  public String toString() {
    return "Pair{" +
        "idpKey=" + idpKey +
        ", count=" + count +
        '}';
  }
}
