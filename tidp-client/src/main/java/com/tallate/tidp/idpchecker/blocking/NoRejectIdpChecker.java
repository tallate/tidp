package com.tallate.tidp.idpchecker.blocking;

import com.tallate.tidp.KeyState;

/**
 * 不拒绝
 * 1. 跟踪idpKey的状态直到EXECUTING状态结束
 * 2. 判断若不是SUCCESS则继续执行
 *
 * @author tallate
 * @date 1/19/19
 */
public class NoRejectIdpChecker extends BlockingIdpChecker {

    public NoRejectIdpChecker() {
        rejectStateSet.add(KeyState.SUCCESS);
        passStateSet.add(KeyState.FAIL);
        passStateSet.add(KeyState.RUNTIME_FAIL);
    }

}
