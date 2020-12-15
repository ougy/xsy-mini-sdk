 package com.rkhd.platform.sdk.task;

 import java.util.List;

 public class BatchJobPrepare {
   private String sql;
   private String apiKey;
   private List<Long> ids;
   private boolean isAdmin;

   private BatchJobPrepare(String sql, String apiKey, List<Long> ids, boolean isAdmin) {
     this.sql = sql;
     this.apiKey = apiKey;
     this.ids = ids;
     this.isAdmin = isAdmin;
   }

   public static class Builder {
     private String sql;
     private String apiKey;
     private List<Long> ids;
     private boolean isAdmin;

     public Builder(String sql) {
       this.sql = sql;
     }

     public Builder(String apiKey, List<Long> ids) {
       this.apiKey = apiKey;
       this.ids = ids;
     }

     public Builder isAdmin(boolean isAdmin) {
       this.isAdmin = isAdmin;
       return this;
     }

     public BatchJobPrepare build() {
       return new BatchJobPrepare(this.sql, this.apiKey, this.ids, this.isAdmin);
     }
   }
 }