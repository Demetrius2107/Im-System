import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import proto.Message;
import utils.ByteBufToMessageUtils;

import java.util.List;

/**
 * @author: Elon
 * @title: MessageDecoder
 * @projectName: im-system
 * @description: TODO
 * @date: 2025/1/24 21:17
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 28){
            return ;
        }

        Message message = ByteBufToMessageUtils.transition(byteBuf);
        if(message == null){
            return;
        }

        list.add(message);

    }
}
