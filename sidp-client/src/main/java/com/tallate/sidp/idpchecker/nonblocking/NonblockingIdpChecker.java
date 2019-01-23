package com.tallate.sidp.idpchecker.nonblocking;

import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.idpchecker.BaseIdpChecker;
import com.tallate.sidp.idpchecker.RejectException;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.store.KeyStoreException;
import com.tallate.sidp.util.StringJoinerUtil;
import org.javatuples.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * 非阻塞，即使别的线程正在执行调用（EXECUTING），也直接拦截
 *
 * @author tallate
 * @date 1/20/19
 */
public abstract class NonblockingIdpChecker extends BaseIdpChecker {

  Set<KeyState> rejectStateSet;

  public NonblockingIdpChecker() {
    rejectStateSet = new HashSet<>();
    rejectStateSet.add(KeyState.SUCCESS);
  }

  @Override
  public void preCheck() throws RejectException, KeyGenException, KeyStoreException {
    String id = keyProvider.get();
    IdpKey newK = new IdpKey()
        .setId(id)
        .setKeyState(KeyState.EXECUTING);
    Pair<IdpKey, Integer> pair = keyStore.putIfAbsent(newK);
    IdpKey oldK = pair.getValue0();
    if (rejectStateSet.contains(oldK.getKeyState())) {
      throw new RejectException(StringJoinerUtil.join(ExceptionMsgs.IDP_REJECT_EXCEPTION, ":", oldK.toString()));
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
  public void onException(Throwable cause) throws KeyGenException, KeyStoreException {
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