package com.rkhd.platform.sdk.task;

import com.rkhd.platform.sdk.model.XObject;
import com.rkhd.platform.sdk.exception.BatchJobException;

import java.util.List;

public interface BatchJob {
  BatchJobPrepare prepare() throws BatchJobException;
  
  void execute(List<XObject> paramList, String paramString);
  
  void finish(String paramString);
}