package com.aw.theArtOfJavaConcurrencyProgramming.chapter04;

/**
 * 测试
 */
public class Main {
    public static void main(String[] args) throws Exception {
        SimpleHttpServer server = new SimpleHttpServer();
        server.setPort(9090);
        server.setBasePath(".\\src\\main\\java\\com\\aw\\theArtOfJavaConcurrencyProgramming\\chapter04");
        server.start();
    }
}
