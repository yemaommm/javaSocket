package so.sao.shop.gpssocket.Interface;

import so.sao.shop.gpssocket.Dto.MessageDto;

/**
 * @author negocat on 2017/10/30.
 */
public interface iService {

    void doService(iBodyUtils ctx, MessageDto messageDto);
}
