package com.example;

import com.example.log.KeyLogHelper;

public class KeyLogHelperTest {
    public static void main(String[] args) {
        testMethodA();
    }

    private static void testMethodA() {
        testMethodB();
    }

    private static void testMethodB() {
        KeyLogHelper.log("AAAAAAAA");
    }
}
