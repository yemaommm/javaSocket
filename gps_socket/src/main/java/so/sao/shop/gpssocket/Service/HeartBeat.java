package so.sao.shop.gpssocket.Service;

import org.springframework.stereotype.Service;
import so.sao.shop.gpssocket.Dto.MessageDto;
import so.sao.shop.gpssocket.Interface.iBodyUtils;
import so.sao.shop.gpssocket.Interface.iService;

/**
 * 心跳包
 * @author negocat on 2017/10/30.
 */
@Service("0x08")
public class HeartBeat implements iService {
    @Override
    public void doService(iBodyUtils ctx, MessageDto messageDto) {

    }
}
