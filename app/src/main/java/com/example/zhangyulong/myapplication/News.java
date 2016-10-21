package com.example.zhangyulong.myapplication;

import java.util.List;

/**
 * Created by zhangyulong on 16-10-19.
 */

public class News {
    private String message;
    private int erro_code;
    private Data data;
    public int getErro_code() {
        return erro_code;
    }

    public String getMessage() {
        return message;
    }
    public Data getData(){
        return data;
    }
    public static class Data{
        private String subject;
        private int index;
        private String content;
        private String newscome;
        private String gonggao;
        private String shengao;
        private String sheying;

        public int getIndex() {
            return index;
        }

        public String getContent() {
            return content;
        }

        public String getGonggao() {
            return gonggao;
        }

        public String getNewscome() {
            return newscome;
        }

        public String getShengao() {
            return shengao;
        }

        public String getSheying() {
            return sheying;
        }

        public String getSubject() {
            return subject;
        }

    }
}
