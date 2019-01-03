package com.erge.mulchannel;

import org.junit.Test;

import java.util.zip.ZipFile;

public class MulChannelTest {

    @Test
    public void test1() throws Exception {
        LeRandomAccessFile laf = new LeRandomAccessFile("test/3", "rw");
        long len = 2048;
        laf.setLength(0);
        laf.setLength(len);
        MulChannel.EOCD eocd = new MulChannel.EOCD();
        eocd.comment = "360渠道";
        laf.seek(len - eocd.size());
        laf.writeInt(eocd.signature);
        laf.writeShort(eocd.diskNum);
        laf.writeShort(eocd.cdStarts);
        laf.writeShort(eocd.cdNum);
        laf.writeShort(eocd.cdTotal);
        laf.writeInt(eocd.cdSize);
        laf.writeInt(eocd.cdOffset);
        laf.writeUTF(eocd.comment);
        laf.close();
    }

    @Test
    public void testGetComment() throws Exception {
        MulChannel mc = new MulChannel("test/2.apk");
        System.out.println(mc.getComment());
    }

    @Test
    public void testWriteComment() throws Exception {
        MulChannel mc = new MulChannel("test/3");
//        MulChannel.EOCD eocd = new MulChannel.EOCD();
//        eocd.comment = "{\"channel\":\"guanwang\"}";
//        mc.writeEocd(eocd);
        mc.writeComment("{\"channel\":\"guanwang渠道\"}");
    }

    @Test
    public void testApkAddchannel() throws Exception {
        MulChannel mc = new MulChannel("test/0.apk", "test/2.apk");
        mc.writeComment("official_test");
    }

    @Test
    public void testZipComment() throws Exception {
        System.out.println(new ZipFile("test/360zhushou.apk").getComment());
    }

    @Test
    public void testMulChannelPacket() throws Exception {
        String channels[] = {
                "360zhushou",
                "4399_you_xi_he",
                "4399_local",
                "91zhushou",
                "anzhi",
                "androidshichang",
                "baiduzhushou",
                "bubugao",
                "chuizishouji",
                "erge_wap",
                "huawei",
                "jifeng",
                "lenovo",
                "meizu",
                "oppoyingyong",
                "ppzhushou",
                "sanxingshichang",
                "sougouzhushou",
                "vivoyingyong",
                "xiaohaJQR",
                "xiaomi",
                "yingyongbao",
                "yiyonghui",
        };

        long start = System.currentTimeMillis();
        for (String channel : channels) {
            MulChannel mc = new MulChannel("test/0.apk", "test/" + channel + ".apk");
            mc.writeComment(channel);
        }
        System.out.printf("渠道包%d个, 打包耗时%.3fs", channels.length, (System.currentTimeMillis()-start)/1000f);
    }

}