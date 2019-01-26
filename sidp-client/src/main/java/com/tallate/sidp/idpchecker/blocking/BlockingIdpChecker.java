package com.tallate.sidp.idpchecker.blocking;

import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.idpchecker.BaseIdpChecker;
import com.tallate.sidp.idpchecker.RejectException;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.keystore.KeyStoreException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

/**
 * 支持阻塞，当上一个请求还在执行中时，IdpKey的状态为EXECUTING，这个状态可以当作乐观锁
 *
 * @author tallate
 * @date 1/20/19
 */
@Slf4j
public abstract class BlockingIdpChecker extends BaseIdpChecker {

    Set<KeyState> rejectStateSet;

    Set<KeyState> passStateSet;

    public BlockingIdpChecker() {
        rejectStateSet = new HashSet<>();
        rejectStateSet.add(KeyState.SUCCESS);
        passStateSet = new HashSet<>();
    }

    private static final int INIT_RETRY_INTERVAL_TIME = 50;
    private static final int MAX_RETRY_INTERVAL_TIME = 200;

    @Override
    public void preCheck()
            throws RejectException, KeyGenException, KeyStoreException, InterruptedException {
        String id = keyProvider.get();
        IdpKey newK = new IdpKey()
                .setId(id)
                .setKeyState(KeyState.EXECUTING);
        IdpKey oldK;
        int intervalTime = INIT_RETRY_INTERVAL_TIME;
        while (true) {
            Pair<IdpKey, Integer> pair = keyStore.putIfAbsentOrInStates(newK, passStateSet);
            oldK = pair.getValue0();
            Integer updatedCount = pair.getValue1();
            if (KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 1) {
                // 没有其他线程执行，保存成功
                break;
            } else if (passStateSet.contains(oldK.getKeyState())) {
                // 存在其他线程执行失败，保存成功
                break;
            } else if (rejectStateSet.contains(oldK.getKeyState())) {
                // 其他线程执行成功，保存失败
                throw new RejectException(ExceptionMsgs.IDP_REJECT_EXCEPTION, oldK);
            }
            // 其他线程执行中：KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 0
            Thread.sleep(intervalTime);
            intervalTime *= 2;
            if (intervalTime > MAX_RETRY_INTERVAL_TIME) {
                // 重试间隔时间超过阈值
                throw new RejectException(ExceptionMsgs.IDP_RETRYLIMIT_EXCEPTION, oldK);
            }
        }
    }

    @Override
    public void postCheck() throws KeyStoreException, KeyGenException {
        IdpKey idpKey = new IdpKey()
                .setId(keyProvider.get())
                .setKeyState(KeyState.SUCCESS);
        keyStore.replace(idpKey);
    }
}
