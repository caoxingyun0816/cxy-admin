package com.imooc.utils;

import com.imooc.entity.XmlParse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by caoxingyun on 2018/8/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XmlUtilTest {

    @Autowired
    private XmlUtil xmlUtil;

    @Test
    public void parseXMlTest(){
        String xml = "<SyncContentsResult serialNo=\"201807261532589005124\" TimeStamp=\"2018-07-26 15:12:27\">\n" +
                "  <Assets>\n" +
                "    <Asset ID=\"1244029\" currentID=\"6666000143\" type=\"Image\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"1244030\" currentID=\"6666000144\" type=\"Image\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"1244031\" currentID=\"6666000145\" type=\"Image\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"1244032\" currentID=\"6666000146\" type=\"Image\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"5000426905\" currentID=\"6666000147\" type=\"Video\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"5000426906\" currentID=\"6666000148\" type=\"Video\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "    <Asset ID=\"9500130909\" currentID=\"6666000149\" type=\"Movie\" op=\"0\" result=\"0\" desc=\"SUCC\" />\n" +
                "  </Assets>\n" +
                "</SyncContentsResult>";
        XmlParse xmlParse = (XmlParse) xmlUtil.parseAsset(xml);
        System.out.println(xmlParse.getTimeStamp());
    }
}