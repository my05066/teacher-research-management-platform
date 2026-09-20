package com.tit.university.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tit.university.mapper.ProgramMapper;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Program;
import com.tit.university.pojo.ProgramQueryParam;
import com.tit.university.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgramServiceImpl implements ProgramService {


    @Autowired
    private ProgramMapper programMapper;

    @Override
    public PageResult<Program> page(ProgramQueryParam param, Long userId, String status) {
        PageHelper.startPage(param.getCurrentPage(), param.getPageSize());
        Page<Program> pg = (Page<Program>) programMapper.list(userId, status);
        return new PageResult<>(pg.getTotal(), pg.getResult());
    }

    @Override
    public void add(Program program) {
        if (program.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        program.setStatus("待审核");

        programMapper.insert(program);
    }





    @Override
    public void audit(Long id, String status, Long auditUserId, String auditRemark) {
        Program prog = programMapper.selectById(id);
        if (prog == null) return;
        prog.setStatus(status);
        prog.setAuditUserId(auditUserId);
        prog.setAuditTime(LocalDateTime.now());
        prog.setAuditRemark(auditRemark);
        programMapper.updateAudit(prog);
    }


    @Override
    public void submitCompletionDoc(Long id, String completionDocPath, String completionDocName, Long completionDocSize) {
        Program prog = programMapper.selectById(id);
        if (prog == null) return;

        prog.setCompletionDocPath(completionDocPath);
        prog.setCompletionDocName(completionDocName);
        prog.setCompletionDocSize(completionDocSize);
        prog.setIsCompleted(1);
        prog.setStatus("待审核");
        programMapper.updateCompletionDoc(prog);
    }

    @Override
    public void delete(Long id) {
        programMapper.deleteById(id);
    }
}
