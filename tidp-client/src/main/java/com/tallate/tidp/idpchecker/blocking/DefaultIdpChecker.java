package com.tallate.tidp.idpchecker.blocking;

import com.tallate.tidp.KeyState;

/**
 * EXECUTING阻塞，FAIL放行，其他拒绝
 *
 * @author tallate
 * @date 1/19/19
 */
public class DefaultIdpChecker extends BlockingIdpChecker {

    public DefaultIdpChecker() {
        rejectStateSet.add(KeyState.SUCCESS);
        rejectStateSet.add(KeyState.RUNTIME_FAIL);
        passStateSet.add(KeyState.FAIL);
    }
}
