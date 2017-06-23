package com.yixun.system.service.impl;

import com.yixun.system.service.BookkeepingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class BookkeepingServiceImpl
  implements BookkeepingService
{
  public int book(long payerAcctCode, long payeeAcctCode, int payServiceCode, double amount, String dealId, Date PostDate)
  {
    return 0;
  }
}