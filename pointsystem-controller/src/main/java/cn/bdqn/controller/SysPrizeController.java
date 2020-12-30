package cn.bdqn.controller;

import cn.bdqn.pointsystem.emtitys.Prize;
import cn.bdqn.pointsystem.service.PrizeService;
import cn.bdqn.pointsystem.utlis.FileUpload;
import cn.bdqn.pointsystem.utlis.ReturnResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname sysPrizeController  商品信息
 * @Description 注释
 * @Date 2020/1/6 12:02
 * @Created by zhaomengliang
 */
@Controller
@RequestMapping("/sys/prize")
public class SysPrizeController {

    @Autowired
    private PrizeService prizeService;

    @ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String ho(){
        return "prize";
    }
    @ResponseBody
    @GetMapping("/list")
    public List<Prize> list(){ return prizeService.selectPrize(); }
    @ResponseBody
    @GetMapping("/insert")
    public boolean insertPrinze(Prize prize){
        return prizeService.insertPrize(prize);
    }
    @ResponseBody
    @GetMapping("/selectone")
    public  Prize selectOne(int id){ return  prizeService.selectPrizeOne(id); }


    @GetMapping("/getByIdPrize")
    public String getByIdPrize(int prizeId, Model model){
        Prize prize = prizeService.selectPrizeOne(prizeId);
        model.addAttribute("prize",prize);
        return "admin/getByIdPrize";
    }

    @GetMapping("/updatePrize")
    @ResponseBody
    public Object updatePrize(Prize prize){
        boolean flag = prizeService.updatePrize(prize);
        Map<Object,Object> map=new HashMap<>();
        map.put("flag",flag);
        return map;
    }

    /**
     * 删除商品信息
     * @param list
     * @return
     */
    @GetMapping("/delPrize")
    @ResponseBody
    public Object delPrize(@RequestParam int[] list) {
        for (int i = 0; i < list.length; i++) {
            try{
                Prize prize = prizeService.selectPrizeOne(list[i]);//根据商品编号查询商品信息
                delFile(FileUpload.getpath().substring(5)+prize.getImgUrl());//删除图片
                int a = prizeService.deletePrize(list[i]);//删除商品
                if (a == 0) {//如果等0 false
                    return "false";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "true";
    }

    /**
     * 添加页面
     * @return
     */
    @GetMapping("/in")
    public String i(){
        return "/admin/insertprize";
    }

    /**
     * 根据奖品id获取奖品对象
     * ReturnResult.returnFail("","",500)查询异常
     * ReturnResult.returnFail("","",505);奖品数量为空
     * ReturnResult.returnSuccess("",prize);查询成功返回
     * @param prizeId 奖品id
     * @return
     */
    @ResponseBody
    @GetMapping("/getPrizeById")
    public Map<String,Object> getPrizeById(@RequestParam Integer prizeId){
        Map<String,Object> map = null;
        Prize prize = prizeService.selectPrizeOne(prizeId);
        if (null==prize){
            map = ReturnResult.returnFail("","",500);
        }else {
            map = ReturnResult.returnSuccess("",prize);
        }
        return map;
    }


    /**
     * 获取系统路径（文件存放位置）
     */
    @Value("${value.systemPath}")
    private String systemPath;

    /**
     * 增加商品
     * @param file  前台上传图片
     * @param prize 商品对象
     * @return
     */
    @RequestMapping("/pictureUpload")
    @ResponseBody
    public Map<String,Object> fileUpload(MultipartFile file,Prize prize){//必须使用MultipartFile类型来接收图片
        prize.setId(0);
        if (prizeService.selectPrizeOneName(prize.getName())<1){
            System.out.println(FileUpload.getpath().substring(6)+"==="+systemPath);
            prize.setImgUrl(FileUpload.getFileURL(file,systemPath,FileUpload.getpath().substring(6)));
            prizeService.insertPrize(prize);
            return ReturnResult.returnSuccess("操作完成","");
        }else {
            return ReturnResult.returnFail("操作失败","","500");
        }

    }
    /**删除文件
     * @param path 目录
     */
    public void delFile (String path) {
        File file=new File(path);
        if(file.exists() && file.isFile())
            file.delete();
    }


    /**
     * 商品修改
     * @param file
     * @param id
     * @param img 未修改前图片路径
     * @return
     */
    @RequestMapping("/updatePrize")
    @ResponseBody
    public Map<String,Object> upPrize(Prize prize
            ,@RequestParam(value="file",required=false) MultipartFile file
            ,@RequestParam("id") Integer id
            ,@RequestParam("img") String img
    ){
        Map<String,Object> map=new HashMap<>();
        prize.setId(id);
        if (file!=null){//根据文件名判断文件是否为空
            System.out.println("存储地址================="+FileUpload.getpath().substring(5)+systemPath);
            prize.setImgUrl(FileUpload.getFileURL(file,systemPath,FileUpload.getpath().substring(6)));
            delFile(FileUpload.getpath().substring(5)+img);
            if(prizeService.updatePrize(prize))  {
                map= ReturnResult.returnSuccess("操作完成","");
            }else {
               map=ReturnResult.returnFail("操作失败","","500");
            }
        }else{
            prize.setImgUrl(img);
            if(prizeService.updatePrize(prize))  {
                map= ReturnResult.returnSuccess("操作完成","");
            }else {
                map=ReturnResult.returnFail("操作失败","","500");
            }
        }
        return map;
    }
}
