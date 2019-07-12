package com.personal.rpc;

import com.personal.rpc.registry.RPCRegistry;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        new RPCRegistry(8082).start();
    }
}
