package com.lxw.personal.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 2019/7/16/0016
 * Create by 刘仙伟
 */
public class BaseDistributedLock implements Lock {

    private ZkClient zkClient;
    private String basePath;
    private String path;
    private String lockName;
    private int MAX_RETRY_COUNT=10;


    public BaseDistributedLock(ZkClient zkClient,String path,String lockName){
        this.zkClient=zkClient;
        this.basePath=path;
        this.path=path.concat("/").concat(lockName);
        this.lockName=lockName;
    }

    private boolean waitToLock(long startMills,Long millsToWait,String  ourPath){
        boolean hasTheLock=false;
        boolean doDelete = false;
        try {
            while (!hasTheLock) {
                //该方法实现获取locker节点下的所有顺序节点，并且从小到大排序
                List<String> children = getSortedChildren();
                String sequenceNodeName = ourPath.substring(basePath.length()+1);
                //计算刚才客户端创建的顺序节点在locker的所有子节点中排序位置，如果是排序为0，则表示获取到了锁
                int ourIndex = children.indexOf(sequenceNodeName);
             /*如果在getSortedChildren中没有找到之前创建的[临时]顺序节点，这表示可能由于网络闪断而导致
      *Zookeeper认为连接断开而删除了我们创建的节点，此时需要抛出异常，让上一级去处理
      *上一级的做法是捕获该异常，并且执行重试指定的次数 见后面的 attemptLock方法  */
                if (ourIndex < 0) {
                    throw new ZkNoNodeException("节点没有找到: " + sequenceNodeName);
                }
                //如果当前客户端创建的节点在locker子节点列表中位置大于0，表示其它客户端已经获取了锁
                //此时当前客户端需要等待其它客户端释放锁，
                hasTheLock = ourIndex == 0;
                //如何判断其它客户端是否已经释放了锁？从子节点列表中获取到比自己次小的哪个节点，并对其建立监听
                String pathToWatch = hasTheLock ? null : children.get(ourIndex - 1);
                if (!hasTheLock) {
                    //如果次小的节点被删除了，则表示当前客户端的节点应该是最小的了，所以使用CountDownLatch来实现等待
                    String previousSequencePath = basePath.concat("/").concat(pathToWatch);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    IZkDataListener previousListener = new IZkDataListener() {
                        //次小节点删除事件发生时，让countDownLatch结束等待
                        //此时还需要重新让程序回到while，重新判断一次！
                        @Override
                        public void handleDataChange(String s, Object o) throws Exception {

                        }

                        @Override
                        public void handleDataDeleted(String s) throws Exception {
                            countDownLatch.countDown();
                        }
                    };
                    try {
                        zkClient.subscribeDataChanges(previousSequencePath, previousListener);
                        if (millsToWait != null) {
                            millsToWait -= (System.currentTimeMillis() - startMills);
                            startMills = System.currentTimeMillis();
                            if (millsToWait > 0L) {
                                countDownLatch.await(millsToWait, TimeUnit.MICROSECONDS);
                            } else {
                                doDelete = true;//time out
                                break;
                            }
                        } else {
                            countDownLatch.await();
                        }
                    }catch (ZkNoNodeException e){
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        zkClient.unsubscribeDataChanges(previousSequencePath,previousListener);
                    }
                }
            }
        }catch (Exception e){
            //发生异常删除节点
            doDelete=true;
            throw e;
        }finally {
            if (doDelete) {
                this.deleteOurPath(ourPath);
            }
        }
        return hasTheLock;
    }
    private void deleteOurPath(String ourPath){
        try {
            this.zkClient.delete(ourPath);
        } catch (ZkException e) {
        }

    }

    public static String standardFixForSorting(String str, String lockName) {
        int index = str.lastIndexOf(lockName);
        if (index >= 0) {
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        } else {
            return str;
        }
    }
    private List<String> getSortedChildren(){
        List<String> children=zkClient.getChildren(basePath);
        List<String> sortedList = new ArrayList<String>(children);
        Collections.sort(sortedList, (o1,o2)->{
            return standardFixForSorting(o1,lockName).compareTo(standardFixForSorting(o2,lockName));
        });
        return sortedList;
    }


    /**
     *
     */
    @Override
    public void lock() {
        tryLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock() {
        return  tryLock(-1L, (TimeUnit)null);
    }

    /**
     * 尝试获取锁
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit){
        return attemptLock(time,unit)!=null?true:false;
    }

    public String attemptLock(long time, TimeUnit unit){
        long startMillis = System.currentTimeMillis();
        Long millisToWait = unit != null ? unit.toMillis(time) : null;
        int retryCount = 0;
        String ourPath = null;
        boolean hasTheLock = false;
        boolean isDone = false;
        while(!isDone){
            isDone=true;

            try{
                //createLockNode用于在locks（basePath持久节点）下创建客户端要获取锁的[临时]顺序节点
                ourPath=createsTheLock(zkClient,path);
                /**
                 * 该方法用于判断自己是否获取到了锁，即自己创建的顺序节点在locks的所有子节点中是否最小
                 * 如果没有获取到锁，则等待其它客户端锁的释放，并且稍后重试直到获取到锁或者超时
                 */
                hasTheLock=waitToLock(startMillis,millisToWait,ourPath);

            }catch (ZkNoNodeException e){
                if(retryCount++<MAX_RETRY_COUNT){
                    isDone=false;
                }else{
                    throw e;
                }
            }
        }
        return hasTheLock?ourPath:null;
    }

    /**
     * 创建临时有序节点
     * @param zkClient
     * @param path
     * @return
     */
    private String createsTheLock(ZkClient zkClient,String path){
        return zkClient.createEphemeralSequential(path,null);
    }

    @Override
    public void unlock() {
        deleteOurPath(path);
    }

    public void releaseLock(String lockPath){
        deleteOurPath(lockPath);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
