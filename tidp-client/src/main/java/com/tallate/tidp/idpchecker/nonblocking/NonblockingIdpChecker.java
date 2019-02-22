package com.tallate.tidp.idpchecker.nonblocking;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.MethodSignatureWrapper;
import com.tallate.tidp.Msgs;
import com.tallate.tidp.idpchecker.BaseIdpChecker;
import com.tallate.tidp.idpchecker.RejectException;
import com.tallate.tidp.util.KeyGenUtil;
import com.tallate.tidp.util.Pair;
import com.tallate.tidp.util.StringJoinerUtil;

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
  public Object onCheck(MethodSignatureWrapper target) throws Throwable {
    String id = keyProvider.get();
    IdpKey newK = KeyGenUtil.newExecuting(id);
    Pair pair = keyStore.putIfAbsent(newK);
    IdpKey oldK = pair.getIdpKey();
    if (rejectStateSet.contains(oldK.getKeyState())) {
      throw new RejectException(StringJoinerUtil
          .join(Msgs.IDP_REJECT_EXCEPTION, ":", oldK.toString()));
    }
    // 调用目标方法
    Object res = target.invoke();
    // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
    IdpKey idpKey = KeyGenUtil.newSuccess(keyProvider.get(), res);
    keyStore.replace(idpKey);
    return res;
  }

}
