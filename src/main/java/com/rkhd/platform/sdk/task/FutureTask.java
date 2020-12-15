package com.rkhd.platform.sdk.task;

import com.rkhd.platform.sdk.exception.ScriptBusinessException;

public interface FutureTask {
  void execute(String paramString) throws ScriptBusinessException;
}