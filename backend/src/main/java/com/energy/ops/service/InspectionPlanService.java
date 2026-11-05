package com.energy.ops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.energy.ops.common.DictText;
import com.energy.ops.common.PageResult;
import com.energy.ops.mapper.InspectionPlanMapper;
import com.energy.ops.vo.InspectionPlanVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InspectionPlanService {

    private final InspectionPlanMapper inspectionPlanMapper;

    public PageResult<InspectionPlanVO> page(long page, long size, Long deviceId) {
        IPage<InspectionPlanVO> result = inspectionPlanMapper.selectPlanPage(
                new Page<>(page, size), deviceId);
        result.getRecords().forEach(vo -> vo.setStatusText(DictText.planStatus(vo.getStatus())));
        return new PageResult<>(result.getTotal(), result.getRecords(), page, size);
    }
}
