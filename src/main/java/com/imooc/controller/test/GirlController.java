package com.imooc.controller.test;

import com.imooc.domain.Girl;
import com.imooc.domain.Result;
import com.imooc.repository.GirlRepository;
import com.imooc.service.girl.GirlService;
import com.imooc.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by caoxingyun
 * 2018-07-18 23:15
 */
@Api(value = "Girl模块")
@RestController
public class GirlController {

    private final static Logger logger = LoggerFactory.getLogger(GirlController.class);

    @Autowired
    private GirlRepository girlRepository;

    @Autowired
    private GirlService girlService;

    /**
     * 查询所有女生列表
     * @return
     */
    @ApiOperation(value = "获取所有用户", notes = "获取所有用户")
    @GetMapping(value = "/girls")
    public List<Girl> girlList() {
        logger.info("girlList");
        int page = 1;
        int size = 5;
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(page,size,sort);
        girlRepository.findAll(pageable);



        return girlRepository.findAll();
    }

    /**
     * 添加一个女生
     * @return
     */
    @ApiOperation(value = "添加一个女生", notes = "传入id添加一个女生")
    @PostMapping(value = "/girls")
    public Result<Girl> girlAdd(@ApiParam(value = "女生实体",required = true) @Valid Girl girl, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(1, bindingResult.getFieldError().getDefaultMessage());
        }

        girl.setCupSize(girl.getCupSize());
        girl.setAge(girl.getAge());

        return ResultUtil.success(girlRepository.save(girl));
    }

    //查询一个女生
    @ApiOperation(value = "查询一个女生",notes = "根据id查询女生")
    @GetMapping(value = "/girls/{id}")
    public Girl girlFindOne(@ApiParam(value = "女生ID",required = true) @PathVariable("id") Integer id,Model model) {;
        return girlRepository.findOne(id);

    }

    //更新
    @ApiOperation(value = "更新一个女生信息",notes = "根据id更新女生信息")
    @PutMapping(value = "/girls/{id}")
    public Girl girlUpdate(@ApiParam(value = "女生ID",required = true) @PathVariable("id") Integer id,
                           @RequestParam("cupSize") String cupSize,
                           @RequestParam("age") Integer age,
                           @RequestParam("money") Double money) {
        Girl girl = new Girl();
        girl.setId(id);
        girl.setCupSize(cupSize);
        girl.setAge(age);
        girl.setMoney(money);
        return girlRepository.save(girl);
    }

    //删除
    @ApiOperation(value = "删除一个女生信息",notes = "根据id删除女生信息")
    @DeleteMapping(value = "/girls/{id}")
    public void girlDelete(@ApiParam(value = "女生ID",required = true) @PathVariable("id") Integer id) {
        girlRepository.delete(id);
    }

    //通过年龄查询女生列表
    @ApiOperation(value = "通过年龄查询女生列表",notes = "根据age查询女生信息")
    @GetMapping(value = "/girls/age/{age}")
    public List<Girl> girlListByAge(@ApiParam(value = "女生年龄",required = true) @PathVariable("age") Integer age) {
        return girlRepository.findByAge(age);
    }

    @PostMapping(value = "/girls/two")
    public void girlTwo() {
        girlService.insertTwo();
    }

    @GetMapping(value = "girls/getAge/{id}")
    public void getAge(@PathVariable("id") Integer id) throws Exception {
        girlService.getAge(id);
    }
}
