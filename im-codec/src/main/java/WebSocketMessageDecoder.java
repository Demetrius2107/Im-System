import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import proto.Message;
import utils.ByteBufToMessageUtils;

import java.util.List;

/**
 * @author: Elon
 * @title: WebSocketMessageDecoder
 * @projectName: im-system
 * @description: websocket消息解码
 * @date: 2025/1/24 21:18
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame, List<Object> list) throws Exception {
        ByteBuf content = binaryWebSocketFrame.content();
        if (content.readableBytes() < 28) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if (message == null) {
            return;
        }
        list.add(message);

    }
}
