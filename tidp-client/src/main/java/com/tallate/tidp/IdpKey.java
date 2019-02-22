package com.tallate.tidp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tallate
 * @date 1/19/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class IdpKey implements Serializable {

  /**
   * 唯一标识一条 key
   */
  private String id;

  /**
   * key 的状态
   */
  private KeyState keyState;

  /**
   * 创建时间
   */
  private Date createdTime;

  /**
   * Response 序列化后的内容
   */
  private byte[] content;

}
