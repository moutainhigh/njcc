package com.yixun.system.service;

import java.util.Date;

public abstract interface BookkeepingService
{
  public abstract int book(long paramLong1, long paramLong2, int paramInt, double paramDouble, String paramString, Date paramDate);
}