package com.yupi.usercenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

public class ImportExcel {
    public static void main(String[] args) {

        String fileName = "D:\\项目\\user-center-backend-master\\src\\main\\resources\\testExcel.xlsx";
        // 读取全部sheet
        // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
        sycRead(fileName);

    }

    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, UseInfo.class, new TableListener()).sheet().doRead();
    }

    public static void sycRead(String fileName) {
        List<UseInfo> useInfos = EasyExcel.read(fileName).head(UseInfo.class).sheet().doReadSync();
        for (UseInfo useInfo :useInfos) {
            System.out.println(useInfo);
        }

    }

}
