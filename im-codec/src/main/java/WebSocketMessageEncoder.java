import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import proto.MessagePack;

import java.util.List;

/**
 * @author: Elon
 * @title: WebSocketMessageEncoder
 * @projectName: im-system
 * @description: TODO
 * @date: 2025/1/24 21:18
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessagePack messagePack, List<Object> list) throws Exception {


        try {
            String s = JSONObject.toJSONString(messagePack);
            ByteBuf byteBuf = Unpooled.directBuffer( 8 + s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(messagePack.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
