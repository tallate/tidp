package com.tallate.tidp.idpchecker.blocking;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.MethodSignatureWrapper;
import com.tallate.tidp.Msgs;
import com.tallate.tidp.idpchecker.BaseIdpChecker;
import com.tallate.tidp.idpchecker.RejectException;
import com.tallate.tidp.spring.DebugConfig;
import com.tallate.tidp.spring.DebugConfig.Mode;
import com.tallate.tidp.util.KeyGenUtil;
import com.tallate.tidp.util.Pair;
import com.tallate.tidp.util.StringJoinerUtil;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * 支持阻塞，当上一个请求还在执行中时，IdpKey的状态为EXECUTING，这个状态可以当作乐观锁
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class BlockingIdpChecker extends BaseIdpChecker {

  Set<KeyState> rejectStateSet;

  Set<KeyState> passStateSet;

  public BlockingIdpChecker() {
    rejectStateSet = new HashSet<>();
    rejectStateSet.add(KeyState.SUCCESS);
    passStateSet = new HashSet<>();
  }

  private static final int INIT_RETRY_INTERVAL_TIME = 50;
  private static final int MAX_RETRY_COUNT = 3;

  @Override
  public Object onCheck(MethodSignatureWrapper target)
      throws Throwable {
    String id = keyProvider.get();
    IdpKey newK = KeyGenUtil.newExecuting(id);
    IdpKey oldK = null;
    int intervalTime = INIT_RETRY_INTERVAL_TIME;
    int retryCount = 0;
    while (retryCount++ < MAX_RETRY_COUNT) {
      Pair pair = keyStore.putIfAbsentOrInStates(newK, passStateSet);
      if (DebugConfig.getMode() == Mode.DEBUG) {
        log.info("putIfAbsentOrInStates返回: " + pair);
      }
      oldK = pair.getIdpKey();
      Integer updatedCount = pair.getCount();
      if (KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 1) {
        // 没有其他线程执行，且这时保存成功
        break;
      } else if (passStateSet.contains(oldK.getKeyState())) {
        // 存在其他线程执行失败，且这时保存成功，说明命中了可以继续执行的状态
        break;
      } else if (rejectStateSet.contains(oldK.getKeyState())) {
        // 其他线程执行成功，且这时保存失败，将旧结果直接返回
        if (DebugConfig.getMode() == Mode.DEBUG) {
          log.info(Msgs.IDP_MULTI_INVOKE, oldK);
        }
        return KeyGenUtil.deserialize(oldK.getContent(), target.getReturnType());
      }
      // 还有一种情况，其他线程执行中，需要阻塞当前线程、重试固定次数，此时KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 0
      Thread.sleep(intervalTime);
      intervalTime *= 2;
      if (DebugConfig.getMode() == Mode.DEBUG) {
        log.info(StringJoinerUtil.join(">> 重试 idpKey=", newK.toString(), " count=",
            Integer.toString(retryCount)));
      }
    }
    if (retryCount >= MAX_RETRY_COUNT) {
      // 重试间隔时间超过阈值
      throw new RejectException(Msgs.IDP_RETRYLIMIT_EXCEPTION, oldK);
    }
    // 调用目标方法
    Object res = target.invoke();
    // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
    IdpKey idpKey = KeyGenUtil.newSuccess(keyProvider.get(), res);
    if (DebugConfig.getMode() == Mode.DEBUG) {
      log.info("replace预存最终结果：" + idpKey);
    }
    keyStore.replace(idpKey);
    return res;
  }

}
