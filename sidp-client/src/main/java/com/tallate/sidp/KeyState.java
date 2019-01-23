package com.tallate.sidp;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KeyState {

  /**
   * 目标未执行（不应该出现）
   */
  NONE(0),
  /**
   * 目标得到执行并成功
   */
  SUCCESS(1),
  /**
   * 目标执行中（如果）
   */
  EXECUTING(2),
  /**
   * 目标执行失败且可再次执行（抛出Exception）
   */
  FAIL(3),
  /**
   * 目标执行失败且不可再次执行（抛出RuntimeException或Error）
   */
  RUNTIME_FAIL(4);

  int value;



}
