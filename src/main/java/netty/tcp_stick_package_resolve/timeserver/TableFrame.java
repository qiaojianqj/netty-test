package netty.tcp_stick_package_resolve.timeserver;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 */

public class TableFrame {

    private ChannelHandlerContext channelHandlerContext;

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    private Map<Integer, ScheduledFuture> timerMap = new HashMap<Integer, ScheduledFuture> ();

    public boolean SetGameTimerWhileTrue() {
        this.channelHandlerContext.executor ().scheduleWithFixedDelay ( () -> {
             {
                try {
                    Thread.sleep ( 10 );
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                System.out.println ( "qiaojian while true " + this.channelHandlerContext + Thread.currentThread ().getName () + ";"
                + Thread.currentThread ().getId ());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS );
        return true;
    }

    public boolean SetGameTimer(final int dwTimerID, long dwElapse, int dwRepeat, final int wBindParam) {
        KillGameTimer(dwTimerID);
        ScheduledFuture future = this.channelHandlerContext.executor ().scheduleWithFixedDelay ( new Runnable() {
            public void run() {
                try{
                    onEventTimer(dwTimerID, wBindParam);
                }catch (Throwable t){
                    System.out.println ("游戏定时任务失败" + t);
                }
            }
        },dwElapse, dwElapse, TimeUnit.MILLISECONDS);
        timerMap.put(dwTimerID, future);

        return true;
    }

    //删除定时器
    public boolean KillGameTimer(int dwTimerID) {
        ScheduledFuture future = timerMap.get(dwTimerID);
        if(future != null && !future.isCancelled() && future.isDone()){
            future.cancel(true);
        }
        timerMap.remove(dwTimerID);
        return true;

    }

    public void onEventTimer(int dwTimerID, int wBIndParam) {
        System.out.println ( new Date (  ) + " ; " + this.channelHandlerContext + "; timerId: " + dwTimerID  + "; " + Thread.currentThread ().getName () + ";"
                + Thread.currentThread ().getId ());
    }

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool (2);
        executor.schedule(  () -> {
            for (;;)
             {
                try {
                    Thread.sleep ( 100 );
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                System.out.println ( "while true loop in thread " + Thread.currentThread ().getName () );
            }
        },  1000, TimeUnit.MILLISECONDS);

        executor.scheduleWithFixedDelay ( () -> {
            System.out.println ( "do once in thread " + Thread.currentThread ().getName () );
        }, 0, 1000, TimeUnit.MILLISECONDS );

        System.out.println ( "main thread " + Thread.currentThread ().getName () + " stop" );
    }
}
