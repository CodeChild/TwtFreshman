package com.example.zhangyulong.myapplication;

import java.net.URL;
import java.util.List;

/**
 * Created by zhangyulong on 16-10-15.
 */

public class Artical{
    private String message;
    private int erro_code;
    private List<Data> data;
    public int getErro_code() {
        return erro_code;
    }

    public String getMessage() {
        return message;
    }
    public List<Data> getData(){
        return data;
    }

    public static class Data{
        private int index;
        private String subject;
        private String pic;
        private int visitcount;
        private int comments;
        private String summary;

        public void setComments(int comments) {
            this.comments = comments;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getSubject() {
            return subject;
        }

        public int getIndex() {
            return index;
        }

        public String getPic() {
            return pic;
        }


        public int getVisitcount() {
            return visitcount;
        }

        public int getComments() {
            return comments;
        }

        public String getSummary() {
            return summary;
        }
    }
}