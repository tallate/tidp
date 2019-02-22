package com.tallate.tidp.idpchecker.nonblocking;

import com.tallate.tidp.KeyState;
import com.tallate.tidp.idpchecker.nonblocking.NonblockingIdpChecker;

/**
 * EXECUTING状态也会继续执行
 *
 * @author tallate
 * @date 1/19/19
 */
public class FastPassIdpChecker extends NonblockingIdpChecker {

  public FastPassIdpChecker() {
    rejectStateSet.add(KeyState.SUCCESS);
  }
}
