package so.sao.shop.gpssocket.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import so.sao.shop.gpssocket.Dto.CoordinateDto;
import so.sao.shop.gpssocket.Dto.MessageDto;
import so.sao.shop.gpssocket.Interface.iBodyUtils;
import so.sao.shop.gpssocket.Interface.iService;
import so.sao.shop.gpssocket.Utils.CodeUtils;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * GPS 定位数据包
 * @author negocat on 2017/10/30.
 */
@Service("0x10")
public class GPSLocationData implements iService {

    private Logger LOGGER = LoggerFactory.getLogger(GPSLocationData.class);

    @Override
    public void doService(iBodyUtils ctx, MessageDto messageDto) {
        byte[] bb = null;

        byte[] bytes = messageDto.getBody();
        // 日期时间  6byte
        int year = 2000 + bytes[0];//年
        byte month = bytes[1];//月
        byte day = bytes[2];//日
        byte hour = bytes[3];//时
        byte minute = bytes[4];//分
        byte second = bytes[5];//秒
        // GPS 数据长度，可见卫星个数：数据长度和可见卫星数各占 0.5byte
        byte GPSLength = bytes[6];//GPS 信息长度
        byte satelliteNum = bytes[7];//卫星个数
        // GPS 经纬度 经度纬度各占 4byte
        byte[] latitude = Arrays.copyOfRange(bytes, 7, 11);
        CoordinateDto latitudeDto = analyzeCoordinate(latitude);//经度
        byte[] longitude = Arrays.copyOfRange(bytes, 11, 15);
        CoordinateDto longitudeDto = analyzeCoordinate(longitude);//维度
        // GPS 速度：占用 1byte，表示 GPS 的运行速度，范围为 0x00-0xFF 公里/小时
        int speed = bytes[15];//GPS 速度
        // 南北纬，东西经，状态，航向：占用 2 个字节，航向表示 GPS 的运行方向，0-360，单位度，正北为 0。
        // 第一个字节 8 位二进制中前六位表示状态，后两位与第二字节的 8 位表示航向
        byte[] type = Arrays.copyOfRange(bytes, 16, 18);
        String typeStr = CodeUtils.bytesToBinaryString(type);//转二进制字符串
        // 0 南纬 1 北纬 0 东经 1 西经 0 GPS 不定位 1 GPS 定位
        char location = typeStr.charAt(3);//GPS是否定位
        char la = typeStr.charAt(4);//东西经
        char lo = typeStr.charAt(5);//南北纬

        int course = new BigInteger(typeStr.substring(6), 2).intValue();//航向

        System.out.println(year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);
        System.out.println("GPS 信息长度:"+GPSLength+"  卫星个数:"+satelliteNum);
        System.out.println("经度:"+latitudeDto);
        System.out.println("维度:"+longitudeDto);
        System.out.println("GPS 速度:"+speed);
        System.out.println("GPS是否定位:"+location+"  东西经:"+la+"  南北纬:"+lo+"  航向:"+course);

        ctx.writeEncode(CodeUtils.toDate(), (byte) 10);
    }

    /**
     * 经纬度解析
     */
    public CoordinateDto analyzeCoordinate(byte[] b){
        int val = CodeUtils.bytesToInt2(b, 0);

        double dblVal = val / 30000.0;
        int intVal = (int) (dblVal / 60);
        float fltVal = (float) (dblVal - intVal * 60);

        return new CoordinateDto(intVal, fltVal);
    }
}
