package com.tahauddin.syed.springbatch08;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTests {

    @Test
    void calcHash() throws NoSuchAlgorithmException {
        String name = "Syed Tahauddin";
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(name.getBytes(StandardCharsets.UTF_8));
        System.out.println("Original Text is :: " + name);
        System.out.println("Hash Text is :: " + new String(digest));
    }

    @Test
    void googleGuavaHash(){
        String name = "Syed Tahauddin132";

        String string = Hashing.sha256()
                .hashString(name, StandardCharsets.UTF_8)
                .toString();
        // 5d4d4b1f0dbd98440413f8619aa2cfbdbae574328835eba80bd3b7f1460d4800
        System.out.println("Original Text is :: " + name);
        System.out.println("Hash Text is :: " + string);

    }




}
