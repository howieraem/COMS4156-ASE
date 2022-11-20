package com.lgtm.easymoney.services;


import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.AnalyticRsp;

/**
 * Analytic Service Interface.
 */
public interface AnalyticService {
  AnalyticRsp getAnalytic(User u);
}
