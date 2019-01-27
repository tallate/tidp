package com.tallate.sidp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author tallate
 * @date 1/19/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class IdpKey implements Serializable {

  private String id;

  private KeyState keyState;

  public IdpKey(String id, String keyStateStr) {
    this.id = id;
    this.keyState = Enum.valueOf(KeyState.class, keyStateStr);
  }

  @Override
  public String toString() {
    return "IdpKey{" +
        "id='" + id + '\'' +
        ", keyState=" + keyState +
        '}';
  }
}
