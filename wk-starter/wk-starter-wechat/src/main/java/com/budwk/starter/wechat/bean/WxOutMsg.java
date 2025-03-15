package com.budwk.starter.wechat.bean;

import java.util.List;

import org.nutz.lang.util.NutMap;

@SuppressWarnings({"serial", "unchecked"})
public class WxOutMsg extends NutMap {
    
    protected transient String _raw;
    
    public WxOutMsg() {}
    
    public WxOutMsg(String msgType) {
        setMsgType(msgType);
    }

    public WxOutMsg setFromUserName(String fromUserName){
        put("fromUserName", fromUserName);
        return this;
    }

    public String getFromUserName(){
        return (String)get("fromUserName");
    }

    public WxOutMsg setToUserName(String ToUserName){
        put("ToUserName", ToUserName);
        return this;
    }

    public String getToUserName(){
        return (String)get("ToUserName");
    }

    public WxOutMsg setMsgType(String msgType){
        put("msgType", msgType);
        return this;
    }

    public String getMsgType(){
        return (String)get("msgType");
    }

    public WxOutMsg setContent(String content){
        put("content", content);
        return this;
    }

    public String getContent(){
        return (String)get("content");
    }

    public WxOutMsg setCreateTime(long createTime){
        put("createTime", createTime);
        return this;
    }

    public long getCreateTime(){
        return getLong("createTime", 0);
    }

    public WxOutMsg setImage(WxImage image){
        put("image", image);
        return this;
    }

    public WxImage getImage(){
        return (WxImage)get("image");
    }

    public WxOutMsg setVoice(WxVoice voice){
        put("voice", voice);
        return this;
    }

    public WxVoice getVoice(){
        return (WxVoice)get("voice");
    }

    public WxOutMsg setVideo(WxVideo video){
        put("video", video);
        return this;
    }

    public WxVideo getVideo(){
        return (WxVideo)get("video");
    }

    public WxOutMsg setMusic(WxMusic music){
        put("music", music);
        return this;
    }

    public WxMusic getMusic(){
        return (WxMusic)get("music");
    }

    public WxOutMsg setArticles(List<WxArticle> articles){
        put("articles", articles);
        return this;
    }

    public List<WxArticle> getArticles(){
        return (List<WxArticle>)get("articles");
    }

    public WxOutMsg setMedia_id(String media_id){
        put("media_id", media_id);
        return this;
    }

    public String getMedia_id(){
        return (String)get("media_id");
    }

    public WxOutMsg setKfAccount(WxKfAccount kfAccount){
        put("kfAccount", kfAccount);
        return this;
    }

    public WxKfAccount getKfAccount(){
        return (WxKfAccount)get("kfAccount");
    }

    public WxOutMsg setCard(WxCard card){
        put("card", card);
        return this;
    }

    public WxCard getCard(){
        return (WxCard)get("card");
    }
    
    public String raw() {
        return _raw;
    }
    
    public void raw(String raw) {
        this._raw= raw;
    }
}
