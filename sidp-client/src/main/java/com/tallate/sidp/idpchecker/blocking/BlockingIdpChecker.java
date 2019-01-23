package com.tallate.sidp.idpchecker.blocking;

import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.idpchecker.BaseIdpChecker;
import com.tallate.sidp.idpchecker.RejectException;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.store.KeyStoreException;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

import java.util.HashSet;
import java.util.Set;

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

  @Override
  public void preCheck() throws RejectException, KeyGenException, KeyStoreException, InterruptedException {
    String id = keyProvider.get();
    IdpKey newK = new IdpKey()
        .setId(id)
        .setKeyState(KeyState.EXECUTING);
    IdpKey oldK;
    int intervalTime = 50;
    int count = 3;
    while (count-- > 0) {
      Pair<IdpKey, Integer> pair = keyStore.putIfAbsentOrInStates(newK, passStateSet);
      oldK = pair.getValue0();
      Integer updatedCount = pair.getValue1();
      if (KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 1) {
        break;
      } else if (passStateSet.contains(oldK.getKeyState())) {
        break;
      } else if (rejectStateSet.contains(oldK.getKeyState())) {
        throw new RejectException(ExceptionMsgs.IDP_REJECT_EXCEPTION, oldK);
      } else if (count == 0) {
        throw new RejectException(ExceptionMsgs.IDP_RETRYLIMIT_EXCEPTION, oldK);
      }
      // KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 0
      Thread.sleep(intervalTime);
      intervalTime *= 2;
    }
  }

  @Override
  public void postCheck() throws KeyStoreException, KeyGenException {
    IdpKey idpKey = new IdpKey()
        .setId(keyProvider.get())
        .setKeyState(KeyState.SUCCESS);
    keyStore.replace(idpKey);
  }

  @Override
  public void onException(Throwable cause) throws KeyStoreException, KeyGenException {
    IdpKey idpKey = new IdpKey()
        .setId(keyProvider.get());
    if (RuntimeException.class.isAssignableFrom(cause.getClass())) {
      idpKey.setKeyState(KeyState.RUNTIME_FAIL);
      keyStore.replace(idpKey);
    } else if (Exception.class.isAssignableFrom(cause.getClass())) {
      idpKey.setKeyState(KeyState.FAIL);
      keyStore.replace(idpKey);
    }
    // TODO 应该不会有别的情况，Error还没碰到过，但是有必要额外注意吗？
  }
}
