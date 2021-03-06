package com.foameraserblue.tmp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    // 클라이언트의 역할
    public Integer calcSum(String filePath) throws IOException {

        // 콜백 객체를 익명 내부 객체를 통해 생성하여 전달한다.
        LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return Integer.parseInt(line) + value;
            }
        };

        return lineReadTemplate(filePath, sumCallback, 0);
    }

    public Integer calcMultiply(String filePath) throws IOException {
        LineCallback<Integer> multiplyCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return Integer.parseInt(line) * value;
            }
        };

        return lineReadTemplate(filePath, multiplyCallback, 1);
    }

    // 문자열을 길게 만들어 반환한다.
    public String concatenate(String filePath) throws IOException {
        LineCallback<String> concatenateCallback = new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return line + value;
            }
        };
        return lineReadTemplate(filePath, concatenateCallback, "");
    }

    // 버퍼 리더를 만들고 콜백에 전달해주는 구조적인 일을 하는 템플릿
    public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            return callback.doSomethingWithReader(br);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());

                }
            }
        }

    }

    // 라인을 이용하는 콜백을 사용하는 템플릿
    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initValue) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = initValue;
            String line = null;

            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }

            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());

                }
            }
        }
    }
}
