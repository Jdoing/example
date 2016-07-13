package transport;

import example.soa.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rpc2.RpcConsumer;
import rpc2.RpcProvider;

/**
 * Created by juemingzi on 16/5/26.
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {
//    private ChannelHandler channelHandler;
//
//    public NettyHandler(ChannelHandler channelHandler){
//        this.channelHandler = channelHandler;
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
//        System.out.println("Server received: " + msg);
//        ByteBuf in = (ByteBuf) msg;
//        try {
//            while (in.isReadable()) { // (1)
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
//        } finally {
//            ReferenceCountUtil.release(msg); // (2)
//        }
//        Object object = msg;

        Channel channel = ctx.channel();

        if (object instanceof Request) {
            Request request = (Request) object;

            Invocation invocation = request.getInvocation();

            Invoker invoker = RpcProvider.getInvoker(invocation.getClazz().getSimpleName());

            Result result = invoker.invoke(invocation);

            Response response = new Response();
            response.setMsgId(request.getMsgId());
            response.setResult(result.getData());

            channel.writeAndFlush(response);
        } else if (object instanceof Response) {
            Response response = (Response) object;
            RpcConsumer.receive(response);

        } else {
            throw new IllegalArgumentException("object类型出错: " + object.toString());
        }
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
