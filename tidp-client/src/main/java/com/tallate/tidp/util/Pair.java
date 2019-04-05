package com.tallate.tidp.util;

import com.tallate.tidp.IdpKey;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pair implements Serializable {

  private IdpKey idpKey;
  private Integer count;

}
