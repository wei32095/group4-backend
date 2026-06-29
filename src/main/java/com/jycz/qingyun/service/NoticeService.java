package com.jycz.qingyun.service;

import com.jycz.qingyun.model.dto.ApiResult;
import com.jycz.qingyun.model.vo.NoticeListVO;

public interface NoticeService {
    ApiResult<NoticeListVO> getNotices(Long userId, int page, int size);
}
